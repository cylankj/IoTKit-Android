package com.cylan.jfgappdemo;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import com.cylan.constants.JfgConstants;
import com.cylan.jfgapp.jni.JfgAppCmd;
import com.cylan.jfgappdemo.datamodel.BindDevBean;
import com.cylan.utils.JfgUtils;
import com.superlog.SLog;

import java.io.File;
import java.util.ArrayList;

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

  public Context ctx;

  @Override
  public void onCreate() {
    super.onCreate();
    cb = new AppDemoCallBack();
    ctx = this;

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
          ApplicationInfo info = ctx.getPackageManager().
              getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
          String vid = info.metaData.getString("vid");
          String vkey = info.metaData.getString("vkey");
          String serverAddress = info.metaData.getString("ServerAddress");
          cmd.initNativeParam(vid, vkey, serverAddress, file.getAbsolutePath());
          //log file path .日志文件的存放路径。
          cmd.enableLog(true, file.getAbsolutePath());
//          cmd.setInternalVar(getVar()); // 初始化内部属性。
          SLog.i("enable log " + file.getAbsolutePath());

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }).start();
    activityCallbacks = new ActivityCallbacks();
    registerActivityLifecycleCallbacks(activityCallbacks);
  }


  public ArrayList<String> getVar() {
    ArrayList<String> var = new ArrayList<>();
    var.add(JfgUtils.getVersion(this));
    var.add(Build.VERSION.RELEASE);
    var.add(JfgUtils.getAppPackageName(ctx));
    var.add(Build.MODEL);
    var.add(JfgConstants.ADDR);
    var.add(JfgAppCmd.getInstance().getSdkVersion());
    return var;
  }

}
