package com.cylan.jfgappdemo.ui;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cylan.entity.jniCall.JFGDevice;
import com.cylan.entity.jniCall.JFGMsgVideoDisconn;
import com.cylan.entity.jniCall.JFGMsgVideoResolution;
import com.cylan.entity.jniCall.JFGVideoRect;
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
 * 多路视频播放界面
 * Created by tim on 17-1-13.
 */

public class MultVideoActivity extends Activity {

    JFGDevice device;
    int len = 4;
    SurfaceView sv;
    ToggleButton iv[] = new ToggleButton[len];
    JFGVideoRect rect[] = new JFGVideoRect[len];
    int[] camId = new int[len];
    int[] ssrc = new int[len];
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
        iv[0] = binding.ivPlay1;
        iv[1] = binding.ivPlay2;
        iv[2] = binding.ivPlay3;
        iv[3] = binding.ivPlay4;
        rect[0] = new JFGVideoRect(0, 0, 0.499f, 0.499f);
        rect[1] = new JFGVideoRect(0.501f, 0, 1, 0.499f);
        rect[2] = new JFGVideoRect(0, 0.501f, 0.499f, 1);
        rect[3] = new JFGVideoRect(0.501f, 0.501f, 1, 1);
        sv = ViERenderer.CreateRenderer(getBaseContext(), true);
        binding.llRoot.addView(sv, 0);
        for (int i = 0; i < len; i++) {
            camId[i] = i + 1;
            ssrc[i] = 1000 + i;
            iv[i].setOnCheckedChangeListener(new CheckedChangeListener(i));
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnVideoDisconnect(JFGMsgVideoDisconn msg) {
        flag = false;
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
                SLog.i("ssrc index:" + index);
            }
        }
        try {
            JfgAppCmd.getInstance().enableRenderMultiRemoteView(true, msg.ssrc, sv, rect[index]);
        } catch (JfgException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            JfgAppCmd.getInstance().stopPlay(device.uuid);
        } catch (JfgException e) {
            e.printStackTrace();
        }
    }


    class CheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        int index;

        public CheckedChangeListener(int index) {
            this.index = index;
        }

        @Override
        public void onCheckedChanged(CompoundButton v, boolean isChecked) {
            int open;
            try {
                if (isChecked) {
                    if (!flag) {
                        JfgAppCmd.getInstance().playVideo(device.uuid);
                        flag = true;  // 标志一下打开播放句柄
                    } // 发一个透传消息到设备
                    open = 1;
                } else {
                    open = 2;
                }
                PlayMultVideoData data = new PlayMultVideoData(open, camId[index], ssrc[index]);
                JfgAppCmd.getInstance().robotTransmitMsg(new RobotMsg(tag, getSn(), false, JfgMsgPackUtils.pack(data)));
                SLog.i(data.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
