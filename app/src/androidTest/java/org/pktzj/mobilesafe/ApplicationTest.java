package org.pktzj.mobilesafe;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.telephony.SmsManager;
import android.test.ApplicationTestCase;
import android.text.format.Formatter;
import android.util.Log;

import org.pktzj.mobilesafe.dao.AppLockDAO;
import org.pktzj.mobilesafe.dao.BlackDAO;
import org.pktzj.mobilesafe.domain.APPBean;
import org.pktzj.mobilesafe.domain.BlackBean;
import org.pktzj.mobilesafe.domain.ContactBean;
import org.pktzj.mobilesafe.engine.APPMangerEngine;
import org.pktzj.mobilesafe.engine.AntivirusEngine;
import org.pktzj.mobilesafe.engine.PhoneLocationEngine;
import org.pktzj.mobilesafe.engine.TaskManagerEngine;
import org.pktzj.mobilesafe.engine.readContactEngine;
import org.pktzj.mobilesafe.service.LostFindService;
import org.pktzj.mobilesafe.utils.MD5Utils;
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
        String safenum = SPTool.getString(getContext(), MyConstants.SAFENUM, "");

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
    //获得黑名单号码的拦截模式
    public void testgetMode() {
        BlackDAO blackDAO = new BlackDAO(getContext());
        int mode = blackDAO.getMode("110");
        Log.d(MyConstants.TAG, "mode: " + mode);
    }

    //手机号码归属地
    public void testmobilelocation() {
        String number = "15273200836";
        String location = PhoneLocationEngine.mobileQuery(getContext(), number);
        Log.d("MobileLocation", "location: " + location);
    }

    //固话号码归属地
    public void testphonelocation() {
        String number = "8452768";
        String location = PhoneLocationEngine.phoneQuery(getContext(), number);
        Log.d("PhoneLocation", "location: " + location);
    }

    //
    public void testgetAllAPK() {
        List<APPBean> allAPK = APPMangerEngine.getAllAPK(getContext());
        for (APPBean appBean : allAPK) {
            Log.d("APP", appBean.toString());
        }
    }

    public void testgetAllMemSize() {
        long totalMemSize = TaskManagerEngine.getTotalMemSize(getContext());
        Log.d("allMemSize", Formatter.formatFileSize(getContext(),totalMemSize));
    }
    public void testgetAvailMemSize() {
        long totalMemSize = TaskManagerEngine.getAvailMemSize(getContext());
        Log.d("AvailMemSize", Formatter.formatFileSize(getContext(),totalMemSize));
    }


    public void testaddapplock() {
        AppLockDAO dao = new AppLockDAO(getContext());
        long insert = dao.insert("org.pktzj.mobliesafe");
    }

    public void testgetallapplock() {
        AppLockDAO dao = new AppLockDAO(getContext());
        List<String> allLock = dao.getAllLock();
        for (String str : allLock) {
            Log.d("applock", str);
        }
    }

    public void testeremoveapplock() {
        AppLockDAO dao = new AppLockDAO(getContext());
        dao.remove("org.pktzj.mobliesafe");
    }

    public void testisvirus() {
        boolean virus = AntivirusEngine.isVirus(getContext(), "a2bd62c99207956348986bf1357dea01");
        Log.d("isvirus", virus + "");
        String fileMD5 = MD5Utils.getFileMD5("/data/app/SmokeTest/SmokeTest.apk");
        Log.d("fileMD5", "fileMD5: " + fileMD5);
        virus = AntivirusEngine.isVirus(getContext(), fileMD5);
        Log.d("isvirus", virus + "");
    }

    public void testpathmd5() {
        String md5 = MD5Utils.getFileMD5("/data/app/SmokeTest/SmokeTest.apk");
        Log.d("SmokeTest.apk MD5: ", md5);
    }

    public void testgetallvirus() {
        List<String> list = AntivirusEngine.getall(getContext());
        for (String str : list) {
            Log.d("virus md5", "virus md5: " + str);
        }
    }
    public void testaddmd5() {
        long l = AntivirusEngine.addVirus(getContext(), "58E2A92F712012C5361628053A6A4569", "SmokeTest.apk", "aaaaaa");
        l = AntivirusEngine.addVirus(getContext(), "85E3FF8782BF69BC95443E19E4A2D6A5", "Weibo.apk", "微博测试程序用");
        Log.d("SmokeTest.apk MD5: ", l + "");
    }
}