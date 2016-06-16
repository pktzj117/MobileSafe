package org.pktzj.mobilesafe.engine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pktzj on 2016/6/13.
 */

public class AntivirusEngine {
    public static boolean isVirus(Context context, String md5) {
        md5 = md5.toLowerCase();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir().toString() + "/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("SELECT 1 FROM datable WHERE md5 = ?", new String[]{md5});
        if (cursor.getCount() != 0) {
            cursor.close();
            db.close();
            return true;
        }
        return false;
    }

    public static List<String> getall(Context context) {
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir().toString() + "/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("SELECT md5 FROM datable", null);
        while (cursor.moveToNext()) {
            String md5 = cursor.getString(cursor.getColumnIndex("md5"));
            list.add(md5);
        }
        cursor.close();
        db.close();
        return list;
    }
    public static long addVirus(Context context, String md5,String name,String desc) {
        md5 = md5.toLowerCase();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir().toString() + "/antivirus.db", null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues value = new ContentValues();
        value.put("md5",md5);
        value.put("type",6f);
        value.put("name",name);
        value.put("desc",desc);
        long insert = db.insert("datable", null, value);
        db.close();
        return insert;
    }
}
