package org.pktzj.mobilesafe.utils;


import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by pktzj on 2016/5/21.
 */
public class ServiceUtils {

    public static boolean serviceIsRunning(Context context, Class service) {

        //Log.d(MyConstants.TAG, "MyServiceName: " + service.getName());
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServices) {
            String name = serviceInfo.service.getClassName();
            //Log.d(MyConstants.TAG, "ServiceName: " + name);
            if (name.equals(service.getName())) {
                return true;
            }
        }
        return false;
    }
}
