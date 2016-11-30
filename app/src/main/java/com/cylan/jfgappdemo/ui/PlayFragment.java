package com.cylan.jfgappdemo.ui;

import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cylan.entity.jniCall.JFGDevice;
import com.cylan.entity.jniCall.JFGMsgVideoDisconn;
import com.cylan.entity.jniCall.JFGMsgVideoResolution;
import com.cylan.entity.jniCall.JFGMsgVideoRtcp;
import com.cylan.entity.jniCall.JFGVideo;
import com.cylan.jfgapp.interfases.CallBack;
import com.cylan.jfgapp.jni.JfgAppCmd;
import com.cylan.jfgappdemo.JfgEvent;
import com.cylan.jfgappdemo.R;
import com.cylan.jfgappdemo.databinding.FragmentPlayBinding;
import com.cylan.utils.JfgNetUtils;
import com.cylan.utils.JfgUtils;
import com.superlog.SLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.webrtc.videoengine.ViERenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by lxh on 16-7-28.
 */
public class PlayFragment extends BaseFragment {

    /**
     * The Binding.
     */
    FragmentPlayBinding binding;
    /**
     * The Device.
     */
    JFGDevice device;
    /**
     * The Video view.
     */
    SurfaceView videoView;
    /**
     * The Playing.
     */
    boolean playing;
    /**
     * The Real time.
     */
    boolean realTime = true;
    /**
     * The Enable voice.
     */
    boolean enableVoice;
    /**
     * The Enable mic.
     */
    boolean enableMic;
    /**
     * The Curre history time.
     */
    long curreHistoryTime; //当前录像时间。
    /**
     * The Video adapter.
     */
    ArrayAdapter videoAdapter;
    /**
     * The Videos.
     */
    ArrayList<JFGVideo> videos;
    /**
     * The Is ready.
     */
    boolean isReady;


