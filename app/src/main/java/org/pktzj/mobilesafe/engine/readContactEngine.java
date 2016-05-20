package org.pktzj.mobilesafe.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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
                    "WHERE raw_contact_id=?", new String[]{id}, null);
            while (cursor.moveToNext()) {
                String data1 = cursor.getString(cursor.getColumnIndex("data1"));
                String mimetype = cursor.getString(cursor.getColumnIndex("mimetype"));


                if (mimetype.equals("vnd.android.cursor.item/name")) {
                    Log.d(MyConstants.TAG,"第" +id + "个用户：名字：" + data1);
                    contact.setName(data1);
                } else if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                    Log.d(MyConstants.TAG,"第" +id + "个用户：电话：" + data1);
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
