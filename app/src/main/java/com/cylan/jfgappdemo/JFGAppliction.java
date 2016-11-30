package com.cylan.jfgappdemo;

import android.app.Application;
import android.content.pm.PackageManager;
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
        System.loadLibrary("sqlcipher");
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
        File dir = Environment.getExternalStorageDirectory();
        File file = new File(dir, "/JfgAppDemo");
        if (!file.exists()) {
            file.mkdir();
        }

        try {
            // 初始化,Context , AppCallBack , log file path .日志文件的存放路径。
            JfgAppCmd.initJfgAppCmd(this, cb, file.getAbsolutePath());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        activityCallbacks = new ActivityCallbacks();
        registerActivityLifecycleCallbacks(activityCallbacks);
    }


}
