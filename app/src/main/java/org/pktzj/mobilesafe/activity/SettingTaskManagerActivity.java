package org.pktzj.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.service.LockScreenClearService;
import org.pktzj.mobilesafe.utils.MyConstants;
import org.pktzj.mobilesafe.utils.SPTool;
import org.pktzj.mobilesafe.utils.ServiceUtils;

public class SettingTaskManagerActivity extends Activity {

    private CheckBox cb_locklear;
    private CheckBox cb_showsysapk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        cb_locklear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(SettingTaskManagerActivity.this, LockScreenClearService.class);
                if (!ServiceUtils.serviceIsRunning(SettingTaskManagerActivity.this, LockScreenClearService.class) && isChecked) {
                    startService(intent);
                } else if (ServiceUtils.serviceIsRunning(SettingTaskManagerActivity.this, LockScreenClearService.class) && !isChecked) {
                    stopService(intent);
                }
            }
        });
        cb_showsysapk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPTool.putboolean(SettingTaskManagerActivity.this, MyConstants.SHOWSYSAPP, isChecked);
            }
        });
    }

    private void initData() {
        cb_showsysapk.setChecked(SPTool.getboolean(SettingTaskManagerActivity.this, MyConstants.SHOWSYSAPP, false));
    }

    private void initView() {
        setContentView(R.layout.activity_setting_task_manager);
        cb_locklear = (CheckBox) findViewById(R.id.cb_lockscreenclear);
        cb_showsysapk = (CheckBox) findViewById(R.id.cb_show_sysapk);
    }
}
