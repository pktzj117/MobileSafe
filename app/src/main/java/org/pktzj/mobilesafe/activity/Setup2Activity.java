package org.pktzj.mobilesafe.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.utils.MyConstants;
import org.pktzj.mobilesafe.utils.SPTool;

/**
 * Created by pktzj on 2016/5/20.
 */
public class Setup2Activity extends BaseSetupActivity {

    private static final String TAG = "MobileSafe";
    private ImageView iv_isbind;
    private Button bt_bindsim;

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup2);
        bt_bindsim = (Button) findViewById(R.id.bt_bindsim_setup2);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initData() {
        super.initData();
        String simnum = SPTool.getString(this, MyConstants.SIMNUM, "");
        if (TextUtils.isEmpty(simnum)) {
            Drawable drawable= getResources().getDrawable(R.drawable.unlock);
            drawable.setBounds(0, 0, bt_bindsim.getMinimumHeight(), bt_bindsim.getMinimumHeight());
            bt_bindsim.setCompoundDrawables(null,null,drawable,null);
        } else {
            Drawable drawable= getResources().getDrawable(R.drawable.lock);
            drawable.setBounds(0, 0, bt_bindsim.getMinimumHeight(), bt_bindsim.getMinimumHeight());
            bt_bindsim.setCompoundDrawables(null,null,drawable,null);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void changebindsim(View view) {

        String simnum = SPTool.getString(this, MyConstants.SIMNUM, "");
        if (TextUtils.isEmpty(simnum)) {//未绑定sim卡  进行绑定操作
            {   //绑定sim卡
                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String simSerialNumber = tm.getSimSerialNumber();
                SPTool.putString(this, MyConstants.SIMNUM, simSerialNumber);
            }

            Drawable drawable= getResources().getDrawable(R.drawable.lock);
            drawable.setBounds(0, 0, bt_bindsim.getMinimumHeight(), bt_bindsim.getMinimumHeight());
            bt_bindsim.setCompoundDrawables(null,null,drawable,null);
        } else {//已绑定sim卡  进行解绑操作
            SPTool.putString(this, MyConstants.SIMNUM, "");
            Drawable drawable= getResources().getDrawable(R.drawable.unlock);
            drawable.setBounds(0, 0, bt_bindsim.getMinimumHeight(), bt_bindsim.getMinimumHeight());
            bt_bindsim.setCompoundDrawables(null,null,drawable,null);
        }
    }

    @Override
    public void next(View view) {
        String simnum = SPTool.getString(this, MyConstants.SIMNUM, "");
        Log.d(MyConstants.TAG, "simnum: " + simnum);
        if (TextUtils.isEmpty(simnum)) {
            Toast.makeText(Setup2Activity.this, "未绑定SIM卡,请先绑定SIM卡!", Toast.LENGTH_SHORT).show();
            return;
        }
        super.next(view);
    }

    @Override
    public void newxActivity() {
        startActivity(Setup3Activity.class);
    }

    @Override
    public void prevActivity() {
        startActivity(Setup1Activity.class);
    }
}
