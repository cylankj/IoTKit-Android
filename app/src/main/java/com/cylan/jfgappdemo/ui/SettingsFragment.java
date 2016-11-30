package com.cylan.jfgappdemo.ui;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cylan.constants.JfgConstants;
import com.cylan.entity.jniCall.JFGDPMsg;
import com.cylan.entity.jniCall.JFGDevice;
import com.cylan.entity.jniCall.JFGResult;
import com.cylan.entity.jniCall.RobotoGetDataRsp;
import com.cylan.jfgapp.jni.JfgAppCmd;
import com.cylan.jfgappdemo.JfgEvent;
import com.cylan.jfgappdemo.R;
import com.cylan.jfgappdemo.databinding.FragmentSettingsBinding;
import com.cylan.utils.JfgMsgPackUtils;
import com.superlog.SLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.msgpack.MessagePack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * 设置界面，此处列出两个功能，其他功能类似
 * Created by lxh on 16-8-18.
 */
public class SettingsFragment extends BaseFragment {

    JFGDevice device;
    FragmentSettingsBinding binding;


    public static SettingsFragment getInstance(Bundle bundle) {
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        device = (JFGDevice) getArguments().getSerializable("Device");
        EventBus.getDefault().register(this);
        ArrayList<JFGDPMsg> dp = new ArrayList<>();
        JFGDPMsg type = new JFGDPMsg(501, 0);
        JFGDPMsg motion = new JFGDPMsg(303, 0);
        dp.add(type);
        dp.add(motion);
        long seq = JfgAppCmd.getInstance().robotGetData(device.uuid, dp, 1, false, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        addListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void addListener(){
        binding.rgRecordType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int type = checkedId == R.id.rb_2 ? 1 : checkedId == R.id.rb_3 ? 2 : 0;
                SLog.w("checkedId:" + checkedId);
                ArrayList<JFGDPMsg> list = new ArrayList<JFGDPMsg>();
                JFGDPMsg msg = new JFGDPMsg(303, System.currentTimeMillis() / 1000);
                try {
                    msg.packValue = JfgMsgPackUtils.pack(type);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                list.add(msg);
                long seq = JfgAppCmd.getInstance().robotSetData(device.uuid, list);
                SLog.i("seq: " + seq);
            }
        });
        binding.swMotionDetection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ArrayList<JFGDPMsg> list = new ArrayList<JFGDPMsg>();
                JFGDPMsg msg = new JFGDPMsg(501, System.currentTimeMillis() / 1000);
                try {
                    msg.packValue =JfgMsgPackUtils.pack(isChecked);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                list.add(msg);
                long seq = JfgAppCmd.getInstance().robotSetData(device.uuid, list);
                SLog.i("seq: " + seq);
            }
        });
        binding.btnUnbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (device != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("UnBind Device").setMessage("UnBind " + device.uuid + " ?")
                            .setNegativeButton("NO", null).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JfgAppCmd.getInstance().unBindDevice(device.uuid);
                        }
                    }).create().show();
                }
            }
        });
    }

    @Subscribe()
    public void onResult(JFGResult result) {
        if (result.event == JfgEvent.ResultEvent.JFG_RESULT_UNBINDDEV) {
            if (result.code == JfgConstants.RESULT_OK) {
                // unbind succeed finish this fragment and playfragment.
            }
        }
    }


    @Subscribe()
    public void onSetDataRsp(JfgEvent.RobotoSetDataRsp rsp) {
        SLog.w("setData seq: " + rsp.seq);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnRobotGetDataRsp(RobotoGetDataRsp rsp) throws IOException {
        if (!TextUtils.equals(rsp.identity, device.uuid)) return;
        SLog.i(rsp.identity);
        for (Map.Entry<Integer, ArrayList<JFGDPMsg>> entry : rsp.map.entrySet()) {
            int key = entry.getKey();
            switch (key) {
                case 501:
                    if (entry.getValue().size() > 0) {
                        byte[] data = entry.getValue().get(0).packValue;
                       boolean flag =JfgMsgPackUtils.unpack(data,Boolean.class);
                        SLog.e("key:" + key + " ,value: " +flag);
                        binding.swMotionDetection.setChecked(flag);
                    }
                    break;
                case 303:
                    if (entry.getValue().size() > 0) {
                        byte[] data = entry.getValue().get(0).packValue;
                        int state = JfgMsgPackUtils.unpack(data,Integer.class);
                        binding.rgRecordType.check(state + 1);
                        SLog.e("key:" + key + " ,value: " + state);
                        ((RadioButton) binding.rgRecordType.getChildAt(state)).setChecked(true);
                    }
                    break;
                default:
                    SLog.i("unhandle key " + key);
                    break;
            }
        }
    }


}
