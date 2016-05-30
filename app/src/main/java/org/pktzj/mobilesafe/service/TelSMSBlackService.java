package org.pktzj.mobilesafe.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Telephony;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import org.pktzj.mobilesafe.dao.BlackDAO;
import org.pktzj.mobilesafe.utils.MyConstants;

import java.lang.reflect.Method;

public class TelSMSBlackService extends Service {
    private PhoneStateListener listener;
    private TelephonyManager tm;
    private TelephonyManager telMgr;
    private BlackDAO dao;
    private SmsReceiver smsReceiver;

    public TelSMSBlackService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 短信监听
     */
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
                if ((dao.getMode(sm.getDisplayOriginatingAddress()) & BlackDAO.SMSMODE) == BlackDAO.SMSMODE) {
                    abortBroadcast();//终止广播继续传递
                }
            }
        }
    }

    private ITelephony iTelephony;
    private TelephonyManager manager;

    private void endCall() {

        Class<TelephonyManager> c = TelephonyManager.class;
        Method getITelephonyMethod = null;
        try {
            getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            iTelephony = (ITelephony) getITelephonyMethod.invoke(tm, (Object[]) null);
            iTelephony.endCall();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    /**
     * 删除电话日志
     * @param incomingNumber
     *     要删除日志的号码
     */
    protected void deleteCalllog(String incomingNumber) {
        //只能内容提供者来删除电话日志
        Uri uri = Uri.parse("content://call_log/calls");
        //删除日志
        getContentResolver().delete(uri, "number=?", new String[]{incomingNumber});

    }
    @SuppressLint("ServiceCast")
    @Override
    public void onCreate() {
        Log.d(MyConstants.TAG, "TelSMSBlackService is running");
        //短信拦截
        smsReceiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(smsReceiver, filter);

        // 初始化黑名单的业务类
        dao = new BlackDAO(getApplicationContext());
        //拦截电话黑名单
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        listener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, final String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.d(MyConstants.TAG, "CALL_STATE_IDLE");
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.d(MyConstants.TAG, "CALL_STATE_RINGING");
                        int mode = dao.getMode(incomingNumber);
                        if ((mode & BlackDAO.PHONEMODE) == BlackDAO.PHONEMODE) {
                            Log.d(MyConstants.TAG, "电话黑名单号码: " + incomingNumber + "成功拦截!");
                            getContentResolver().registerContentObserver(  Uri.parse("content://call_log/calls"), true, new ContentObserver(new Handler()) {
                                @Override
                                public void onChange(boolean selfChange) {
                                    //电话日志变化触发此方法调用
                                    deleteCalllog(incomingNumber);
                                    //取消内容观察者注册
                                    getContentResolver().unregisterContentObserver(this);
                                    super.onChange(selfChange);
                                }
                            });
                            //挂断电话
                            endCall();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Log.d(MyConstants.TAG, "CALL_STATE_OFFHOOK");
                        break;
                    default:
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        //注册监听器
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(MyConstants.TAG, "TelSMSBlackService is stop");
        //取消短信接收者
        unregisterReceiver(smsReceiver);
        //取消电话监听
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }

}
