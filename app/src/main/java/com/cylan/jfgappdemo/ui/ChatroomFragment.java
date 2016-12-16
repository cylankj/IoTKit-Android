package com.cylan.jfgappdemo.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cylan.entity.jniCall.JFGDevice;
import com.cylan.entity.jniCall.RobotMsg;
import com.cylan.ex.JfgException;
import com.cylan.jfgapp.jni.JfgAppCmd;
import com.cylan.jfgappdemo.JfgEvent;
import com.cylan.jfgappdemo.R;
import com.cylan.jfgappdemo.databinding.FragmentChatroomBinding;
import com.cylan.jfgappdemo.datamodel.StringAndInt;
import com.superlog.SLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * 消息透传界面。
 * 此界面的消息，原样的发送到设备端。
 * Created by lxh on 16-8-15.
 */
public class ChatroomFragment extends BaseFragment {

    FragmentChatroomBinding binding;
    int sn;
    JFGDevice device;
    StringBuilder sb;

    public static ChatroomFragment getInstance(Bundle bundle) {
        ChatroomFragment fragment = new ChatroomFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chatroom, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();
        device = (JFGDevice) getArguments().getSerializable("Device");
        if (device == null) {
            // device must not null;
            Toast.makeText(getContext(), "Device is Null.", Toast.LENGTH_LONG).show();
            return;
        }
        sb = new StringBuilder();
        addLinstener();
    }


    private void addLinstener() {
        binding.btnCommintSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendMessage();
                } catch (JfgException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendMessage() throws JfgException {
        byte[] bytes = new byte[256];
        if (binding.cbTestHex.isChecked()) {
            for (int i = 0; i < 256; i++) {
                bytes[i] = (byte) i;
            }
        } else {
            String str = binding.etInput.getText().toString().trim();
            if (TextUtils.isEmpty(str)) return;
            bytes = str.getBytes();
        }
        ArrayList<String> list = new ArrayList<>();
        list.add(device.uuid);
        RobotMsg robotMsg = new RobotMsg(list, sn, binding.cbIsack.isChecked(), bytes);
        JfgAppCmd.getInstance().robotTransmitMsg(robotMsg);
        sn++;
        sb.append("send: ").append(new String(robotMsg.bytes)).append("\n");
        binding.tvEchoMsg.setText(sb.toString());
        if (binding.cbClearText.isChecked()) {
            binding.etInput.getText().clear();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnRobotTransmitMsg(RobotMsg robotMsg) {
        if (robotMsg == null) {
            return;
        }
        sb.append("recv:").append(new String(robotMsg.bytes)).append("\n");
        binding.tvEchoMsg.setText(sb.toString());
    }

    @Subscribe()
    public void onRobotTransmitMsgAck(JfgEvent.RobotMsgAck ack) {
        SLog.i("sn:" + ack.sn);
    }


}
