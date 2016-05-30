package org.pktzj.mobilesafe.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.utils.MyConstants;

public class LostFindService extends Service {
    private SmsReceiver recevier;
    private boolean isPlay;

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
        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
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

    class SmsReceiver extends BroadcastReceiver {
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
                String messageBody = sm.getMessageBody();
                if (messageBody.equals("#*gps*#")) {
                    Intent service = new Intent(LostFindService.this, LocationService.class);
                    startService(service);

                    abortBroadcast();//终止广播继续传递
                } else if (messageBody.equals("#*lockscreen*#")) {
                    //获取设备管理器
                    DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
                    //设置密码
                    dpm.resetPassword("123", 0);
                    //一键锁屏
                    dpm.lockNow();

                    abortBroadcast();//终止广播继续传递
                } else if (messageBody.equals("#*wipedata*#")) {
                    //获取设备管理器
                    DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
                    //清楚sd数据
                    dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                    abortBroadcast();
                }else if (messageBody.equals("#*music*#")){
                    //只播放一次
                    abortBroadcast();
                    if (isPlay) {
                        return;
                    }

                    //播放音乐
                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.qqqg);
                    //设置左右声道声音为最大值
                    mp.setVolume(1, 1);
                    mp.start();//开始播放
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            //音乐播放完毕，触发此方法
                            isPlay = false;
                        }
                    });;
                    isPlay = true;

                }
            }
        }
    }
}
