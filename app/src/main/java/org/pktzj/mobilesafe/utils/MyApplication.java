package org.pktzj.mobilesafe.utils;

import android.app.Application;

import org.xutils.x;

/**
 * Created by pktzj on 2016/5/17.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);//xUtil初始化
    }
}
