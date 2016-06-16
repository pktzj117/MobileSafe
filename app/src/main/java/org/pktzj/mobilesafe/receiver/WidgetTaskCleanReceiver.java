package org.pktzj.mobilesafe.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.pktzj.mobilesafe.domain.TaskBean;
import org.pktzj.mobilesafe.engine.TaskManagerEngine;

import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class WidgetTaskCleanReceiver extends BroadcastReceiver {
    public WidgetTaskCleanReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        List<TaskBean> availAPKInfo = TaskManagerEngine.getAvailAPKInfo(context);
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (TaskBean bean : availAPKInfo) {
            if (!bean.getPackName().equals(context.getPackageName())) {
                am.killBackgroundProcesses(bean.getPackName());
                Log.d("lockscreenclear","kill " + bean.getPackName());
            }
        }
    }
}