    /**
     * Gets instance.
     *
     * @param bundle the bundle
     * @return the instance
     */
    public static PlayFragment getInstance(Bundle bundle) {
        PlayFragment fragment = new PlayFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_play, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 改变布局
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.rlVideo.getLayoutParams();
        int w = Resources.getSystem().getDisplayMetrics().widthPixels;
        params.height = (int) (w * 0.7f); // 动态设置高度
        binding.rlVideo.setLayoutParams(params);
        device = (JFGDevice) getArguments().getSerializable("device");
        binding.tvCid.setText(device.alias);
        videoView = ViERenderer.CreateRenderer(getContext(), true);
        SLog.i("create videView.");
        binding.rlVideo.addView(videoView, 0);
        videos = new ArrayList<>();
        videoAdapter = new ArrayAdapter<JFGVideo>(getContext(), android.R.layout.simple_list_item_1, videos);
        binding.lvHistoryList.setAdapter(videoAdapter);
        addLinstener();
        JfgAppCmd.getInstance().getVideoList(device.uuid);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        SLog.i("isPlay ? " + playing);
        if (playing) {
            // stop play
            stopPlay();
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (playing) {
            stopPlay();
        }
        JfgAppCmd.getInstance().removeRenderRemoteView();

    }

    /**
     * Add linstener.
     */
    private void addLinstener() {
        binding.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show chat fragmnet .
                Bundle bundle = new Bundle();
                bundle.putSerializable("Device", device);
                ChatroomFragment fragment = ChatroomFragment.getInstance(bundle);
                showOtherFragment(fragment);
            }
        });

        binding.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    stopPlay();
                } else {
                    playVideo();
                }
            }
        });
        binding.ivMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("Device", device);
                MessageFragment fragment = MessageFragment.getInstance(bundle);
                showOtherFragment(fragment);
            }
        });

        binding.ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("Device", device);
                SettingsFragment fragment = SettingsFragment.getInstance(bundle);
                showOtherFragment(fragment);
            }
        });


        binding.ivScreenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePic();
            }
        });

        binding.rlVideo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // show play view;
                    binding.vsStateView.setVisibility(View.VISIBLE);
                    binding.vsStateView.setDisplayedChild(0);
                    binding.ivPlay.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (playing) {
                                binding.vsStateView.setVisibility(View.GONE);
                            }
                        }
                    }, 2000);
                }
                return true;
            }
        });

        binding.tbSpeak.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!playing) return;
                enableMic = isChecked;
                SLog.i("mic:" + enableMic + " , voice:" + enableVoice);
                JfgAppCmd.getInstance().setAudio(true, enableMic, enableVoice); // local
            }
        });

        binding.tbVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!playing) return;
                enableVoice = isChecked;
                SLog.i("mic:" + enableMic + " , voice:" + enableVoice);
                JfgAppCmd.getInstance().setAudio(true, enableMic, enableVoice); // local
            }
        });

        binding.lvHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (realTime) {
                    binding.tbVideo.setChecked(false);
                    return;
                }
                curreHistoryTime = videos.get(position).beginTime;
                if (isReady) {
                    JfgAppCmd.getInstance().playHistoryVideo(device.uuid, curreHistoryTime);
                }
            }
        });

        binding.tbVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SLog.i("isChecked?" + isChecked);
                realTime = isChecked;
                JfgAppCmd.getInstance().switchVideoMode(realTime, curreHistoryTime);
            }
        });

    }

    private void showOtherFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .addToBackStack("play").add(R.id.fl_container, fragment, fragment.getClass().getName())
                .hide(PlayFragment.this).commit();
    }

    /**
     * Play video.
     */
    private void playVideo() {
        // has network ?
        if (JfgNetUtils.getInstance(getContext()).getNetType() == -1) {
            showToast("phone is not netWork or client is offline!");
            return;
        }
        // dev is online ?
//        if (device.base.netType == JfgNetUtils.JFG_NET_OFFLINE) {
//            showToast("Device is OffLine!");
//            return;
//        }
        if (playing) return;
        if (realTime) {
            JfgAppCmd.getInstance().playVideo(device.uuid);
        } else {
            if (curreHistoryTime == 0) curreHistoryTime = videos.get(0).beginTime;
            JfgAppCmd.getInstance().playHistoryVideo(device.uuid, curreHistoryTime);
        }
        // do time out;
        binding.vsStateView.setVisibility(View.VISIBLE);
        binding.vsStateView.setDisplayedChild(1);
        binding.ivPlay.setImageResource(R.drawable.btn_pause);
        SLog.w("play video : " + device.uuid);
        playing = true;
    }


    /**
     * Stop play.
     */
    private void stopPlay() {
        if (!playing) return;
        JfgAppCmd.getInstance().stopPlay(device.uuid);
        binding.vsStateView.setVisibility(View.VISIBLE);
        binding.vsStateView.setDisplayedChild(0);
        binding.ivPlay.setImageResource(R.drawable.btn_play);
        playing = false;
        isReady = false;
    }

    /**
     * Take pic.
     */
    private void takePic() {
        binding.ivScreenshot.setEnabled(false);
        SLog.i("take Pic ");
        binding.ivScreenshot.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.ivScreenshot.setEnabled(true);
            }
        }, 3000);  // delayed 3S

        //   Playing before only screenshot
        if (!playing) {
            showToast("只有播放中才能截图。");
            return;
        }
        // get video pic . false
        JfgAppCmd.getInstance().screenshot(false, new CallBack<Bitmap>() {
            @Override
            public void onSucceed(Bitmap bitmap) {
                savePic(bitmap);
            }

            @Override
            public void onFailure(String string) {
                SLog.e(string);
            }
        });
    }

    /**
     * Save pic.
     *
     * @param bitmap the bitmap
     */
    private void savePic(Bitmap bitmap) {
        long time = System.currentTimeMillis();
        File dir = new File(Environment.getExternalStorageDirectory(), "pic");
        if (!dir.exists()) dir.mkdir();
        File file = new File(dir, "pic_" + time + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmap2Bytes(bitmap));
            fos.flush();
            fos.close();
            SLog.i("" + file.getAbsolutePath());
            showToast(file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            SLog.e(e.getMessage());
        }
    }


    /**
     * Bitmap 2 bytes byte [ ].
     *
     * @param bm the bm
     * @return the byte [ ]
     */
    private byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    /**
     * On video disconnect.
     *
     * @param msg the msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnVideoDisconnect(JFGMsgVideoDisconn msg) {
        playing = false;
        //show play view
        binding.vsStateView.setVisibility(View.VISIBLE);
        binding.ivPlay.setImageResource(R.drawable.btn_play);
        binding.vsStateView.setDisplayedChild(0);
        binding.tvBitRate.setVisibility(View.GONE);
        SLog.i(msg.remote + " errCode:" + msg.code);
        showToast("errCode:" + msg.code);
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
        binding.vsStateView.setVisibility(View.GONE);
        binding.tvBitRate.setVisibility(View.VISIBLE);
        JfgAppCmd.getInstance().setRenderRemoteView(videoView);
        enableMic = false; // default state
        enableVoice = false;// default state
        JfgAppCmd.getInstance().setAudio(false, true, true); // enable remote device mic and voice
        if (!realTime) {
            isReady = true;
            JfgAppCmd.getInstance().playHistoryVideo(device.uuid, curreHistoryTime);
        }
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
     * On update history videos.
     *
     * @param list the list
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnUpdateHistoryVideos(ArrayList<JFGVideo> list) {
        videos.clear();
        videos.addAll(list);
        videoAdapter.notifyDataSetChanged();
        if (videos.size() != 0) {
            binding.tbVideo.setEnabled(true);
            curreHistoryTime = videos.get(0).beginTime;
        }
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
        binding.tvCid.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
