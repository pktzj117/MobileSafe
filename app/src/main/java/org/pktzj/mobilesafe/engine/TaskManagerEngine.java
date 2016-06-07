package org.pktzj.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import org.pktzj.mobilesafe.domain.TaskBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pktzj on 2016/6/6.
 */

public class TaskManagerEngine {

    public static List<TaskBean> getAvailAPKInfo(Context context) {
        List<TaskBean> TaskDatas = new ArrayList<TaskBean>();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcessInfos) {
            TaskBean bean = new TaskBean();
            //获取包名
            String processName = info.processName;
            bean.setPackName(processName);
            PackageInfo packageInfo = null;
            //有些应用没有名字不显示
            try {
                packageInfo = pm.getPackageInfo(processName, PackageManager.GET_ACTIVITIES);
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
            //设置图标
            bean.setIcon(packageInfo.applicationInfo.loadIcon(pm));
            //设置应用名称
            bean.setAppName(packageInfo.applicationInfo.loadLabel(pm).toString());
            //判断应用类型  用户应用和系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                bean.setUSEAPP(false);
                bean.setROMAPP(true);
            } else {
                bean.setUSEAPP(true);
                bean.setROMAPP(false);
            }

            //获取内存大小
            Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
            long totalsize = memoryInfo[0].getTotalPrivateDirty() * 1024;
            bean.setSize(totalsize);
            TaskDatas.add(bean);
        }
        return TaskDatas;
    }

    /**
     * @param context
     * @return 返回总内存
     */
    public static long getTotalMemSize(Context context) {
        long size = 0;

        try {
            File file = new File("proc/meminfo");
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String totalMemInfo = reader.readLine();
            int startIndex = totalMemInfo.indexOf(":");
            int endIndex = totalMemInfo.indexOf("k");
            //单位为KB
            totalMemInfo = totalMemInfo.substring(startIndex + 1, endIndex).trim();
            size = Long.parseLong(totalMemInfo) * 1024;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * @param context
     * @return 返回空闲内存
     */
    public static long getAvailMemSize(Context context) {
        long size = 0;

        try {
            File file = new File("proc/meminfo");
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            reader.readLine();
            String totalMemInfo = reader.readLine();
            int startIndex = totalMemInfo.indexOf(":");
            int endIndex = totalMemInfo.indexOf("k");
            //单位为KB
            totalMemInfo = totalMemInfo.substring(startIndex + 1, endIndex).trim();
            size = Long.parseLong(totalMemInfo) * 1024;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }
}
