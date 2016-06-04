package org.pktzj.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.service.PhoneLocationService;
import org.pktzj.mobilesafe.service.TelSMSBlackService;
import org.pktzj.mobilesafe.utils.MyConstants;
import org.pktzj.mobilesafe.utils.SPTool;
import org.pktzj.mobilesafe.utils.ServiceUtils;
import org.pktzj.mobilesafe.view.SettingCenterItemView;

public class SettingCenter extends Activity {

    private SettingCenterItemView id_update;
    private SettingCenterItemView id_blacknum;
    private SettingCenterItemView id_phonelocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        initData();

        initEvent();
    }

    private void initEvent() {
        id_update.setItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_update.setChecked(!id_update.isChecked());
                SPTool.putboolean(SettingCenter.this, MyConstants.UPDATESERVICE, id_update.isChecked());
            }
        });

        id_blacknum.setItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ischecked = !id_blacknum.isChecked();
                id_blacknum.setChecked(ischecked);
                SPTool.putboolean(SettingCenter.this,MyConstants.BLACKNUM,ischecked);
                //开启和关闭服务
                if (ServiceUtils.serviceIsRunning(SettingCenter.this, TelSMSBlackService.class) && !ischecked) {
                    Intent service = new Intent(SettingCenter.this, TelSMSBlackService.class);
                    stopService(service);
                } else if (!ServiceUtils.serviceIsRunning(SettingCenter.this, TelSMSBlackService.class) && ischecked) {
                    Intent service = new Intent(SettingCenter.this, TelSMSBlackService.class);
                    startService(service);
                }
            }
        });

        id_phonelocation.setItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ischecked = !id_phonelocation.isChecked();
                id_phonelocation.setChecked(ischecked);
                SPTool.putboolean(SettingCenter.this,MyConstants.PHONELOCA,ischecked);
                //开启和关闭服务
                Intent service = new Intent(SettingCenter.this, PhoneLocationService.class);
                if (ServiceUtils.serviceIsRunning(SettingCenter.this, PhoneLocationService.class) && !ischecked) {
                    stopService(service);
                } else if (!ServiceUtils.serviceIsRunning(SettingCenter.this, PhoneLocationService.class) && ischecked) {
                    startService(service);
                }
            }
        });
    }

    private void initData() {
        id_update.setChecked(SPTool.getboolean(SettingCenter.this, MyConstants.UPDATESERVICE,false));
        id_blacknum.setChecked(SPTool.getboolean(SettingCenter.this, MyConstants.BLACKNUM,false));
        id_phonelocation.setChecked(SPTool.getboolean(SettingCenter.this, MyConstants.PHONELOCA,false));
    }

    private void initView() {
        setContentView(R.layout.activity_settingcenter);

        //获取启动更新控件
        id_update = (SettingCenterItemView) findViewById(R.id.SettingCenterItemView_update);
        id_blacknum = (SettingCenterItemView) findViewById(R.id.SettingCenterItemView_blacknum);
        id_phonelocation = (SettingCenterItemView) findViewById(R.id.SettingCenterItemView_phonelocation);
    }
}
