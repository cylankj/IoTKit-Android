package com.cylan.jfgappdemo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.cylan.entity.JfgEnum;
import com.cylan.jfgapp.jni.JfgAppCmd;
import com.superlog.SLog;

/**
 * 监听当前是在前台还是在后台
 * Created by lxh on 16-8-4.
 */
public class ActivityCallbacks implements Application.ActivityLifecycleCallbacks {
    /**
     * The Count.
     */
    int count = 0;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        count++;
        SLog.i("count: " + count);
        if (JfgAppCmd.getInstance() != null) {
            JfgAppCmd.getInstance().reportEnvChange(JfgEnum.ENV_ONTOP);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        count--;
        SLog.i("count: " + count);
        if (count == 0) {
            if (JfgAppCmd.getInstance() != null) {
                JfgAppCmd.getInstance().reportEnvChange(JfgEnum.ENV_ONBACK);
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

}
