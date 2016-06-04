package org.pktzj.mobilesafe.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.utils.MyConstants;
import org.pktzj.mobilesafe.utils.SPTool;

/**
 * Created by pktzj on 2016/5/19.
 */
public class LostFindActivity extends Activity {
    private TextView tv_safenum;
    private TextView tv_isprotect;
    private TextView tv_issetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        tv_issetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LostFindActivity.this, Setup1Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initData() {
        boolean issetup = SPTool.getboolean(LostFindActivity.this, MyConstants.ISSETUP, false);
        if (issetup) {
            tv_safenum.setText("安全号码: " + SPTool.getString(LostFindActivity.this, MyConstants.SAFENUM, ""));
//            Drawable drawable= getResources().getDrawable(R.drawable.lock,null);
//            drawable.setBounds(0, 0, tv_issetup.getWidth(), tv_issetup.getHeight());
//            tv_issetup.setCompoundDrawables(null,null,drawable,null);
        } else {
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initView() {
        setContentView(R.layout.activity_lostfind);
        tv_safenum = (TextView) findViewById(R.id.tv_safenum_lostfind);
        tv_isprotect = (TextView) findViewById(R.id.tv_isprotect_lostfind);
        tv_issetup = (TextView) findViewById(R.id.tv_issetup_lostfind);
    }
}
