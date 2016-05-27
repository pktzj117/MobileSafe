package org.pktzj.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pktzj on 2016/5/26.
 */
public class BlackDB extends SQLiteOpenHelper {

    /**
     * 初始化版本信息
     * @param context
     */
    public BlackDB(Context context) {
        super(context, "blacknum", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //只初始化一次
        //创建表
        db.execSQL("CREATE TABLE blacktb(_id INTEGER PRIMARY KEY AUTOINCREMENT,phone TEXT,mode INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE blacktb");
        onCreate(db);
    }
}
