package org.pktzj.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
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
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initView() {
        setContentView(R.layout.activity_lostfound);
    }
}
