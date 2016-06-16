package org.pktzj.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import org.pktzj.mobilesafe.domain.TaskBean;
import org.pktzj.mobilesafe.engine.TaskManagerEngine;

import java.util.List;

public class LockScreenClearService extends Service {
    private LockScreenBroadCast lockScreenBroadCast;

    public LockScreenClearService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public class LockScreenBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            List<TaskBean> availAPKInfo = TaskManagerEngine.getAvailAPKInfo(context);
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            for (TaskBean bean : availAPKInfo) {
                if (!bean.getPackName().equals(getPackageName())) {
                    am.killBackgroundProcesses(bean.getPackName());
                    Log.d("lockscreenclear","kill " + bean.getPackName());
                }
            }
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("LockScreenBroadCast", "onCreate");
        lockScreenBroadCast = new LockScreenBroadCast();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(lockScreenBroadCast, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("LockScreenBroadCast", "onDestroy");
        unregisterReceiver(lockScreenBroadCast);
    }
}
