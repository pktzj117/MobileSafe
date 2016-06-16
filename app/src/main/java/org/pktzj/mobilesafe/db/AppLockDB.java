package org.pktzj.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pktzj on 2016/6/10.
 */

public class AppLockDB extends SQLiteOpenHelper{
    public AppLockDB(Context context) {
        super(context, "applocktb", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE applocktb(_id INTEGER PRIMARY KEY AUTOINCREMENT,packName TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE applocktb");
        onCreate(db);
    }
}
