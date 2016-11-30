package com.cylan.jfgappdemo.ui;

import android.databinding.DataBindingUtil;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.cylan.constants.JfgConstants;
import com.cylan.jfgapp.jni.JfgAppUdpCmd;
import com.cylan.jfgappdemo.JFGAppliction;
import com.cylan.jfgappdemo.JfgEvent;
import com.cylan.jfgappdemo.R;
import com.cylan.jfgappdemo.databinding.FragmentInputWifiCfgBinding;
import com.cylan.jfgappdemo.datamodel.BindDevBean;
import com.cylan.udpMsgPack.JfgUdpMsg;
import com.cylan.utils.JfgMD5Util;
import com.cylan.utils.JfgNetUtils;
import com.superlog.SLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.msgpack.MessagePack;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by lxh on 16-8-4.
 */
public class InputWifiCfgFragment extends BaseFragment {

    /**
     * The Bean.
     */
    BindDevBean bean;
    /**
     * The Binding.
     */
    FragmentInputWifiCfgBinding binding;
    /**
     * The Adapter.
     */
    ArrayAdapter<String> adapter;
    /**
     * The Ssid list.
     */
    ArrayList<String> ssidList;


    /**
     * Gets instance.
     *
     * @param bundle the bundle
     * @return the instance
     */
    public static InputWifiCfgFragment getInstance(Bundle bundle) {
        InputWifiCfgFragment fragment = new InputWifiCfgFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_input_wifi_cfg, container, false);
        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new String("scan wifi list"));
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bean = (BindDevBean) getArguments().getSerializable("bindDevBean");
        ArrayList<ScanResult> list = getArguments().getParcelableArrayList("list");
        ssidList = new ArrayList<>();
        if (list != null && list.size() != 0) {
            ssidList.clear();
            for (ScanResult s : list) {
                if (TextUtils.isEmpty(s.SSID)) continue;
                ssidList.add(s.SSID);
            }
        }
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, ssidList);
        binding.spWifiList.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        addListener();
    }

    /**
     * Scan wifi list.
     *
     * @param str the str
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void scanWifiList(String str) {
        if (!TextUtils.equals(str, "scaning")) return;
        ArrayList<ScanResult> results = JfgNetUtils.getInstance(getContext()).getScanResult();
        if (results != null && results.size() != 0) {
            ssidList.clear();
            for (ScanResult s : results) {
                if (TextUtils.isEmpty(s.SSID)) continue;
                ssidList.add(s.SSID);
            }
            binding.spWifiList.post(new Runnable() {
                @Override
                public void run() {
                    binding.spWifiList.setAdapter(adapter);
                }
            });
        }
        SystemClock.sleep(10 * 1000);
//        scanWifiList("scaning"); // FIXME: 16-8-25 StackOverflowError
        EventBus.getDefault().post("scaning");
    }


    private void addListener() {
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = binding.spWifiList.getSelectedItemPosition();
                String ssid = ssidList.get(position);
                String pwd = binding.etWifiPwd.getText().toString().trim();
                if (TextUtils.isEmpty(ssid)) return;
                JfgAppUdpCmd cmd = JfgAppUdpCmd.getInstance(getContext());
                // set dev languae
                cmd.setLanguage(JfgConstants.IP, bean.cid, bean.mac);
                // set dev server address
                cmd.setServerAddress(JfgConstants.IP, bean.cid, bean.mac);
                // set dev bind code ,for sdk 3.0
                String md5str = JfgMD5Util.lowerCaseMD5(JFGAppliction.account + System.currentTimeMillis());
                cmd.setBindCode(JfgConstants.IP, bean.cid, bean.mac, md5str);
                bean.bindCode = md5str;
                SLog.i("bind code : " + md5str);
                JFGAppliction.bindBean = bean;
                // set dev wifi config
                if (bean.devNetType == 0) {
                    // wifi cam or door bell
                    cmd.setWifiCfg(JfgConstants.IP, bean.cid, bean.mac, ssid, pwd);
                } else if (bean.devNetType == 2) {
                    // 3G or 4g cam
                    cmd.setWifiCfg(JfgConstants.IP, bean.cid, bean.mac, "", "");
                }

            }
        });
    }


    /**
     * Recv local msg.
     *
     * @param msg the msg
     * @throws IOException the io exception
     */
    @Subscribe
    public void recvLocalMsg(JfgEvent.LocalMsg msg) throws IOException {
        SLog.i(msg.toString());
        MessagePack pack = new MessagePack();
        JfgUdpMsg.UdpHeader heard = pack.read(msg.data, JfgUdpMsg.UdpHeader.class);
        SLog.e("------------" + heard.cmd);
        if (TextUtils.equals(heard.cmd, JfgConstants.do_set_wifi_ack)) {
            // connect wifi ;.
            JFGAppliction.bindModel = true;
            JFGAppliction.bindBean = bean;
            if (bean.netId != -1) {
                JfgNetUtils.getInstance(getContext()).connect2Wifi(bean.netId);
                // exit this fragment and parent fragmnet
            }
        }

    }

}

