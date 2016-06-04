package org.pktzj.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.engine.PhoneLocationEngine;

public class LocationActivity extends Activity {

    private EditText et_location;
    private TextView tv_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {

    }

    private void initData() {

    }

    private void initView() {
        setContentView(R.layout.activity_location);
        et_location = (EditText) findViewById(R.id.et_location_advancetool);
        tv_location = (TextView) findViewById(R.id.tv_location);

    }

    public void querylocation(View view) {
        String number = et_location.getText().toString().trim();
        String location = PhoneLocationEngine.locationQuery(LocationActivity.this, number);
        tv_location.setText("电话归属地: " + location);

    }
}
