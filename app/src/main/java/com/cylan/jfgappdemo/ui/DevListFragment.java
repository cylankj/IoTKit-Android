package com.cylan.jfgappdemo.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cylan.constants.JfgConstants;
import com.cylan.entity.jniCall.JFGAccount;
import com.cylan.entity.jniCall.JFGDPMsg;
import com.cylan.entity.jniCall.JFGDevBaseValue;
import com.cylan.entity.jniCall.JFGDevice;
import com.cylan.entity.jniCall.JFGDoorBellCaller;
import com.cylan.entity.jniCall.JFGMsgHttpResult;
import com.cylan.entity.jniCall.JFGResult;
import com.cylan.entity.jniCall.RobotoGetDataRsp;
import com.cylan.ex.JfgException;
import com.cylan.jfgapp.jni.JfgAppCallBack;
import com.cylan.jfgapp.jni.JfgAppCmd;
import com.cylan.jfgappdemo.JFGAppliction;
import com.cylan.entity.JfgEvent;
import com.cylan.jfgappdemo.R;
import com.cylan.jfgappdemo.adapter.DevsAdapter;
import com.cylan.jfgappdemo.databinding.FragmentDevListBinding;
import com.cylan.jfgappdemo.datamodel.BindDevBean;
import com.cylan.jfgappdemo.datamodel.IntAndString;
import com.cylan.utils.JfgMsgPackUtils;
import com.superlog.SLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lxh on 16-7-23.
 */
public class DevListFragment extends BaseFragment {

    /**
     * The Binding.
     */
    FragmentDevListBinding binding;
    /**
     * The Adapter.
     */
    DevsAdapter adapter;

