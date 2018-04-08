package com.test.carweather.utils;

import android.util.Log;

//As android has deprecated its log-control variable Config.DEBUG,
//It is strongly recommended that every package defines its own global variable to 
//control the log on/off.
public class MyLogConfig {
  // here it's the apk name, like Stk, Phone, TouchDialer, InCallScreen, etc
  public static final String LOG_TAG = "CarWeather";
  public static boolean DEBUG = true;
}
