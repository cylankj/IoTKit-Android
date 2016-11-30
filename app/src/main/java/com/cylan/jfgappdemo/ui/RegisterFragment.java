package com.cylan.jfgappdemo.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.cylan.entity.JfgEnum;
import com.cylan.entity.jniCall.JFGResult;
import com.cylan.jfgapp.jni.JfgAppCmd;
import com.cylan.jfgappdemo.JfgEvent;
import com.cylan.jfgappdemo.R;
import com.cylan.jfgappdemo.databinding.FragmentRegisterBinding;
import com.superlog.SLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by lxh on 16-7-14.
 */
public class RegisterFragment extends Fragment {
    /**
     * The Binding.
     */
    FragmentRegisterBinding binding;

    private String token; // use it to register;
    private String account;
    public String pwd;


    /**
     * Gets instance.
     *
     * @param bundle the bundle
     * @return the instance
     */
    public static RegisterFragment getInstance(Bundle bundle) {
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setListener();
        showOrHideView(false);
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

    /**
     * 90s , interval 1s
     */
    CountDownTimer timer = new CountDownTimer(90 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            binding.tvGetCode.setText(millisUntilFinished / 1000 + "S");
        }

        @Override
        public void onFinish() {
            binding.tvGetCode.setEnabled(true);
            binding.tvGetCode.setText("SMSCode");
        }
    };


    /**
     * Sets listener.
     */
    private void setListener() {
        binding.tvGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = binding.etUserName.getText().toString().trim();
                JfgAppCmd.getInstance().sendCheckCode(account, JfgEnum.JFG_SMS_REGISTER);
                timer.start();
                showOrHideView(true);
                binding.tvGetCode.setEnabled(false);
            }
        });
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                account = binding.etUserName.getText().toString().trim();
                pwd = binding.etPwd.getText().toString().trim();
                String code = binding.etCode.getText().toString().trim();
                // check code
                if (TextUtils.isEmpty(token)) {
                    Toast.makeText(getContext(), "token is null!", Toast.LENGTH_SHORT).show();
                    return;
                }
                JfgAppCmd.getInstance().verifySMS(account, code, token);
                // use phone register


            }
        });
    }


    /**
     * On result.
     *
     * @param result the result event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResult(JFGResult result) {
        if (result.event == JfgEvent.ResultEvent.JFG_RESULT_REGISTER) {
            Toast.makeText(getContext(), "register result: " + result.code, Toast.LENGTH_SHORT).show();
            SLog.i("register result: " + result.code);
            if (result.code == 0) {
                // register ok ! show login fragment .
                LoginFragment fragment = LoginFragment.getInstance(null);
                getActivity().getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fl_container, fragment).commit();
            }
        } else if (result.event == JfgEvent.ResultEvent.JFG_RESULT_VERIFY_SMS) {
            Toast.makeText(getContext(), "get sms result: " + result.code, Toast.LENGTH_SHORT).show();
            SLog.i("verify sms code result : " + result.code);
            JfgAppCmd.getInstance().
                    register(account, pwd, JfgEvent.REGISTER_TYPE_PHONE, token);

            // use mail register
//  JfgAppCmd.getInstance().register("cleverdog@cylan.com.cn", "88888888", JfgEvent.REGISTER_TYPE_MAIL, "");
        }
    }

    @Subscribe()
    public void onSmsCodeResult(JfgEvent.SmsCodeResult result) {
        if (result.error == 0) {
            token = result.token;
        } else {
            Toast.makeText(getContext(), "get Sms code error: " + result.error, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Show or hide view.
     *
     * @param isShow the is show
     */
    private void showOrHideView(boolean isShow) {
        binding.etPwd.setVisibility(isShow ? View.VISIBLE : View.GONE);
        binding.btnRegister.setVisibility(isShow ? View.VISIBLE : View.GONE);
        binding.etCode.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
