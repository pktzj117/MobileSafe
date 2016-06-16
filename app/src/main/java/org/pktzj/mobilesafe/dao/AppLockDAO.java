package org.pktzj.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.pktzj.mobilesafe.db.AppLockDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pktzj on 2016/6/10.
 */

public class AppLockDAO {
    public static final String TABLE = "applocktb";
    public static final String PACKNAME = "packname";
    private final AppLockDB lockDB;

    public AppLockDAO(Context context) {
        this.lockDB = new AppLockDB(context);
    }


    public long insert(String packname) {
        SQLiteDatabase db = lockDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packname", packname);
        long _id = db.insert(TABLE, null, values);
        db.close();
        return _id;
    }

    public List<String> getAllLock() {
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = lockDB.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + PACKNAME + " FROM " + TABLE, null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0));
        }
        cursor.close();
        return list;
    }

    public int remove(String packname) {
        SQLiteDatabase db = lockDB.getWritableDatabase();
        int result = db.delete(TABLE, PACKNAME + " = ?", new String[]{packname});
        return result;
    }
}
