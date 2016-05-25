package org.pktzj.mobilesafe;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.pktzj.mobilesafe.domain.ContactBean;
import org.pktzj.mobilesafe.engine.readContactEngine;
import org.pktzj.mobilesafe.service.LostFindService;
import org.pktzj.mobilesafe.utils.MyConstants;
import org.pktzj.mobilesafe.utils.SPTool;
import org.pktzj.mobilesafe.utils.ServiceUtils;

import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    private Notification notification;
    private NotificationManager nManager;

    public ApplicationTest() {

        super(Application.class);


    }

    public void test() {
        List<ContactBean> contacts = readContactEngine.readContarts(getContext());
        for (ContactBean contact : contacts) {
            Log.d(MyConstants.TAG, contact.toString());
        }
    }

    public void testService() {
        boolean b = ServiceUtils.serviceIsRunning(getContext(), LostFindService.class);
    }

    public void testsms() {
        String safenum = SPTool.getSring(getContext(), MyConstants.SAFENUM, "");

        //发送短信给安全号码
        SmsManager sm = SmsManager.getDefault();
        sm.sendTextMessage(safenum, "", "我是小偷!!这是我的号码!", null, null);
    }

    String body;//短信内容
    String address;//短信手机号

    public void testresms() {

        new Thread() {
            @SuppressLint("NewApi")
            public void run() {
                try {
                    nManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    address = "+86 18310177830";
                    body = "您的短信验证码是";
                    Thread.sleep(1 * 1000);
                    ContentResolver resolver = getContext().getContentResolver();
                    Uri uri = Uri.parse("content://sms/");
                    ContentValues values = new ContentValues();
                    values.put("address", address);
                    values.put("date", System.currentTimeMillis());
                    values.put("type", "1");
                    values.put("body", body);
                    resolver.insert(uri, values);
                    Uri smsToUri = Uri.parse("smsto://" + address);
                    Intent mIntent = new Intent();
                    //					mIntent.setClassName("com.android.mms",
//							"com.android.mms.ui.ConversationList"); // 包名和类名（短信列表）
                    mIntent.setClassName("com.android.mms",
                            "com.android.mms.ui.ComposeMessageActivity"); // 包名和类名（短信详情）
                    mIntent.setType("vnd.android-dir/mms-sms");
                    mIntent.setData(Uri.parse("content:" + address));//此为号码
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (android.os.Build.VERSION.SDK_INT >= 11) {
                            PendingIntent pIntent = PendingIntent.getActivity(
                                    getContext(), 0, mIntent, 0);
                        // 设置通知的标题和内容
                        notification = new Notification.Builder(
                                getContext())
                                .setContentTitle(address)
                                .setContentText(body)
                                .setSmallIcon(
                                        android.R.drawable.stat_notify_chat)
                                .setContentIntent(pIntent).build();
                    }
                    // 显示时间
                    long when = System.currentTimeMillis();
                    notification.tickerText = body; // 显示在状态栏中的文字
                    notification.when = when; // 设置来通知时的时间
                    notification.flags |= Notification.FLAG_AUTO_CANCEL; // 点击清除按钮或点击通知后会自动消失
                    notification.defaults = Notification.DEFAULT_SOUND;// 设置默认铃声
                    notification.defaults = Notification.DEFAULT_VIBRATE;// 设置默认震动
                    notification.defaults = Notification.DEFAULT_ALL; // 设置铃声震动
                    nManager.notify(0, notification);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}