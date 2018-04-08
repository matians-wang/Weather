package com.test.carweather.base;

import java.util.HashMap;
import java.util.Map;

public class WeatherConstants {

    private WeatherConstants() {
    }

    // SharedPreferences KEY
    public static final String ALARM_ALLOW = "ALARM_ALLOW";
    public static final String NOTIFICATION_ALLOW = "NOTIFICATION_ALLOW";
    public static final String NOTIFICATION_THEME = "NOTIFICATION_THEME";
    public static final String POLLING_TIME = "POLLING_TIME";

    public static final String CITYS_TIPS_SHOW = "CITYS_TIPS_SHOW";
    public static final String LOCATION = "LOCATION";
    public static final String MAIN_PAGE_WEATHER = "MAIN_PAGE_WEATHER";
    public static final String DEFAULT_CITY = "DEFAULT_CITY";
    public static final String ADDED_CITIES = "ADDED_CITIES";
    public static final String DEFAULT_CITY_ID = "101220901";

    public static final String CITY_LIST = "CITY_LIST";
    public static final String CITY_NAME_LIST = "CITY_NAME_LIST";

    public static final String DEFAULT_STR = "$";

    private static Map<String, Integer> sWeatherIcons = new HashMap<String, Integer>();

    private static final long[] SCHEDULES = { 30 * 60, 60 * 60, 3 * 60 * 60, 0 };
    private static final String[] SUNNY = { "晴", "多云" };
    private static final String[] WEATHERS = { "阴", "晴", "多云", "大雨", "雨", "雪", "风", "雾霾", "雨夹雪" };
    public static final String[] WEATHER_CONDITIONS = { "晴", "多云", "阴", "阵雨", "雷阵雨", "雷阵雨并伴有冰雹", "雨夹雪", "小雨", "中雨",
            "大雨", "暴雨", "大暴雨", "特大暴雨", "阵雪", "小雪", "中雪", "大雪", "暴雪", "雾", "冻雨", "沙尘暴", "小到中雨", "中到大雨", "大到暴雨", "暴雨到大暴雨",
            "大暴雨到特大暴雨", "小到中雪", "中到大雪", "大到暴雪", "浮尘", "扬沙", "强沙尘暴" };

    public static long getSchedule(int which) {
        return SCHEDULES[which];
    }
}
