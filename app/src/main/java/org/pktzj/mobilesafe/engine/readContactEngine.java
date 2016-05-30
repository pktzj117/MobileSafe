package org.pktzj.mobilesafe.engine;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.pktzj.mobilesafe.domain.ContactBean;
import org.pktzj.mobilesafe.utils.MyConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pktzj on 2016/5/20.
 */
public class readContactEngine {


    /**
     * 获取短信记录
     * @param context
     * @return
     */
    public static List<ContactBean> readSMSLog(Context context) {
        List<ContactBean> SMSLog = new ArrayList<ContactBean>();
        ContactBean contactBean = null;

        Cursor cursor = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            cursor = context.getContentResolver().query(Telephony.Sms.CONTENT_URI, new String[]{"person", "address"}, null, null, null);
        } else {
            cursor = context.getContentResolver().query(Uri.parse("content://sms"), new String[]{"person", "address"}, null, null, null);
        }
        while (cursor.moveToNext()) {
            contactBean = new ContactBean();
            String name = cursor.getString(cursor.getColumnIndex("person"));
            String phone = cursor.getString(cursor.getColumnIndex("address"));
            contactBean.setName(name);
            contactBean.setNumber(phone);
            SMSLog.add(contactBean);
        }
        cursor.close();
        return SMSLog;
    }
    /**
     *
     * 获取通话记录
     * @param context
     * @return
     */
    public static List<ContactBean> readCallLog(Context context) {
        List<ContactBean> callLog = new ArrayList<ContactBean>();
        ContactBean contactBean = null;

        Cursor cursor;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(MyConstants.TAG, "没有权限");
        }
        cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{"name","number"}, null, null, null);
        while (cursor.moveToNext()) {
            contactBean = new ContactBean();
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String phone = cursor.getString(cursor.getColumnIndex("number"));
            contactBean.setName(name);
            contactBean.setNumber(phone);
            callLog.add(contactBean);
        }
        cursor.close();
        return callLog;
    }


    /**
     * 读取手机联系人
     */
    public static List<ContactBean> readContarts(Context context) {
        List<ContactBean> contacts = new ArrayList<ContactBean>();
        Uri uriContacts = Uri.parse("content://com.android.contacts/contacts");
        Uri uriDatas = Uri.parse("content://com.android.contacts/data");
        //获取联系人id
        Cursor id_cursor = context.getContentResolver().query(uriContacts, new String[]{"_id"}, null, null, null);
        //查表data循环读取所有联系人
        while (id_cursor.moveToNext()) {
            ContactBean contact = new ContactBean();
            String id = id_cursor.getString(id_cursor.getColumnIndex("_id"));
            Cursor cursor = context.getContentResolver().query(uriDatas, new String[]{"data1", "mimetype"},
                    "raw_contact_id=?", new String[]{id}, null);
            while (cursor.moveToNext()) {
                String data1 = cursor.getString(cursor.getColumnIndex("data1"));
                String mimetype = cursor.getString(cursor.getColumnIndex("mimetype"));


                if (mimetype.equals("vnd.android.cursor.item/name")) {
                    // Log.d(MyConstants.TAG,"第" +id + "个用户：名字：" + data1);
                    contact.setName(data1);
                } else if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                    //Log.d(MyConstants.TAG,"第" +id + "个用户：电话：" + data1);
                    contact.setNumber(data1);
                }
            }
            cursor.close();
            contacts.add(contact);
        }
        id_cursor.close();
        return contacts;
    }
}
