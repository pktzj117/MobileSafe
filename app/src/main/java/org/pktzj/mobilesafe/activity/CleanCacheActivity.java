package org.pktzj.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.domain.APPBean;
import org.pktzj.mobilesafe.engine.APPMangerEngine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CleanCacheActivity extends Activity {

    private static final int SCANNING = 1;
    private static final int FINISHED = 2;
    private static final int CLEANCACHE = 3;
    private TextView tv_cache;
    private ProgressBar pb_cache;
    private LinearLayout ll_cache;
    private PackageManager mPackageManager;
    private List<CacheInfo> infos = new ArrayList<>();
    private int count;
    private List<APPBean> allAPK;
    private boolean enbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        mPackageManager = getPackageManager();
        scanallcache();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCANNING:
                    ll_cache.setVisibility(View.GONE);
                    tv_cache.setVisibility(View.VISIBLE);
                    pb_cache.setVisibility(View.VISIBLE);
                    tv_cache.setText("正在扫描:" + msg.obj);
                    break;
                case FINISHED:
                    pb_cache.setVisibility(View.GONE);
                    if (infos.size() == 0) {
                        ll_cache.setVisibility(View.GONE);
                        tv_cache.setVisibility(View.VISIBLE);
                        tv_cache.setText("没有缓存,你的手机很干净.");
                    } else {
                        tv_cache.setVisibility(View.GONE);
                        ll_cache.setVisibility(View.VISIBLE);
                        ll_cache.removeAllViews();
                        for (CacheInfo info : infos) {
                            View view = View.inflate(CleanCacheActivity.this, R.layout.item_cache_listview, null);
                            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon_cleancache);
                            TextView tv_appname = (TextView) view.findViewById(R.id.tv_apkname_cleancache);
                            TextView tvSize = (TextView) view.findViewById(R.id.tv_size_cleancache);
                            iv_icon.setImageDrawable(info.icon);
                            tv_appname.setText(info.name);
                            tvSize.setText(info.cacheSize);
                            ll_cache.addView(view,0);
                        }
                        enbutton = true;
                    }
                    break;
                case CLEANCACHE:
                    ll_cache.setVisibility(View.GONE);
                    pb_cache.setVisibility(View.GONE);
                    tv_cache.setVisibility(View.VISIBLE);
                    tv_cache.setText(msg.obj + "");
                    break;
                default:
                    break;
            }
        }
    };

    private void scanallcache() {
        new Thread() {


            @Override
            public void run() {
                handler.obtainMessage(SCANNING).sendToTarget();
                allAPK = APPMangerEngine.getAllAPK(CleanCacheActivity.this);
                for (APPBean appBean : allAPK) {
                    getAppCacheSize(appBean.getPackName());
                    SystemClock.sleep(20);
                }
            }
        }.start();
    }

    private void getAppCacheSize(String packageName) {
        try {
            Method myUserId = UserHandle.class.getDeclaredMethod("myUserId");
            int userID = (Integer) myUserId.invoke(mPackageManager,null);
            Method getPackageSizeInfo = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, int.class, IPackageStatsObserver.class);
            getPackageSizeInfo.invoke(mPackageManager, packageName, userID, new MyObserver());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 缓存信息的封装类
     *
     * @author Administrator
     */
    private class CacheInfo {
        Drawable icon;
        String name;
        long cacheSizeLong;//缓存大小
        String cacheSize;// 格式化后字符串

        public CacheInfo(Drawable icon, String name, long cacheSizeLong, String cacheSize) {
            this.icon = icon;
            this.name = name;
            this.cacheSizeLong = cacheSizeLong;
            this.cacheSize = cacheSize;
        }
    }

    private int counts = 0;

    private class MyObserver extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            count++;

            long cacheSize = pStats.cacheSize;
            String packageName = pStats.packageName;
            String formatFileSize = Formatter.formatFileSize(getApplicationContext(), cacheSize);
            Message message = handler.obtainMessage(SCANNING);
            message.obj = packageName;
            handler.sendMessage(message);

            if (cacheSize > 0) {

                try {
                    PackageInfo packageInfo = mPackageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                    CacheInfo cacheInfo = new CacheInfo(packageInfo.applicationInfo.loadIcon(mPackageManager),
                            packageInfo.applicationInfo.loadLabel(mPackageManager).toString(),
                            cacheSize, formatFileSize);
                    infos.add(cacheInfo);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (count == allAPK.size()) {
                handler.obtainMessage(FINISHED).sendToTarget();
            }
        }
    }

    private void initView() {
        setContentView(R.layout.activity_clean_cache);
        tv_cache = (TextView) findViewById(R.id.tv_loadcache_cleancache);
        pb_cache = (ProgressBar) findViewById(R.id.pb_loadcache_cleancache);
        ll_cache = (LinearLayout) findViewById(R.id.ll_cachelist_cleancache);
    }

    public void cleancache(View view) {
        if (enbutton) {
            enbutton = false;
            cleanallcache();
        }
    }

    private void cleanallcache() {
        try {
            Method freeStorageAndNotify = PackageManager.class.getDeclaredMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
            freeStorageAndNotify.invoke(mPackageManager,Long.MAX_VALUE, new MyCleanCache());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    class MyCleanCache extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            Message message = handler.obtainMessage(CLEANCACHE);
            long totalSize = 0;//累计缓存大小
            for (CacheInfo info : infos) {
                totalSize += info.cacheSizeLong;
            }
            infos.clear();
            message.obj = "垃圾文件清理成功，本次为您节省了" + Formatter.formatFileSize(getApplicationContext(), totalSize);
            handler.sendMessage(message);
        }
    }
}
