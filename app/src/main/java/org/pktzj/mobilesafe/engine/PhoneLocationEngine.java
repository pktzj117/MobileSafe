package org.pktzj.mobilesafe.engine;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pktzj on 2016/5/30.
 */

public class PhoneLocationEngine {
    /**
     * 手机号码归属地查询
     *
     * @param context
     * @param number
     * @return
     */
    public static String mobileQuery(Context context, String number) {
        String location = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir().toString() + "/address.db", null, SQLiteDatabase.OPEN_READONLY);
        //通过外键找到归属地
        Cursor cursor = db.rawQuery("SELECT location FROM data2 WHERE id = (SELECT outKey FROM data1 WHERE id = ?)", new String[]{number.substring(0, 7)});

        if (cursor.moveToNext()) {
            location = cursor.getString(0);
        }

        return location;
    }


    /**
     * 固话号码归属地
     *
     * @param context
     * @param number
     * @return
     */
    public static String phoneQuery(Context context, String number) {
        String location = null;
        String area = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir().toString() + "/address.db", null, SQLiteDatabase.OPEN_READONLY);
        //两位区号 开头只有  1 , 2
        if (number.charAt(1) == '1' || number.charAt(1) == '2') {
            area = number.substring(1, 3);
        } else {//三位区号
            area = number.substring(1, 4);
        }

        //找到归属地
        Cursor cursor = db.rawQuery("SELECT location FROM data2 WHERE area = ?", new String[]{area});

        if (cursor.moveToNext()) {
            location = cursor.getString(0);
        }

        return location;
    }

    /**
     * 查询服务号码 如：110匪警
     *
     * @param phoneNumber
     * @return
     */
    public static String serviceNumberQuery(String phoneNumber) {
        String res = "";
        if (phoneNumber.equals("110")) {
            res = "匪警";
        } else if (phoneNumber.equals("10086")) {
            res = "中国移动";
        }
        return res;
    }

    /**
     * 电话归属地业务类的封装类
     *
     * @param phoneNumber
     * @param context
     * @return
     */
    public static String locationQuery(Context context,String phoneNumber) {
        String location = null;
        /**
         * 判断num. 1 手机号  2 固定号码 3 服务号
         *
         */
        Pattern pattern = Pattern.compile("1{1}[3857]{1}[0-9]{9}");
        Matcher matcher = pattern.matcher(phoneNumber);
        boolean matches = matcher.matches();
        if (matches) {
            //是手机号
            location = mobileQuery(context, phoneNumber);
        } else if (phoneNumber.length() >= 11) {
            //固定号码
            //如果是固定号码
            location = phoneQuery(context, phoneNumber);
        } else {
            //如果是服务号码
            location = serviceNumberQuery(phoneNumber);
        }
        return location;
    }
}
