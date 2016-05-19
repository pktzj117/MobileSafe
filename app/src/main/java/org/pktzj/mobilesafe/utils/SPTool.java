package org.pktzj.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by pktzj on 2016/5/19.
 */
public class SPTool {
    public static void putSring(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.SPFILE, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();//保存数据
    }
    public static String getSring(Context context, String key,String defValue) {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.SPFILE, Context.MODE_PRIVATE);
        return sp.getString(key,defValue);//保存数据
    }
    public static void putboolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.SPFILE, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();//保存数据
    }
    public static boolean getboolean(Context context, String key,boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.SPFILE, Context.MODE_PRIVATE);
        return sp.getBoolean(key,defValue);//保存数据
    }
}
