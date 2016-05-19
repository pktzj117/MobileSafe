package org.pktzj.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.utils.MyConstants;
import org.pktzj.mobilesafe.utils.SPTool;

/**
 * Created by pktzj on 2016/5/19.
 */
public class LostFoundActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        boolean issetup = SPTool.getboolean(this, MyConstants.ISSETUP, false);
        if (issetup) {

        } else {

        }
    }

    private void initView() {
        setContentView(R.layout.activity_lostfound);
    }
}
