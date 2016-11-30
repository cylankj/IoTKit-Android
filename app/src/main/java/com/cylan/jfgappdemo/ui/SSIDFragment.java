package com.cylan.jfgappdemo.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.cylan.constants.JfgConstants;
import com.cylan.jfgapp.jni.JfgAppUdpCmd;
import com.cylan.jfgappdemo.JfgEvent;
import com.cylan.jfgappdemo.R;
import com.cylan.jfgappdemo.databinding.FragmentDevSsidListBinding;
import com.cylan.jfgappdemo.datamodel.BindDevBean;

import com.cylan.udpMsgPack.JfgUdpMsg;
import com.cylan.utils.JfgNetUtils;
import com.superlog.SLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.msgpack.MessagePack;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by lxh on 16-8-3.
 */
public class SSIDFragment extends BaseFragment {


    /**
     * The Binding.
     */
    FragmentDevSsidListBinding binding;
    /**
     * The Adapter.
     */
    ArrayAdapter<String> adapter;
    /**
     * The Ssids.
     */
    ArrayList<String> ssids;
    /**
     * The Scaner thread.
     */
    HandlerThread scanerThread;
    /**
     * The Scaner.
     */
    Handler scaner;
    /**
     * The Cmd.
     */
    JfgAppUdpCmd cmd;
    /**
     * The Dialog.
     */
    AlertDialog dialog;
    /**
     * The Bind dev bean.
     */
    BindDevBean bindDevBean;
    /**
     * The Net id.
     */
    int netId;
    /**
     * The Receiver.
     */
    NetWorkChangeReceiver receiver;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SSIDFragment getInstance() {
        return new SSIDFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dev_ssid_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ssids = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, ssids);
        binding.lvSsidList.setAdapter(adapter);
        cmd = JfgAppUdpCmd.getInstance(getContext());
    }

    private void initHandler() {
        scanerThread = new HandlerThread("ScanDevSsid");
        scanerThread.start();
        scaner = new Handler(scanerThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
//                        SLog.i("scan dev ssid");
                        scaner.removeMessages(1);
                        ArrayList<String> list = cmd.getDeviceList();
                        if (list.size() > 0) {
                            ssids.clear();
                            ssids.addAll(list);
                            notifyDataChange();
                        }
                        scaner.sendEmptyMessageDelayed(1, 3000);
                        break;
                    case 2:
                        SLog.i("Connect To wifi is time out.");
                        if (dialog != null && dialog.isShowing()) {
                            dialog.cancel();
                            showToast("Connect devices " + bindDevBean.ssid + " failure.");
                        }
//                        handler.removeMessages(2);
                        break;
                    case 3:
                        scaner.removeMessages(3);
                        JfgAppUdpCmd.getInstance(getContext()).ping(JfgConstants.IP);
                        scaner.sendEmptyMessageDelayed(3, 1000);
                        break;
                    case 4:
                        scaner.removeMessages(3);
                        scaner.removeMessages(4);
                        JfgAppUdpCmd.getInstance(getContext()).fping(JfgConstants.IP);
                        scaner.sendEmptyMessageDelayed(4, 1000);
                        break;
                    case 5:
                        JfgNetUtils netUtils = JfgNetUtils.getInstance(getContext());
                        //1.save current netId;
                        netId = netUtils.getNetworkId();
                        // 2. connect dev ssid ;
                        SLog.e("save current netId: " + netId);
                        if (netId != -1) {
                            bindDevBean.netId = netId;
                        }
                        netUtils.connect2Wifi(bindDevBean.ssid, "11111111", 3);
                        break;
                }
                return true;
            }
        });

    }

    private void notifyDataChange() {
        binding.lvSsidList.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        SLog.e("onStart");
        initHandler();
        bindDevBean = new BindDevBean();
        scaner.sendEmptyMessage(1);
        addLinstener();
        receiver = new NetWorkChangeReceiver();
        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        getContext().registerReceiver(receiver, filter);
    }


    private void addLinstener() {
        binding.lvSsidList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // connect this dev;
                bindDevBean = new BindDevBean();
                bindDevBean.ssid = ssids.get(position);
                SLog.i(bindDevBean.ssid);
                scaner.sendEmptyMessageDelayed(2, 30 * 1000);
                showDialog(bindDevBean.ssid);
                scaner.sendEmptyMessage(5);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        SLog.i("onStop");
        getContext().unregisterReceiver(receiver);
    }

    @Override
    public void onDestroyView() {
        SLog.i("OnDestroyView");
        super.onDestroyView();
        scaner.removeCallbacksAndMessages(null);
        scanerThread.quit();
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

    private void showDialog(String ssid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        dialog = builder.setView(R.layout.dialog_loading_item)
                .setTitle(ssid).setCancelable(false).create();
        dialog.show();
        SLog.i("showDialog ");
    }

    private void showToast(final String str) {
        binding.lvSsidList.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
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
        if (TextUtils.equals(heard.cmd, JfgConstants.ping_ack)) {
            // ping ack
            JfgUdpMsg.PingAck pingAck = pack.read(msg.data, JfgUdpMsg.PingAck.class);
            if (pingAck.net == 2) {
                // this devices has mobile network. 3G or 4G
                bindDevBean.devNetType = pingAck.net;
            }
            SLog.i(pingAck.cid);
            SLog.e("Send Fping");
            scaner.removeCallbacksAndMessages(null);
            scaner.sendEmptyMessageDelayed(4, 200);
            bindDevBean.cid = pingAck.cid;
        } else if (TextUtils.equals(heard.cmd, JfgConstants.f_ping_ack)) {
            JfgUdpMsg.FPingAck fpingAck = pack.read(msg.data, JfgUdpMsg.FPingAck.class);
            SLog.i("cid: %s, mac: %s, version: %s", fpingAck.cid, fpingAck.mac, fpingAck.version);
            if (!TextUtils.equals(fpingAck.cid, bindDevBean.cid)) {
                scaner.sendEmptyMessageDelayed(4, 1000);
                return;
            }
            scaner.removeMessages(4);
            // show input fragment .
            bindDevBean.mac = fpingAck.mac;
            bindDevBean.version = fpingAck.version;
            String ip = JfgNetUtils.getInstance(getContext()).getWayIpAddress();
            bindDevBean.ip = ip;
            Bundle bundle = new Bundle();
            bundle.putSerializable("bindDevBean", bindDevBean);
            ArrayList<ScanResult> results = JfgNetUtils.getInstance(getContext()).getScanResult();
            bundle.putParcelableArrayList("list", results);
            InputWifiCfgFragment fragment = InputWifiCfgFragment
                    .getInstance(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fl_child_container, fragment).hide(SSIDFragment.this).commit();
        }

    }


    /**
     * The type Net work change receiver.
     */
    class NetWorkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                SLog.i("networkinfo :" + info.getState());
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    String ssid = JfgNetUtils.getInstance(context).getSSID();
                    if (!TextUtils.equals(bindDevBean.ssid, ssid)) {
                        scaner.sendEmptyMessageDelayed(2, 5000);
                        return;
                    }
                    scaner.sendEmptyMessage(3);
                    showToast("Connect " + bindDevBean.cid + " successed .");
                    if (dialog != null && dialog.isShowing()) {
                        dialog.cancel();
                    }
                }
            }
        }
    }

}
