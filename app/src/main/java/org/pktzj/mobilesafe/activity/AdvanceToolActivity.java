package org.pktzj.mobilesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.engine.SmsEngine;
import org.pktzj.mobilesafe.utils.ActivityUtils;

public class AdvanceToolActivity extends Activity {

    private TextView tv_location;
    private TextView tv_smsback;
    private ProgressDialog pb_backsms;
    private TextView tv_smsresu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        initData();

        initEvent();
    }

    private void initEvent() {
        tv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivity(AdvanceToolActivity.this, LocationActivity.class);
            }
        });
        tv_smsback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SmsEngine.smsBackJson(AdvanceToolActivity.this, new SmsEngine.BackProgress() {
                    @Override
                    public void show() {
                        pb_backsms.show();
                    }

                    @Override
                    public void setMax(int count) {
                        pb_backsms.setMax(count);
                    }

                    @Override
                    public void setProgress(int progress) {
                        pb_backsms.setProgress(progress);
                    }

                    @Override
                    public void end() {
                        pb_backsms.dismiss();
                    }
                });
            }
        });
        tv_smsresu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SmsEngine.smsBackGson(AdvanceToolActivity.this, new SmsEngine.BackProgress() {
                    @Override
                    public void show() {
                        pb_backsms.show();
                    }

                    @Override
                    public void setMax(int count) {
                        pb_backsms.setMax(count);
                    }

                    @Override
                    public void setProgress(int progress) {
                        pb_backsms.setProgress(progress);
                    }

                    @Override
                    public void end() {
                        pb_backsms.dismiss();
                    }
                });
            }
        });


    }

    private void initData() {
        pb_backsms.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    private void initView() {
        setContentView(R.layout.activity_advance_tool);
        tv_location = (TextView) findViewById(R.id.tv_location_advancetool);
        tv_smsback = (TextView) findViewById(R.id.tv_smsback);
        tv_smsresu = (TextView) findViewById(R.id.tv_smsresu);

        pb_backsms = new ProgressDialog(this);
    }
}
