package com.test.carweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.test.carweather.model.CityModel;
import com.test.carweather.model.ModelCallback;
import com.test.carweather.model.ModelManager;
import com.test.carweather.model.WeatherModel;
import com.test.carweather.utils.MyLocationManager;
import com.test.carweather.utils.MyLogConfig;
import com.test.carweather.utils.NetWork;

public class CarWeatherService extends Service {
    private static final String TAG = CarWeatherService.class.getSimpleName();

    public static final String ACTION_UPDATE_WEATHER = "com.test.intent.action.UPDATE_WEATHER";
    public static final String ACTION_UPDATE_CITY = "com.test.intent.action.UPDATE_CITY";
    public static final String ACTION_UPDATE_CITY_FINISHED = "com.test.intent.action.UPDATE_CITY_FINISHED";
    public static final String ACTION_SET_UPDATE_RATE = "com.test.intent.action.SET_UPDATE_RATE";
    public static final String ACTION_UPDATE_ALL_FINISHED = "com.test.intent.action.UPDATE_ALL_FINISHED";
    public static final String ACTION_UPDATE_TIME = "com.test.intent.action.UPDATE_TIME";
    public static final String ACTION_UPDATE_SELECTED_CITY = "com.test.intent.action.UPDATE_SELECTED_CITY";
    public static final String ACTION_UPDATE_ALARM = "com.test.intent.action.UPDATE_ALARM";
    public static final String ACTION_UPDATE_LOCATION = "com.test.intent.action.UPDATE_LOCATION";

    public static final int INTENT_FLAG_SINGLE = 0;
    public static final int INTENT_FLAG_MORE = 1;

    private static final int DEFAULT_UPDATE_RATE = 0;

    private static final int[] sInterval = { 1, 3, 6, 9, 12 };

    public final static int ALARM_START_TIME_WAIT = 5 * 60 * 1000; // 10s

    private static final int INTERVAL_UPDATE = 5 * 60 * 1000;

    private CarWeatherBInder mBinder = new CarWeatherBInder();
    private ModelCallback.WeatherCallback mCallback;

    private static final int LOCATION_TIMEOUT = 10000;
    private static final boolean LOCATION_IS_ONCE = true;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private MyLocationManager myLocationManager;

    private boolean isLocationReceived;
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

        }
    };
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            if (isLocationReceived) {
                Log.d(TAG, "timerService: location received");
                return;
            }else {
                Log.d(TAG, "timerService: location timeout");
                refreshLocation();
            }

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "WeatherService created.");
        startLocation();
        initAlarm();
        registBroadcastReceiver();
    }

    public void startLocation() {
        Log.d(TAG, "startLocation");
        if (myLocationManager == null) {
            initLocalLocation();
        }
        isLocationReceived = false;
        myLocationManager.recordLocation(true);

        mHandler.removeCallbacks(timerRunnable);
        mHandler.postDelayed(timerRunnable, 10 * 1000);
    }

    public void initLocalLocation() {
        Log.d(TAG, "initLocalLocation");
        myLocationManager = new MyLocationManager(getApplicationContext(), new MyLocationManager.Listener() {

            @Override
            public void onLocationReceived(Location location) {
                Log.d(TAG, "onLocationReceived");
                isLocationReceived = true;
                updateLocation(location);
                refreshLocation();
                //request location once
                myLocationManager.recordLocation(false);
            }

            @Override
            public void onLocationError() {
                Log.e(TAG, "onLocationError");
            }
        });
    }

    private void updateLocation(Location location) {
        double tempLatitude = location.getLatitude();
        double tempLongitude = location.getLongitude();
        if (tempLatitude != latitude || tempLongitude != longitude) {
            latitude = tempLatitude;
            longitude = tempLongitude;
        }
    }

    public void refreshLocation() {
        Log.d(TAG, "refreshLocation");
        if (latitude == 0.0 && longitude == 0.0) {
            Log.d(TAG, "latitude == 0.0 && longitude == 0.0");
            if (mCallback != null) {
                mCallback.onLocationComplete(null, false);
            } else {
                // update widget with last location
                ModelManager.getModel(CityModel.class).requestLocationGeocode();
            }
        } else {
            if (mCallback != null) {
                mCallback.onLocationChange(latitude, longitude);
            } else {
                ModelManager.getModel(CityModel.class).updateLocationCity(longitude, latitude);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "WeatherService started.");
        String action = intent.getAction();
        if (ACTION_UPDATE_ALARM.equals(action)) {
            if (NetWork.isAvailable(getApplicationContext())) {
                if (mCallback != null) {
                    mCallback.getAlarm();
                }
            }
        } else if (ACTION_UPDATE_WEATHER.equals(action)) {

        } else if (ACTION_UPDATE_LOCATION.equals(action)) {
            if (NetWork.isAvailable(getApplicationContext())) {
                startLocation();
            }
        }
        Log.d(TAG, "In onStartCommand action =" + action);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAlarm(this);
        unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * when service create, call it to start a alarm for update weather.
     */
    private void initAlarm() {
        int rate = DEFAULT_UPDATE_RATE;
        cancelAlarm(this);
        startAlarm(this, INTERVAL_UPDATE);
        // startWeatherAlarm(this, interval);
    }

    private void startAlarm(Context context, Intent intent, long repeatTime) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerAtTime = SystemClock.elapsedRealtime() + ALARM_START_TIME_WAIT;
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime, repeatTime, pendIntent);
    }

    private void startAlarm(Context context, long repeatTime) {
        Log.d(TAG, "start alarm, repeatTime =" + repeatTime);
        Intent intent = new Intent(context, CarWeatherService.class);
        intent.setAction(ACTION_UPDATE_LOCATION);
        startAlarm(this, intent, repeatTime);
    }

    private void startWeatherAlarm(Context context, long repeatTime) {
        Log.d(TAG, "startWeatherAlarm:start alarm, repeatTime =" + repeatTime);
        Intent intent = new Intent(context, CarWeatherService.class);
        intent.setAction(ACTION_UPDATE_ALARM);
        startAlarm(this, intent, repeatTime);
    }

    private void cancelAlarm(Context context) {
        Log.d(TAG, "cancel alarm.");
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, CarWeatherService.class);
        intent.setAction(ACTION_UPDATE_LOCATION);
        PendingIntent pendIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(pendIntent);
    }

    private Intent registBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        return registerReceiver(mBroadcastReceiver, filter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "WeatherService receiver : action = " + action);
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                // update weather information when net status changed.
                if (NetWork.isAvailable(getApplicationContext())) {
                    startLocation();
                }
            }
        }
    };

    public void setPresenterListener(ModelCallback.WeatherCallback mCallback) {
        this.mCallback = mCallback;
    }

    public final class CarWeatherBInder extends Binder {
        public CarWeatherService getService() {
            return CarWeatherService.this;
        }
    }
}
