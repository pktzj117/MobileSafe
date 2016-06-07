package org.pktzj.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import org.pktzj.mobilesafe.domain.APPBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pktzj on 2016/6/4.
 */

public class APPMangerEngine {
    /**
     * @return 返回SD卡可用空间
     */
    public static long getSDcardFree() {
        return Environment.getExternalStorageDirectory().getFreeSpace();
    }

    /**
     * @return 返回ROM的可用控件
     */
    public static long getRomFree() {
        return Environment.getDataDirectory().getFreeSpace();
    }

    public static List<APPBean> getAllAPK(Context context) {
        List<APPBean> applist = new ArrayList<>();
        APPBean appBean = null;
        //获取包管理器
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        for (PackageInfo info : installedPackages) {
            appBean = new APPBean();
            ApplicationInfo applicationInfo = info.applicationInfo;
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                appBean.setROMAPP(true);
                appBean.setSDAPP(false);
            } else{
                appBean.setSDAPP(true);
                appBean.setROMAPP(false);
            }
            appBean.setIcon(applicationInfo.loadIcon(packageManager));
            appBean.setAppName(applicationInfo.loadLabel(packageManager).toString());
            appBean.setPackName(applicationInfo.packageName.toString());
            appBean.setSize(new File(applicationInfo.sourceDir).length());
            applist.add(appBean);
        }
        return applist;
    }
}
