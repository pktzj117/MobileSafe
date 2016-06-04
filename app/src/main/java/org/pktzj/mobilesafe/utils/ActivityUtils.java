package org.pktzj.mobilesafe.utils;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by pktzj on 2016/5/30.
 */

public class ActivityUtils {

    public static void startActivity(Activity context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
    }
}
