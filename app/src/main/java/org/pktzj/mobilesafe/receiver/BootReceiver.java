package org.pktzj.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import org.pktzj.mobilesafe.utils.MyConstants;
import org.pktzj.mobilesafe.utils.SPTool;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //手机启动完成,检测sim 卡是否变化
        //取出原来保存的sim卡信息
        String oldsim = SPTool.getString(context, MyConstants.SIMNUM, "");

        //获取当前手机的sim卡信息
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = tm.getSimSerialNumber();


        //判断sim是否发生变化
        if (!TextUtils.isEmpty(oldsim)) {
            if (!oldsim.equals(simSerialNumber + "1")) {
                //sim卡发生变化,发送报警短信
                String safenum = SPTool.getString(context, MyConstants.SAFENUM, "");

                //发送短信给安全号码
                SmsManager sm = SmsManager.getDefault();
                sm.sendTextMessage(safenum, "", "我是小偷!!这是我的号码!", null, null);
            }
        }
    }
}
