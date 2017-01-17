package com.cylan.jfgappdemo.ui;

import android.app.Activity;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.cylan.entity.jniCall.JFGDevice;
import com.cylan.entity.jniCall.JFGMsgVideoDisconn;
import com.cylan.entity.jniCall.JFGMsgVideoResolution;
import com.cylan.entity.jniCall.RobotMsg;
import com.cylan.ex.JfgException;
import com.cylan.jfgapp.jni.JfgAppCmd;
import com.cylan.jfgappdemo.R;
import com.cylan.jfgappdemo.databinding.ActivityMulVideoBinding;
import com.cylan.jfgappdemo.datamodel.PlayMultVideoData;
import com.cylan.utils.JfgMsgPackUtils;
import com.superlog.SLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.webrtc.videoengine.ViERenderer;

import java.util.ArrayList;


/**
 * Created by tim on 17-1-13.
 */

public class MultVideoActivity extends Activity {

    JFGDevice device;
    int len = 4;
    RelativeLayout root[] = new RelativeLayout[4];
    SurfaceView[] sv = new SurfaceView[len];
    ViewSwitcher vs[] = new ViewSwitcher[len];
    ImageView iv[] = new ImageView[len];
    int[] camId = new int[len];
    int[] ssrc = new int[len];
    boolean[] isplay = new boolean[len];
    ActivityMulVideoBinding binding;
    boolean flag;
    public ArrayList<String> tag = new ArrayList<>();
    int sn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mul_video);
        binding = DataBindingUtil.setContentView(MultVideoActivity.this, R.layout.activity_mul_video);
        device = (JFGDevice) getIntent().getSerializableExtra("device");
        init();
        EventBus.getDefault().register(this);
    }

    public int getSn() {
        return ++sn;
    }

    public void init() {
        tag.add(device.uuid);
        vs[0] = binding.vs1;
        vs[1] = binding.vs2;
        vs[2] = binding.vs3;
        vs[3] = binding.vs4;
        iv[0] = binding.ivPlay1;
        iv[1] = binding.ivPlay2;
        iv[2] = binding.ivPlay3;
        iv[3] = binding.ivPlay4;
        root[0] = binding.v1;
        root[1] = binding.v2;
        root[2] = binding.v3;
        root[3] = binding.v4;
        for (int i = 0; i < sv.length; i++) {
            sv[i] = ViERenderer.CreateRenderer(getBaseContext(), true);
            camId[i] = i + 1;
            ssrc[i] = 1000 + i;
            iv[i].setOnClickListener(new PlayListener(i));
            root[i].addView(sv[i], 0);
            root[i].setOnTouchListener(new TouchListener(i));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.tb1.setOnCheckedChangeListener(new CheckedChangeListener(0));
        binding.tb2.setOnCheckedChangeListener(new CheckedChangeListener(1));
        binding.tb3.setOnCheckedChangeListener(new CheckedChangeListener(2));
        binding.tb4.setOnCheckedChangeListener(new CheckedChangeListener(3));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnVideoDisconnect(JFGMsgVideoDisconn msg) {
        flag = false;
        //show play view
//        binding.vsStateView.setVisibility(View.VISIBLE);
//        binding.ivPlay.setImageResource(R.drawable.btn_play);
//        binding.vsStateView.setDisplayedChild(0);

        // 重置所有的view
        SLog.e(msg.remote + " errCode:" + msg.code);
        Toast.makeText(this, "err:" + msg.code, Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnVideoNotifyResolution(JFGMsgVideoResolution msg) throws JfgException {
        //render view
        SLog.i(msg.toString());
        int index = 0;
        for (int i = 0; i < len; i++) {
            if (ssrc[i] == msg.ssrc) {
                index = i;
                isplay[i] = true;
                SLog.e("ssrc index:" + index);
            }
        }
        try {
            JfgAppCmd.getInstance().enableRenderRemoteView(true, msg.ssrc, sv[index]);
        } catch (JfgException e) {
            e.printStackTrace();
        }
        vs[index].setVisibility(View.GONE);
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (flag) {
                JfgAppCmd.getInstance().stopPlay(device.uuid);
            }
        } catch (JfgException e) {
            e.printStackTrace();
        }
    }


    class TouchListener implements View.OnTouchListener {
        int index;

        public TouchListener(int index) {
            this.index = index;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                SLog.i("touch: " + index);
                vs[index].setVisibility(View.VISIBLE);
                vs[index].setDisplayedChild(0);
                vs[index].postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        vs[index].setVisibility(View.GONE);
                    }
                }, 2000);
                return true;
            }
            return false;
        }
    }

    class PlayListener implements View.OnClickListener {
        int index;

        public PlayListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            try {
                boolean tmp = isplay[index];
                if (!tmp) {
                    // play
                    if (!flag) {
                        JfgAppCmd.getInstance().playVideo(device.uuid);
                        flag = true;  // 标志一下打开播放句柄
                    } // 发一个透传消息到设备
                }
                PlayMultVideoData data = new PlayMultVideoData(tmp ? 2 : 1, camId[index], ssrc[index]);
                JfgAppCmd.getInstance().robotTransmitMsg(new RobotMsg(tag, getSn(), false, JfgMsgPackUtils.pack(data)));
                SLog.i(data.toString());
                vs[index].setVisibility(View.VISIBLE);
                vs[index].setDisplayedChild(tmp ? 0 : 1);
                iv[index].setImageResource(tmp ? R.drawable.btn_play : R.drawable.btn_pause);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class CheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        int index;

        public CheckedChangeListener(int index) {
            this.index = index;
        }

        @Override
        public void onCheckedChanged(CompoundButton v, boolean isChecked) {
            if (isChecked) {
                // 放大一个view
                //增加点击放大效果

                RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) root[index].getLayoutParams();
                p.height = Resources.getSystem().getDisplayMetrics().heightPixels;
                p.width = Resources.getSystem().getDisplayMetrics().widthPixels;
                root[index].setLayoutParams(p);

            } else {
                // 缩小回原样
            }
        }
    }
}
