package com.test.carweather.utils;

public class Constants {
    public static String ACCESS_TOKEN_ACTIVE = "";
    public static String CUSTOMER_KEY_ACTIVE = "";

    public static final String ACCESS_TOKEN = "105cb45b48fd7b54d988cd7adfbd9291";
    public static String CUSTOMER_KEY = "";
    public static String URL_DNS = "http://dns.yttsp.com/cfg/1.0/sn/" + CUSTOMER_KEY + "/dns/";
    public static String UCS_API_HOST = "";
    public static String FACTORY_CODE = "";

    public static final String PARAM_UPDATE_RATE = "update_weather_rate";
    public static final String PARAM_INDEX = "index";
    public static final String PARAM_IDS = "ids";
    public static final String PARAM_NETWORK_STATE = "state";
    public static final String PARAMETER_UPDATE ="UPDATE";

    public static final String IHU_ID = "111";
    public static final String SERVICE_ID = "222";
    public static final String TEMPERATURE_SCALE = "C";
    public static final String LANGUAGE = "zh-cn";

    public static final String FORECAST_DAYS = "5";
    public static final String FORECAST_START_TIME = "";

    public static final String ADDED_CITIES = "ADDED_CITIES";

    public static final String RANGE_DIVIDER = " ~ ";
    public final static String SIGN_CELSIUS = "\u2103";
    public final static String LOCATION_DIVIDER = "\u2022";

    public final static String KEY_WEATHER_LIST_DATA = "weather_list";
    public final static String KEY_WEATHER_LOCATION_DATA = "weather_location";

    public static final String PREFS_KEY_ACCESS_TOKEN = "com.test.carweather.access.token";
    public static final String PREFS_KEY_CUSTOMER_KEY = "com.test.carweather.customer.key";

    public static final String ERROR_CODE_SN = "2003";
    public static final String ERROR_CODE_TUID = "1001";

    public static void setActiveToken(String token) {
        if (token == null) {
            return;
        }
        ACCESS_TOKEN_ACTIVE = token;
    }

    public static void setActiveKey(String key) {
        if (key == null) {
            return;
        }
        CUSTOMER_KEY_ACTIVE = key;
    }

    public static void setDefaultKey(String key) {
        if (key == null) {
            return;
        }
        CUSTOMER_KEY = key;
    }

    public static void setHost(String host) {
        if (host == null) {
            return;
        }
        UCS_API_HOST = host;
    }

    public static void setFactoryCode(String factoryCode) {
        if (factoryCode == null) {
            return;
        }
        FACTORY_CODE = factoryCode;
    }

}
