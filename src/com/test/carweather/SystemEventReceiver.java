package com.test.carweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class SystemEventReceiver extends BroadcastReceiver {
    private final static String TAG = "Weather_" + SystemEventReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        if (action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals("intent.action.PACKAGE_LATER_SCANNED")) {
            Log.d(TAG, "onReceive:" + action);
            startService(context);
        }
    }

    private void startService(Context context) {
        Intent i = new Intent(context, CarWeatherService.class);
        context.startService(i);
    }
}
