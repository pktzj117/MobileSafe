package org.pktzj.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.engine.TaskManagerEngine;
import org.pktzj.mobilesafe.receiver.TaskCleanReceiver;

import java.util.Timer;
import java.util.TimerTask;

public class AppWidgetService extends Service {
    private Timer timer;
    private AppWidgetManager awm;
    private TimerTask task;

    public AppWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        awm = AppWidgetManager.getInstance(AppWidgetService.this);
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                ComponentName componentName = new ComponentName(getApplicationContext(), TaskCleanReceiver.class);

                //远程桌面
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.appwidget);
                Log.d("appwidget", "更新数据");
                //运行中软件的个数
                int availAPPNUM = TaskManagerEngine.getAvailAPKInfo(AppWidgetService.this).size();

                //可用内存
                long availMemSize = TaskManagerEngine.getAvailMemSize(AppWidgetService.this);

                //总内存
                long totalMemSize = TaskManagerEngine.getTotalMemSize(AppWidgetService.this);

                //给RemoteViews的子组件赋值
                views.setTextViewText(R.id.process_count, "正在运行软件:" + availAPPNUM);
                views.setTextViewText(R.id.process_memory, "可用内存:" + Formatter.formatFileSize(AppWidgetService.this, availMemSize));
                Intent intent = new Intent("org.pktzj.mobilesafe.receiver.WidgetTaskClean");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(AppWidgetService.this, 0, intent, 0);
                //给widget按钮加点击事件
                views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

                awm.updateAppWidget(componentName, views);
            }
        };
        //每隔5秒更新
        timer.schedule(task, 0, 5 * 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer = null;
        task = null;
    }
}
