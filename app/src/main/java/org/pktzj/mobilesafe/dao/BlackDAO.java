package org.pktzj.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.pktzj.mobilesafe.db.BlackDB;
import org.pktzj.mobilesafe.domain.BlackBean;
import org.pktzj.mobilesafe.utils.MyConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pktzj on 2016/5/26.
 */
public class BlackDAO {
    private BlackDB blackDB;

    public BlackDAO(Context context) {
        this.blackDB = new BlackDB(context);
    }

    public long adddata(BlackBean blackBean) {
        SQLiteDatabase db = blackDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MyConstants.PHONE, blackBean.getPhone());
        values.put(MyConstants.MODE, blackBean.getMode());
        long insert = db.insert(MyConstants.BLACKTB, null, values);
        db.close();
        return insert;
    }

    public long adddata(String phone, int mode) {
        SQLiteDatabase db = blackDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MyConstants.PHONE, phone);
        values.put(MyConstants.MODE, mode);
        long insert = db.insert(MyConstants.BLACKTB, null, values);
        db.close();
        return insert;
    }

    public List<BlackBean> getDatas(int start,int num) {
        List<BlackBean> blacknums = new ArrayList<BlackBean>();
        BlackBean blacknum = null;
        SQLiteDatabase db = blackDB.getReadableDatabase();

        Cursor cursor = db.query(MyConstants.BLACKTB, null, null, null, null, null,"_id DESC", start + "," + num);
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            String phone = cursor.getString(cursor.getColumnIndex(MyConstants.PHONE));
            int mode = cursor.getInt(cursor.getColumnIndex(MyConstants.MODE));
            blacknum = new BlackBean(_id,phone,mode);
            blacknums.add(blacknum);
        }
        return blacknums;
    }

    public List<BlackBean> getAllDatas() {
        List<BlackBean> blacknums = new ArrayList<BlackBean>();
        BlackBean blacknum = null;
        SQLiteDatabase db = blackDB.getReadableDatabase();

        Cursor cursor = db.query(MyConstants.BLACKTB, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String phone = cursor.getString(cursor.getColumnIndex(MyConstants.PHONE));
            int mode = cursor.getInt(cursor.getColumnIndex(MyConstants.MODE));
            blacknum = new BlackBean(phone,mode);
            blacknums.add(blacknum);
        }
        return blacknums;
    }

    /**
     * 删除电话号码为phone的条目
     * @param phone 删除条件电话号码
     * @return 变动条目数
     */
    public int delete(String phone) {
        SQLiteDatabase db = blackDB.getWritableDatabase();
        int result = db.delete(MyConstants.BLACKTB, "phone = ?", new String[]{phone});
        return result;
    }


    /**
     * 修改phone的mode并移到数据库的最后
     * @param phone 条件
     * @param mode  修改的模式
     * @return  修改后条目的_id
     */
    public long dupdate(String phone, int mode) {
        //先删除数据 在重新添加,会使数据在最后
        int result = delete(phone);
        long adddata = adddata(phone, mode);
        return adddata;
    }
    /**
     * 修改phone的mode并移到数据库的最后
     * @param blackBean 条件
     * @return  修改后条目的_id
     */
    public long dupdate(BlackBean blackBean) {
        //先删除数据 在重新添加,会使数据在最后
        int result = delete(blackBean.getPhone());
        long adddata = adddata(blackBean);
        return adddata;
    }
}
