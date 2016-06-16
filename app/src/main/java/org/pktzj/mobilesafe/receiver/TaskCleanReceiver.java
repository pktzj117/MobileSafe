package org.pktzj.mobilesafe.receiver;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import org.pktzj.mobilesafe.service.AppWidgetService;

/**
 * Created by pktzj on 2016/6/8.
 */

public class TaskCleanReceiver extends AppWidgetProvider {
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent sIntent = new Intent(context, AppWidgetService.class);
        context.startService(sIntent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent sIntent = new Intent(context, AppWidgetService.class);
        context.stopService(sIntent);
    }
}
