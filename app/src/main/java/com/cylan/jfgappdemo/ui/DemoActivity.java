package com.cylan.jfgappdemo.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.cylan.jfgapp.jni.JfgAppCmd;
import com.cylan.jfgappdemo.R;
import com.cylan.jfgappdemo.databinding.ActivityDemoBinding;

import java.util.List;


/**
 * Created by lxh on 16-7-14.
 */
public class DemoActivity extends FragmentActivity {

    ActivityDemoBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_demo);
        showLoginFragment();
    }

    private void showLoginFragment() {
        LoginFragment fragment = LoginFragment.getInstance(null);
        getSupportFragmentManager().beginTransaction().
                add(R.id.fl_container, fragment).commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        System.exit(0);
        super.onDestroy();
    }


    private static long time = 0;

    @Override
    public void onBackPressed() {
        if (checkExtraChildFragment()) {
            return;
        } else if (checkExtraFragment())
            return;
        if (System.currentTimeMillis() - time < 1500) {
            // close database
            JfgAppCmd.getInstance().closeDataBase();
            super.onBackPressed();
        } else {
            time = System.currentTimeMillis();
            Toast.makeText(DemoActivity.this, String.format(getString(R.string.click_back_again_exit),
                    getString(R.string.app_name)), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkExtraChildFragment() {
        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> list = fm.getFragments();
        if (list.isEmpty())
            return false;
        for (Fragment frag : list) {
            if (frag != null && frag.isVisible()) {
                FragmentManager childFm = frag.getChildFragmentManager();
                if (childFm != null && childFm.getBackStackEntryCount() > 0) {
                    childFm.popBackStack();
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkExtraFragment() {
        final int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {
            getSupportFragmentManager().popBackStack();
            return true;
        } else return false;
    }
}
