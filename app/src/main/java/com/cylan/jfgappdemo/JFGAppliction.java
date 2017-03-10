package com.cylan.jfgappdemo;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.cylan.jfgapp.jni.JfgAppCmd;
import com.cylan.jfgappdemo.datamodel.BindDevBean;

import java.io.File;

/**
 * app 启动后第一时间初始化JfgAppCmd
 * 后期全局使用此类。
 * Created by lxh on 16-7-7.
 */
public class JFGAppliction extends Application {

    static {
        System.loadLibrary("jfgsdk");
    }

    /**
     * The Cb.
     */
    AppDemoCallBack cb;
    /**
     * The Activity callbacks.
     */
    ActivityCallbacks activityCallbacks;
    /**
     * The constant bindModel.
     */
    public static boolean bindModel;
    /**
     * The constant bindBean.
     */
    public static BindDevBean bindBean;


    /**
     * The constant account.
     */
    public static String account;

    @Override
    public void onCreate() {
        super.onCreate();
        cb = new AppDemoCallBack();
        // 不再放在主线程中调用。
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File dir = Environment.getExternalStorageDirectory();
                    final File file = new File(dir, "/JfgAppDemo");
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    // 初始化,Context , AppCallBack
                   JfgAppCmd cmd = JfgAppCmd.getInstance();
                    cmd.setCallBack(cb);
                    // vid , vkey ,serveraddress
                    cmd.initNativeParam("0001","Z5SYDbLV44zfFGRdAgFQhH62fAnIqf3G","yun.jfgou.com:443");
                    //log file path .日志文件的存放路径。
                    cmd.enableLog(true, file.getAbsolutePath());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        activityCallbacks = new ActivityCallbacks();
        registerActivityLifecycleCallbacks(activityCallbacks);
    }


}
