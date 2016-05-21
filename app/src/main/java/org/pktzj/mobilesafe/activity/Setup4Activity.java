package org.pktzj.mobilesafe.activity;

import android.content.Intent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.service.LostFindService;
import org.pktzj.mobilesafe.utils.MyConstants;
import org.pktzj.mobilesafe.utils.SPTool;
import org.pktzj.mobilesafe.utils.ServiceUtils;

/**
 * Created by pktzj on 2016/5/20.
 */
public class Setup4Activity extends BaseSetupActivity{

    private CheckBox cb_isprotected;

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup4);
        cb_isprotected = (CheckBox) findViewById(R.id.cb_isprotected_setup4);
    }

    @Override
    protected void initData() {
        super.initData();
        cb_isprotected.setChecked(ServiceUtils.serviceIsRunning(Setup4Activity.this, LostFindService.class));
    }

    @Override
    protected void initEvet() {
        super.initEvet();
        cb_isprotected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent service = new Intent(Setup4Activity.this, LostFindService.class);
                if (isChecked) {
                    startService(service);
                } else {
                    stopService(service);
                }
            }
        });
    }

    @Override
    public void newxActivity() {
        if (!ServiceUtils.serviceIsRunning(Setup4Activity.this, LostFindService.class)) {
            //如果服务未开启禁止进入下一个页面
            Toast.makeText(Setup4Activity.this, "请先开启防盗服务!", Toast.LENGTH_SHORT).show();
            return;
        }
        SPTool.putboolean(Setup4Activity.this, MyConstants.ISSETUP, true);
        startActivity(LostFindActivity.class);
    }

    @Override
    public void prevActivity() {
        startActivity(Setup3Activity.class);
    }
}
