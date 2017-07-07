package com.cylan.jfgappdemo.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.cylan.ex.JfgException;
import com.cylan.jfgapp.jni.JfgAppCmd;
import com.cylan.jfgappdemo.R;
import com.cylan.jfgappdemo.databinding.ActivityDemoBinding;
import com.superlog.SLog;

import java.io.File;
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
    request();
    showLoginFragment();
  }

  private void showLoginFragment() {
    LoginFragment fragment = LoginFragment.getInstance(null);
    getSupportFragmentManager().beginTransaction().
        add(R.id.fl_container, fragment).commit();
  }

  private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 110;

  private void request() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {
      //申请WRITE_EXTERNAL_STORAGE权限
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
          WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
    }
  }


  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // Permission Granted
        File dir = Environment.getExternalStorageDirectory();
        final File file = new File(dir, "/JfgAppDemo");
        if (!file.exists()) {
          file.mkdir();
        }
        try {
          JfgAppCmd.getInstance().enableLog(true, file.getAbsolutePath());
        } catch (JfgException e) {
          e.printStackTrace();
        }
      } else {
        // Permission Denied
        Toast.makeText(this, "no storage permission", Toast.LENGTH_SHORT).show();
        SLog.e("no storage permission !!!!");
        SystemClock.sleep(2000);
        System.exit(0);
      }
    }

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
