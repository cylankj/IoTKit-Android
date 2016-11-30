package com.cylan.jfgappdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;

/**
 * Created by lxh on 16-8-4.
 */
public class NetWorkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String acction = intent.getAction();
        if (TextUtils.equals(acction, ConnectivityManager.CONNECTIVITY_ACTION)) {

        }
    }

}
