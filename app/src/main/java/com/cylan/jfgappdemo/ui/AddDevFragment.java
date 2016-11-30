package com.cylan.jfgappdemo.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cylan.jfgappdemo.R;
import com.cylan.jfgappdemo.databinding.FragmentAddDevBinding;
import com.superlog.SLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 添加设备界面。
 * 简单描述下逻辑。
 * 1.扫描出设备发出的SSID，我司设备发出的SSID前缀为 DOG- 。
 * 2.连接到此设备上。
 * 3.连接成功后，发送配置命令,命令回应在 onLocalMessage 接口返回,需要用 msgpack 包来解析byte[]。
 * 4.发送命令如下：
 * send: ping , recv: ping_ack; 确认收到回复消息,否则1s 发一次，直到收到，才发送下一个命令。
 * send: fping ,recv: fping_ack; 确认收到回复消息,否则1s 发一次，直到收到，才发送下一个命令。
 * send: setLanguage, send: setServerAddress.
 * 最后是 send: setWifiCfg .注意 WifiCfg 一定要最后发。因为设备端收到后就会关闭ap，与手机断开连接。
 * 5. 连上正常可以上网的wifi 。
 * 6. 登陆成功后发送   JfgAppCmd.getInstance().bindDevice(cid,code);
 * 7. 在onResult 的回调中判断绑定是否成功。
 *
 * 可以根据各自的界面实现逻辑。
 * 此demo 已知问题：在 google nexus 5X 中，handler 消息异常。
 *   在android 6.0 中 有较大的概率使用代码无法切换wifi，（连接设备。） 可以引导用户前往系统设置界面，手动连接设备wifi。
 *   建议自己实现界面逻辑配合以上api使用。
 * Created by lxh on 16-8-3.
 */
public class AddDevFragment extends BaseFragment {
    FragmentAddDevBinding binding;


    public static AddDevFragment getInstance() {
        return new AddDevFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_dev, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getChildFragmentManager().beginTransaction().add(R.id.fl_child_container, SSIDFragment.getInstance()).commit();
    }

    @Subscribe
    public void onExit(ExitFragmentEvent event) {
        SLog.i("exit add fragment ");
        getFragmentManager().popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }


    static class ExitFragmentEvent {

    }

}
