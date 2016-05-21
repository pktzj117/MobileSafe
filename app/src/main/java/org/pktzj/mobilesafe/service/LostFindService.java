package org.pktzj.mobilesafe.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;

import org.pktzj.mobilesafe.utils.MyConstants;

public class LostFindService extends Service {
    private SmsReceiver recevier;

    public LostFindService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        //短信广播接收者
        recevier = new SmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        //级别一样，清单文件，谁先注册谁先执行，如果级别一样，代码比清单要高
        //注册短信监听
        registerReceiver(recevier, filter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(recevier);
        super.onDestroy();
    }

    private class SmsReceiver extends BroadcastReceiver {
        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            //实现短信拦截功能
            Bundle extras = intent.getExtras();
            String format = intent.getStringExtra("format");

            Object[] datas = (Object[]) extras.get("pdus");
            for (Object data : datas) {
                SmsMessage sm = SmsMessage.createFromPdu((byte[]) data, format);
                Log.d(MyConstants.TAG, sm.getDisplayOriginatingAddress() + " : " + sm.getMessageBody());
            }
        }
    }
}