    /**
     * Gets instance.
     *
     * @param bundle the bundle
     * @return the instance
     */
    public static DevListFragment getInstance(Bundle bundle) {
        DevListFragment fragment = new DevListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dev_list, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 模拟数据
        JFGDevice[] devs = new JFGDevice[0];
        adapter = new DevsAdapter(devs);
        binding.rvDevList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDevList.setAdapter(adapter);
        adapter.setSimpleListener(new DevsAdapter.SimpleListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() != null && v.getTag() instanceof Integer) {
                    final int position = (int) v.getTag();
                    JFGDevice d = adapter.getDevice()[position];
                    SLog.i("pid:" + d.pid);
                    if (d.pid == 1348) {  // 多路视频
                        Intent intent = new Intent(getContext(), MultVideoActivity.class);
                        intent.putExtra("device", d);
                        getContext().startActivity(intent);
                    } else if (d.pid == 86 || d.pid == 18 || d.pid == 19) {
                        Intent intent = new Intent(getContext(), VRPlayActivity.class);
                        intent.putExtra("device", d);
                        getContext().startActivity(intent);
                    } else {
                        PlayFragment fragment = PlayFragment.getInstance(getBundle(d));
                        showOtherFragment(fragment);
                    }
                }
            }
        });
    }

    @NonNull
    private Bundle getBundle(JFGDevice d) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("device", d); // put device
        return bundle;
    }

    private void showOtherFragment(BaseFragment fragment) {
        getFragmentManager().beginTransaction()
                .add(R.id.fl_container, fragment, fragment.getClass().getName())
                .addToBackStack("devs_list")
                .hide(DevListFragment.this).commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        addLinstener();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        test();
    }


    @Override
    public void onStop() {
        super.onStop();
        SLog.d("onStop");
        EventBus.getDefault().unregister(this);
    }

    /**
     * Add linstener.
     */
    private void addLinstener() {
        binding.ivAddDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show add dev fragment
           getFragmentManager().beginTransaction()
                   .hide(DevListFragment.this).addToBackStack("list")
                   .add(R.id.fl_container, BindDevFragment.getInstance()).commit();
            }
        });
    }


    /**
     * On update devs.
     *
     * @param devs the devs
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateDevs(JFGDevice[] devs) throws JfgException {
        if (!isResumed()) return;
        SLog.i("update devs");
        adapter.setDevice(devs);
        adapter.notifyDataSetChanged();
//        getMultiDP(devs);
        // 开始查dataPoint;
        for (JFGDevice dev : devs) {
            getDataPoint(dev.uuid);
        }

        JfgAppCmd.getInstance().sendUniservalDataSeq(1,new byte[]{1,2});
    }

    public void getMultiDP(JFGDevice[] devs){
        HashMap<String,JFGDPMsg[]> map = new HashMap<>();
        HashMap<String,long[]> query = new HashMap<>();
        for (JFGDevice d:devs){
            JFGDPMsg[] jd = new JFGDPMsg[]{new JFGDPMsg(201,0),new JFGDPMsg(206,0)};
            map.put(d.uuid,jd);
            long[] qdp = new long[]{505,512};
            query.put(d.uuid,qdp);
        }
        try {
            JfgAppCmd.getInstance().robotGetMultiData(map,2,false,0);
            JfgAppCmd.getInstance().robotCountMultiData(query,false,0);
        } catch (JfgException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets data point.
     *
     * @param peer the peer
     */
    private void getDataPoint(String peer) throws JfgException {
        ArrayList<JFGDPMsg> dp = new ArrayList<>();
        dp.add(new JFGDPMsg(201, 0));// query dev network
        dp.add(new JFGDPMsg(206, 0));// query dev battery
        dp.add(new JFGDPMsg(207, 0));// fw version
        long seq = 0;
        try {
            seq = JfgAppCmd.getInstance().robotGetData(peer, dp, 1, false, 0);
        } catch (JfgException e) {
            e.printStackTrace();
        }
        SLog.i(peer + " seq:" + seq);
    }

    /**
     * On robot get data rsp.
     *
     * @param rsp the event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnRobotGetDataRsp(RobotoGetDataRsp rsp) throws IOException {
        if (!isResumed())
            return;
        SLog.i(rsp.identity + " seq: " + rsp.seq);
        int index = adapter.getPositionBySn(rsp.identity);
        if (index == -1) return;
        JFGDevice dev = adapter.getDevice()[index];
        if (dev == null) return;
        for (Map.Entry<Integer, ArrayList<JFGDPMsg>> entry : rsp.map.entrySet()) {
            SLog.d("dp key: " + entry.getKey());
            if (entry.getKey() == 206) {
                if (entry.getValue().isEmpty()) continue;
                JFGDPMsg dp = entry.getValue().get(0);
                int battery = JfgMsgPackUtils.unpack(dp.packValue, Integer.class);
                SLog.i("cid: " + rsp.identity + " , battery: " + battery);

            } else if (entry.getKey() == 207) {
                if (entry.getValue().isEmpty()) continue;
                JFGDPMsg dp = entry.getValue().get(0);
                String fwversion = JfgMsgPackUtils.unpack(dp.packValue, String.class);
                SLog.i("cid: " + rsp.identity + " , fw version: " + fwversion);
            }

            if (201 != entry.getKey()) continue;
            if (entry.getValue() == null || entry.getValue().isEmpty()) continue;
            JFGDPMsg dp = entry.getValue().get(0);
            IntAndString values = JfgMsgPackUtils.unpack(dp.packValue, IntAndString.class);
            SLog.i("netType:" + values.intValue + " , netName:" + values.strValue);
            // baseValue
            dev.base = new JFGDevBaseValue(); // 判断base 是否为空。
            dev.base.netType = values.intValue;
            dev.base.netName = values.strValue;
            adapter.notifyItemChanged(index);
        }
    }


    /**
     * On result.
     *
     * @param result the result event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResult(JFGResult result) throws JfgException {
        switch (result.event) {
            case JfgEvent.ResultEvent.JFG_RESULT_LOGIN:
                if (result.code == JfgConstants.RESULT_OK
                        && JFGAppliction.bindModel && JFGAppliction.bindBean != null) {
                    sendBindDeviceMsg();
                }
                break;
            case JfgEvent.ResultEvent.JFG_RESULT_BINDDEV:
                SLog.i("bind dev resutl: " + result.code);
                Toast.makeText(getContext(), "bind dev result:" + result.code, Toast.LENGTH_SHORT).show();
                // 发送时区。
                break;
            case JfgEvent.ResultEvent.JFG_RESULT_UNBINDDEV:
                SLog.i("unbind dev result: " + result.code);
                Toast.makeText(getContext(), "Ubind dev result:" + result.code, Toast.LENGTH_SHORT).show();
                if (result.code != 0) {
                    return;
                }
                int count = getFragmentManager().getBackStackEntryCount();
                for (int i = 0; i < count; i++) {
                    getFragmentManager().popBackStack();
                }
                break;
        }
    }

    @Subscribe()
    public void onRobotoSyncData(JfgEvent.RobotoSyncData data) throws IOException {
        SLog.i("sync data from  %s, identity: %s ", data.fromDev?"device":"server", data.identity);
        if (data.list != null && data.list.size() > 0) {
            for (JFGDPMsg msg : data.list) {
                // you must unpack msg.packValue
                SLog.w("sync dpId:" + msg.id+" ,data: "+new String(msg.packValue));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDoorBellCall(JFGDoorBellCaller caller) {
        SLog.i("call form: " + caller.cid );
        Toast.makeText(getContext(), "call form: " + caller.cid, Toast.LENGTH_SHORT).show();
        // u mast get url
    }


    @Subscribe
    public void OnHttpDone(JFGMsgHttpResult msg) {
        SLog.e("requestId:" + msg.requestId + "  ret:" + msg.ret);
//        if (msg.ret==200){
//            SLog.i("data: "+msg.result.length);
//        }
    }

    private void sendBindDeviceMsg() throws JfgException {
        // send bind msg
        BindDevBean bean = JFGAppliction.bindBean;
        SLog.w("bean.BindCode:" + bean.bindCode);
        try {
            JfgAppCmd.getInstance().bindDevice(bean.cid, bean.bindCode,bean.mac,0); // send bind msg
        } catch (JfgException e) {
            e.printStackTrace();
        }
        JFGAppliction.bindBean = null;
        JFGAppliction.bindModel = false;
    }


    @Subscribe()
    public void onUpdateAccount(JFGAccount ja) {
        SLog.i(ja.getAccount() + ", photo url :" + ja.getPhotoUrl());
    }


}
