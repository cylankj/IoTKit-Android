package com.cylan.jfgappdemo.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.cylan.constants.JfgConstants;
import com.cylan.entity.JfgEvent;
import com.cylan.jfgapp.jni.JfgAppUdpCmd;
import com.cylan.jfgappdemo.JFGAppliction;
import com.cylan.jfgappdemo.R;
import com.cylan.jfgappdemo.databinding.FragmentBinddevBinding;
import com.cylan.jfgappdemo.datamodel.BindDevBean;
import com.cylan.udpMsgPack.JfgUdpMsg;
import com.cylan.utils.JfgMD5Util;
import com.cylan.utils.JfgMsgPackUtils;
import com.cylan.utils.JfgNetUtils;
import com.superlog.SLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by tim on 17-2-3.
 */

public class BindDevFragment extends BaseFragment {


    FragmentBinddevBinding binding;

    JfgAppUdpCmd cmd;
    Handler handler;
    BindDevBean bean;
    ArrayAdapter<String> adapter;

    ArrayList<String> ssids;
    String devIp;

    public static BindDevFragment getInstance() {
        return new BindDevFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_binddev, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initHandler();
        EventBus.getDefault().register(this);
    }

    public void initHandler() {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        sendPing();
                        break;
                    case 2:
                        sendFPing();
                        break;
                    case 3:
                        showInputView();
                        break;
                    case 4:
                        Toast.makeText(getContext(), "bind devices time out!", Toast.LENGTH_SHORT).show();
                        initView();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        cmd = JfgAppUdpCmd.getInstance(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void initView() {
        bean = new BindDevBean();
        binding.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 110);
            }
        });
        binding.tvTips.setText("If device AP mode is enabled, connect the device SSID . Otherwise, enable device AP mode first.");
        binding.btnSettings.setText("OK");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // get net name ,if netname start with DOG- , connected dev!
        if (JfgNetUtils.getInstance(getContext()).getNetName().startsWith("DOG-")) {
            binding.btnSettings.setVisibility(View.GONE);
            SLog.i("onActivityResult: send config");
            handler.sendEmptyMessageDelayed(1, 1000);
            handler.sendEmptyMessageDelayed(4, 60 * 1000); // time out!
            binding.tvTips.setText("Send Ping...");
        }
    }


    public void sendPing() {
        handler.removeMessages(1);
        cmd.ping(JfgConstants.IP);
        handler.sendEmptyMessageDelayed(1, 2000);
    }

    public void sendFPing() {
        handler.removeMessages(1);
        handler.removeMessages(2);
        cmd.fping(devIp);
        handler.sendEmptyMessageDelayed(2, 2000);
    }

    public void sendCfg() {
        handler.removeCallbacksAndMessages(null);
        cmd.setLanguage(JfgConstants.IP, bean.cid, bean.mac);
        cmd.setServerAddress(JfgConstants.IP, bean.cid, bean.mac);// set dev server address
        String md5str = JfgMD5Util.lowerCaseMD5(JFGAppliction.account + System.currentTimeMillis());
        cmd.setBindCode(JfgConstants.IP, bean.cid, bean.mac, md5str);
        bean.bindCode = md5str;
        SLog.i("bind code : " + md5str);
        JFGAppliction.bindBean = bean;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void recvLocalMsg(JfgEvent.LocalMsg msg) {
        JfgUdpMsg.UdpHeader heard = null;
        try {
            heard = JfgMsgPackUtils.unpack(msg.data, JfgUdpMsg.UdpHeader.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SLog.i(heard.cmd);
        if (TextUtils.equals(heard.cmd, JfgConstants.ping_ack)) { // ping ack
            JfgUdpMsg.PingAck pingAck = null;
            try {
                pingAck = JfgMsgPackUtils.unpack(msg.data, JfgUdpMsg.PingAck.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (pingAck.net != 0) {
                // this devices has mobile network. 3G or 4G
                bean.devNetType = pingAck.net;
            }
            SLog.i(pingAck.cid);
            handler.removeMessages(1);
            handler.sendEmptyMessageDelayed(2, 1000);
            binding.tvTips.setText("Send Fping...");
            devIp = msg.ip;
            bean.cid = pingAck.cid;
        } else if (TextUtils.equals(heard.cmd, JfgConstants.f_ping_ack)) {
            JfgUdpMsg.FPingAck fAck = null;
            try {
                fAck = JfgMsgPackUtils.unpack(msg.data, JfgUdpMsg.FPingAck.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            SLog.i("cid: %s, mac: %s, version: %s", fAck.cid, fAck.mac, fAck.version);
            if (!TextUtils.equals(fAck.cid, bean.cid)) {
                return;
            }
            bean.mac = fAck.mac;
            bean.version = fAck.version;
            handler.removeMessages(2);
            handler.sendEmptyMessageDelayed(3, 1000);
            binding.tvTips.setText("Send config...");
            // show input view
        } else if (TextUtils.equals(heard.cmd, JfgConstants.do_set_wifi_ack)) {
            // connect wifi ;.
            JFGAppliction.bindModel = true;
            JFGAppliction.bindBean = bean;
            getFragmentManager().popBackStack();
        }
    }

    public void showInputView() {
        if (bean.devNetType == 0) {
            inputWifiCfg();
        } else {
            sendCfg();
            cmd.setWifiCfg(JfgConstants.IP, bean.cid, bean.mac, "", ""); // dev has mobile network
        }
    }

    private void inputWifiCfg() {
        binding.llInput.setVisibility(View.VISIBLE);
        binding.btnSettings.setVisibility(View.VISIBLE);
        binding.btnSettings.setText("Send");
        ssids = new ArrayList<>();
        ArrayList<ScanResult> results = JfgNetUtils.getInstance(getContext()).getScanResult();
        if (!results.isEmpty()) {
            ssids.clear();
            for (ScanResult s : results) {
                if (TextUtils.isEmpty(s.SSID)) continue;
                if (s.SSID.startsWith("DOG-")) continue;  // is devies's ssid ?
                ssids.add(s.SSID);
            }
            adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, ssids);
            binding.spWifiList.setAdapter(adapter);
            SLog.i("has ssids!");
        } else {
            binding.etSsid.setVisibility(View.VISIBLE);
            binding.spWifiList.setVisibility(View.GONE);
        }

        binding.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCfg();
                String ssid;
                if (binding.etSsid.getVisibility() == View.GONE) {
                    ssid = ssids.get(binding.spWifiList.getSelectedItemPosition());
                } else {
                    ssid = binding.etSsid.getText().toString().trim();
                }
                String pwd = binding.etWifiPwd.getText().toString().trim();
                cmd.setWifiCfg(JfgConstants.IP, bean.cid, bean.mac, ssid, pwd);
            }
        });
    }

}
