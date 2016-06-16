package org.pktzj.mobilesafe.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.domain.APPBean;
import org.pktzj.mobilesafe.engine.APPMangerEngine;
import org.pktzj.mobilesafe.fragment.LockedFragment;
import org.pktzj.mobilesafe.fragment.UnlockFragment;

import java.util.List;

public class AppLockActivity extends FragmentActivity {

    private UnlockFragment unlockFragment;
    private LockedFragment lockedFragment;
    private FragmentManager sfm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        sfm = getSupportFragmentManager();
        sfm.beginTransaction().replace(R.id.fl_lockapp_content, unlockFragment).commit();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initView() {
        setContentView(R.layout.activity_app_lock);
        List<APPBean> allAPK = APPMangerEngine.getAllAPK(AppLockActivity.this);
        unlockFragment = new UnlockFragment(allAPK);
        lockedFragment = new LockedFragment(allAPK);
    }

}
