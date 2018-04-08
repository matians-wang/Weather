package com.test.carweather;

import android.app.Application;

import java.io.IOException;

import com.test.carweather.utils.Constants;
import com.test.carweather.utils.PreferencesUtil;
import com.google.gson.Gson;

public class WeatherApplication extends Application {

    private static Application sApplication;
    private static Gson sGson = new Gson();

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        initActiveParam();
        //newDB();
    }

    private void initActiveParam() {
        Constants.setActiveKey(PreferencesUtil.get(Constants.PREFS_KEY_CUSTOMER_KEY, ""));
        Constants.setActiveToken(PreferencesUtil.get(Constants.PREFS_KEY_ACCESS_TOKEN, ""));
    }

    public static void newDB() {
        DBHelper  helper=new DBHelper(sApplication);
        try {
            helper.newDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Gson getGson() {
        return sGson;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static Application getContext() {
        return sApplication;
    }

}
