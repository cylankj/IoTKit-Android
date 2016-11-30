package com.cylan.jfgappdemo.ui;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cylan.entity.JfgEnum;
import com.cylan.entity.jniCall.JFGDPMsg;
import com.cylan.entity.jniCall.JFGDevice;
import com.cylan.entity.jniCall.RobotoGetDataRsp;
import com.cylan.jfgapp.jni.JfgAppCmd;
import com.cylan.jfgappdemo.JfgEvent;
import com.cylan.jfgappdemo.R;
import com.cylan.jfgappdemo.adapter.MessageAdapter;
import com.cylan.jfgappdemo.databinding.FragmentMessageBinding;
import com.cylan.jfgappdemo.datamodel.MessageBean;
import com.cylan.jfgappdemo.datamodel.MsgWarningInfo;
import com.cylan.utils.JfgMsgPackUtils;
import com.cylan.utils.JfgUtils;
import com.superlog.SLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 消息记录页面。
 * Created by lxh on 16-8-9.
 */
public class MessageFragment extends BaseFragment {

    FragmentMessageBinding binding;
    ArrayList<MessageBean> list;
    MessageAdapter adapter;
    HashMap<Long, Integer> delSeqMap;
    JFGDevice device;


    public static MessageFragment getInstance(Bundle bundle) {
        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, container, false);
        device = (JFGDevice) getArguments().getSerializable("Device");
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        list = new ArrayList<>();
        delSeqMap = new HashMap<>();
        adapter = new MessageAdapter(this, list);
        binding.rvMsgList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMsgList.setAdapter(adapter);
        addLinstener();
        getMessage(0, false);
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

    private void getMessage(long version, boolean ase) {
        ArrayList<JFGDPMsg> dps = new ArrayList<>();
        dps.add(new JFGDPMsg(505, version)); // 获取某一个时间点的报警图片
        JfgAppCmd.getInstance().robotGetData(device.uuid, dps, 10, ase, 0);
        SLog.i("getMessage: " + version);
    }


    private void addMessageBean(RobotoGetDataRsp rsp, ArrayList<JFGDPMsg> msgs) throws IOException {
        int len = msgs.size();
        for (int j = 0; j < len; j++) {
            JFGDPMsg dp = msgs.get(j);
            if (dp.id != 505) continue;
            MsgWarningInfo info = JfgMsgPackUtils.unpack(dp.packValue, MsgWarningInfo.class);
            MessageBean bean = getMessageBean(info, rsp.identity);
            bean.version = dp.version;
            if (list.contains(bean)) continue;
            list.add(bean);
        }
    }

    private MessageBean getMessageBean(MsgWarningInfo info, String identity) {
        if (info.files > 7) {
            throw new UnknownError("files > 7 !");
        }
        ArrayList<String> urls = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if ((info.files >> i & 0x1) == 1) {
                String file = info.time + "_" + (i + 1) + ".jpg";
                // 获取报警图片URL
                String url = JfgAppCmd.getInstance().getCloudUrlByType(JfgEnum.JFG_URL.WARNING,info.type,file,identity);
//                SLog.i(url);
                urls.add(url);  // add url ;
            }
        }
        MessageBean bean = new MessageBean();
        bean.urls = urls;
        bean.time = info.time;
        bean.date = JfgUtils.DetailedDateFormat.format(bean.time * 1000);
        return bean;
    }


    private void addLinstener() {
        binding.rvMsgList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState != RecyclerView.SCROLL_STATE_IDLE) return;
                RecyclerView.LayoutManager manager = binding.rvMsgList.getLayoutManager();
                int last = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
                if (last != manager.getItemCount() - 1) return;
                if (list.size() != 0) {
                    long version = list.get(last).version;
                    getMessage(version, true); // 传入最好一天记录的版本号
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        adapter.setListener(new MessageAdapter.OnItemLongClickListener() {
            @Override
            public boolean OnLongClick(View view, int position) {
                // show dialog and del item
                showDialog(list.get(position).version, position);
                return true;
            }
        });
    }

    private void showDialog(final long version, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = builder.setTitle("Delete Message ").setMessage("Dow you want del this msg?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // del item and message
                        delMessage(version, position);
                        dialog.cancel();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setCancelable(true).create();
        dialog.show();
    }


    private void delMessage(long version, int position) {
        ArrayList<JFGDPMsg> dps = new ArrayList<>();
        JFGDPMsg msg = new JFGDPMsg(505, version); // msg warn dp id 505
        SLog.w("del msg by :" + version);
        dps.add(msg);
        long seq = JfgAppCmd.getInstance().robotDelData(device.uuid, dps, 0);
        delSeqMap.put(seq, position);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDelDpResutl(JfgEvent.DelDpResult result) {
        SLog.i("seq:" + result.seq + "  ret:" + result.ret);
        boolean has = delSeqMap.containsKey(result.seq);
        if (!has) return;
        int position = delSeqMap.remove(result.seq);
        list.remove(position);
//        adapter.notifyDataSetChanged();
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, list.size() - 1);

    }


    @Subscribe()
    public void onRobotoGetDataRsp(RobotoGetDataRsp rsp) throws IOException {
        SLog.i(rsp.identity + " seq: " + rsp.seq);
        for (ArrayList<JFGDPMsg> list : rsp.map.values()) {
            addMessageBean(rsp, list);
        }
        binding.rvMsgList.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }


}
