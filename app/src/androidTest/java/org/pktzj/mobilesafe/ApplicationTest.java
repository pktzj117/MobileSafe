package org.pktzj.mobilesafe;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.telephony.SmsManager;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.pktzj.mobilesafe.dao.BlackDAO;
import org.pktzj.mobilesafe.domain.BlackBean;
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
//        List<ContactBean> contacts = readContactEngine.readContarts(getContext());
        List<ContactBean> contacts = readContactEngine.readCallLog(getContext());
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

    public void testaddsql() {
        BlackDAO blackDAO = new BlackDAO(getContext());
        for (int i = 0; i <200 ; i++) {
            long _id = blackDAO.adddata("1234567" + i, 1);
            Log.d(MyConstants.TAG, "_id: "+ _id);
        }
    }
    public void testgetAll() {
        BlackDAO blackDAO = new BlackDAO(getContext());
        List<BlackBean> blacknums = blackDAO.getAllDatas();
        for (BlackBean blacknum : blacknums) {
            Log.d(MyConstants.TAG, blacknum.toString());
        }
    }

    public void testdelete() {
        BlackDAO blackDAO = new BlackDAO(getContext());
        int result = blackDAO.delete("1234567i");
        Log.d(MyConstants.TAG, "num: " + result);
    }

    public void testdupdate() {
        BlackDAO blackDAO = new BlackDAO(getContext());
        long _id = blackDAO.dupdate("12345670", 2);
        Log.d(MyConstants.TAG, "_id: " + _id);
    }

    public void testgetData() {
        BlackDAO blackDAO = new BlackDAO(getContext());
        List<BlackBean> datas = blackDAO.getDatas(0, 20);
        for (BlackBean data : datas) {
            Log.d(MyConstants.TAG, data.toString());
        }
    }

    public void testgetMode() {
        BlackDAO blackDAO = new BlackDAO(getContext());
        int mode = blackDAO.getMode("110");
        Log.d(MyConstants.TAG, "mode: " + mode);
    }
}