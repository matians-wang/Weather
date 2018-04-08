package com.test.carweather.widget;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

import com.test.carweather.CarWeatherService;
import com.test.carweather.HomeActivity;
import com.test.carweather.R;
import com.test.carweather.WeatherApplication;
import com.test.carweather.entity.WeatherInfoEntity;
import com.test.carweather.entity.WidgetInfo;
import com.test.carweather.utils.CarWeatherUtils;
import com.test.carweather.utils.Check;
import com.test.carweather.utils.DialogUtil;
import com.test.carweather.utils.MyLogConfig;
import com.test.carweather.utils.NetWork;
import com.test.carweather.utils.TimeUtils;

public class WeatherAppWidgetProvider extends AppWidgetProvider {

    private final static String TAG = "WeatherAppWidgetProvider";
    private final static boolean DEBUG = MyLogConfig.DEBUG;
    private static boolean networkFlag = false;
    private static List<WeatherInfoEntity> mWeatherEntities = new ArrayList<WeatherInfoEntity>();
    private static WeatherInfoEntity mWeatherEntity;
    private static AlertDialog netDialog;

    public static final String ACTION_UPDATE_LAUNCHER = "com.test.intent.action.UPDATE_LAUNCHER";
    private static final String WIDGET_CLICKED = "widgetClicked";
    private static final String HOME_KEY_CLICKED = "test.intent.action.homekey";

    @Override
    public void onDisabled(Context context) {
        if (DEBUG) { Log.d(TAG, "WeatherWidget onDisabled called"); }
        super.onDisabled(context);
        Intent intent = new Intent(context, CarWeatherService.class);
        context.stopService(intent);
    }

    @Override
    public void onEnabled(Context context) {
        if (DEBUG) { Log.d(TAG, "WeatherWidget onEnabled called"); }
        super.onEnabled(context);
        Intent serviceIntent = new Intent(context, CarWeatherService.class);
        context.startService(serviceIntent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
        if (DEBUG) { Log.d(TAG, "WeatherWidget onUpdate called."); }
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Intent serviceIntent = new Intent(context, CarWeatherService.class);
        serviceIntent.setAction(CarWeatherService.ACTION_UPDATE_LOCATION);
        context.startService(serviceIntent);

        // Update each requested appWidgetId
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget_simple);
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this
        // provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            Intent intent = new Intent(context, getClass()).setAction(WIDGET_CLICKED);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_weather, pi);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    public static void updateWeatherWidget(Context context) {
        Log.d(TAG, "updateWeatherWidget");
        updateWidgetUI(context);
    }

    public static void updateWidget(WeatherInfoEntity mWeather) {
        Log.d(TAG, "updateWidget");
        mWeatherEntity = mWeather;
        updateWidgetUI(WeatherApplication.getContext());
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        Log.d(TAG, "Weather widget receive : action =" + action);
        if (WIDGET_CLICKED.equals(action)) {
             if (!NetWork.isAvailable(context)) {
                 netDialog = DialogUtil.showNetDialog(context, new OnClickListener() {

                     @Override
                     public void onClick(View v) {
                         goToWifiSetting(context);
                     }
                 });
             }else {
                 startApp(context);
             }
        }

        if (HOME_KEY_CLICKED.equals(action)) {
            if (netDialog != null && netDialog.isShowing()) {
                netDialog.dismiss();
            }
        }
    }

    private void goToWifiSetting(Context context) {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT > 10) {
            intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void startApp(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static void updateWidgetUI(Context context) {
         Log.d(TAG, "updateWidgetUI");
        RemoteViews views = null;
        WidgetInfo mDatas = new WidgetInfo(mWeatherEntity);
        if (mDatas.hasData) {
            views = new RemoteViews(context.getPackageName(), R.layout.widget_simple);
            views.setViewVisibility(R.id.widget_content, View.VISIBLE);
            views.setViewVisibility(R.id.widget_empty, View.GONE);

            views.setImageViewResource(R.id.widget_basic_img, CarWeatherUtils.getWeatherIconId(mDatas.weatherIconId));
            views.setTextViewText(R.id.widget_basic_location, mDatas.cityName);
            views.setTextViewText(R.id.widget_basic_tempter, mDatas.temp);
            views.setTextViewText(R.id.widget_basic_state, mDatas.weatherStatus);

        } else {
            views = new RemoteViews(context.getPackageName(), R.layout.widget_simple);
            views.setViewVisibility(R.id.widget_content, View.GONE);
            views.setViewVisibility(R.id.widget_empty, View.VISIBLE);
        }

        pushUpdate(context, null, views);
    }

    private static void pushUpdate(Context context, int[] appWidgetIds, RemoteViews views) {
        Log.d(TAG, "pushUpdate");
        Intent intent = new Intent(context, WeatherAppWidgetProvider.class).setAction(WIDGET_CLICKED);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_weather, pi);

        // Update specific list of appWidgetIds if given, otherwise default to all
        final AppWidgetManager gm = AppWidgetManager.getInstance(context);
        if (appWidgetIds != null) {
            gm.updateAppWidget(appWidgetIds, views);
        } else {
            gm.updateAppWidget(new ComponentName(context, WeatherAppWidgetProvider.class), views);
        }
    }
}
