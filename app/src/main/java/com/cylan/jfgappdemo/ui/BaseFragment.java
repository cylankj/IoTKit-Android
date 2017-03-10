package com.cylan.jfgappdemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by lxh on 16-8-8.
 */
public class BaseFragment extends Fragment {

    /**
     * The constant STATE_SAVE_IS_HIDDEN.
     */
    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";
    /**
     * The Is show.
     */
    private boolean isShow;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            boolean isShow = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN, false);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isShow) {
                ft.show(this);
            } else {
                ft.hide(this);
            }
            ft.commit();
        }
        super.onCreate(savedInstanceState);
    }


    /**
     * Sets fragment show state.
     *
     * @param isShow the is show
     */
    public void setFragmentShowState(boolean isShow) {
        this.isShow = isShow;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isShow);
    }
}
