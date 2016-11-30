package com.cylan.jfgappdemo.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.cylan.entity.jniCall.JFGDevice;
import com.cylan.entity.jniCall.JFGMsgVideoDisconn;
import com.cylan.entity.jniCall.JFGMsgVideoResolution;
import com.cylan.entity.jniCall.JFGMsgVideoRtcp;
import com.cylan.jfgapp.jni.JfgAppCmd;
import com.cylan.jfgappdemo.JfgEvent;
import com.cylan.jfgappdemo.R;
import com.cylan.jfgappdemo.databinding.ActivityVrPlayBinding;
import com.cylan.panorama.CameraParam;
import com.cylan.panorama.PanoramicView;
import com.cylan.utils.JfgNetUtils;
import com.cylan.utils.JfgUtils;
import com.superlog.SLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by lxh on 16-8-23.
 */
public class VRPlayActivity extends Activity {

    ActivityVrPlayBinding binding;
    /**
     * The Video view.
     */
    PanoramicView panoramicView;
    JFGDevice device;

    boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr_play);
        device = (JFGDevice) getIntent().getSerializableExtra("device");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vr_play);
        // 改变布局
        //change layout
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) binding.rlVideo.getLayoutParams();
        params.height = Resources.getSystem().getDisplayMetrics().widthPixels;
        binding.rlVideo.setLayoutParams(params);
        panoramicView = new PanoramicView(this);
        panoramicView.configV360(CameraParam.getTopPreset());
        SLog.i("create videView.");
        binding.rlVideo.addView(panoramicView, 0);
    }

    private void changeLayout(boolean landscape) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) binding.rlVideo.getLayoutParams();
        if (landscape) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            params.height = Resources.getSystem().getDisplayMetrics().heightPixels;
        } else {
            params.height = Resources.getSystem().getDisplayMetrics().widthPixels;
        }
        binding.rlVideo.setLayoutParams(params);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        SLog.e("newConfig");
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                changeLayout(true);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                changeLayout(false);
                break;
        }
    }

    /**
     * Play video.
     */
    private void playVideo() {
        // has network ?
        if (JfgNetUtils.getInstance(this).getNetType() == -1) {
            showToast("phone is not netWork or client is offline!");
            return;
        }
        JfgAppCmd.getInstance().playVideo(device.uuid);
        // do time out;
        binding.pbLoading.setVisibility(View.VISIBLE);
        SLog.w("play video : " + device.uuid);
    }

    /**
     * Stop play.
     */
    private void stopPlay() {
        JfgAppCmd.getInstance().stopPlay(device.uuid);
        binding.pbLoading.setVisibility(View.GONE);
        SLog.i("stop play!");
        isPlaying = false;
    }


    @Override
    protected void onStart() {
        super.onStart();
        addListener();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        if (isPlaying) {
            stopPlay();
        }
        JfgAppCmd.getInstance().removeRenderRemoteView();
    }

    private void addListener() {
        binding.tbPlay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    playVideo();
                } else {
                    stopPlay();
                }
            }
        });
        binding.cbTopModel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    panoramicView.setMountMode(PanoramicView.MountMode.TOP);
                } else {
                    panoramicView.setMountMode(PanoramicView.MountMode.WALL);
                }
                reset();
            }
        });
        binding.cbVrModel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                panoramicView.enableVRMode(isChecked);
                setRequestedOrientation(isChecked ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                panoramicView.detectOrientationChange();
                if (isChecked) {
                    binding.cbGyro.setChecked(isChecked);
                }
            }
        });
        binding.cbGyro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                panoramicView.enableGyro(isChecked);
            }
        });

    }


    private void reset() {
        binding.cbGyro.setChecked(false);
        binding.cbVrModel.setChecked(false);

    }


    /**
     * On video disconnect.
     *
     * @param msg the msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnVideoDisconnect(JFGMsgVideoDisconn msg) {
        //show play view
        binding.tvBitRate.setVisibility(View.GONE);
        SLog.i(msg.remote + " errCode:" + msg.code);
        showToast("errCode:" + msg.code);
        isPlaying = false;
        stopPlay();
    }

    /**
     * On video notify resolution.
     *
     * @param msg the msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnVideoNotifyResolution(JFGMsgVideoResolution msg) {
        //render view
        SLog.i("setRenderRemoteView");
        binding.pbLoading.setVisibility(View.GONE);
        binding.tvBitRate.setVisibility(View.VISIBLE);
        JfgAppCmd.getInstance().setRenderRemoteView(panoramicView);
        JfgAppCmd.getInstance().setAudio(false, true, true); // enable remote device mic and voice
        isPlaying = true;
    }

    /**
     * On video notify rtcp.
     *
     * @param msg the msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnVideoNotifyRTCP(JFGMsgVideoRtcp msg) {
        binding.tvBitRate.setText(msg.bitRate / 8 + "kB/s " + JfgUtils.size2String(msg.videoRecved));
        SLog.d("rtcp:bitRate: %s,fram: %d FPS,time: %d, %s", JfgUtils.size2String(msg.bitRate / 8),
                msg.frameRate, msg.timestamp, JfgUtils.size2String(msg.videoRecved));
    }


    /**
     * On line status.
     *
     * @param state the state
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnLineStatus(JfgEvent.OnLineState state) {
        if (!state.online) {
            // off line
            showToast("phone is off line ");
            isPlaying = false;
            stopPlay();
        }
    }


    /**
     * Show toast.
     *
     * @param str the str
     */
    private void showToast(final String str) {
        // run on ui thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(VRPlayActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
