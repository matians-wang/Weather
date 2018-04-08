package com.test.carweather.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.test.carweather.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.util.Log;

public class CarWeatherUtils {
    public static final String TAG = CarWeatherUtils.class.getSimpleName();
    public static final String PREFS_AUTO_UPDATE_SETTING = "auto_update_settings";
    private static final String KEY_WEATHER_WIFI_ONLY = "only_wifi";

    public static boolean isWifiOnly(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_AUTO_UPDATE_SETTING, 0);
        return settings.getBoolean(KEY_WEATHER_WIFI_ONLY, false);
    }

    public static final int INVALID_WEATHER_ICON_ID = R.drawable.weather_icon_99;

    public static int getWeatherIconId(String type) {
        int id = INVALID_WEATHER_ICON_ID;
        if (type == null || type.length() == 0) {
            return id;
        }
        int weatherType = Integer.valueOf(type);
        switch (weatherType) {
        case WeatherInfo.WEATHER_TYPE_SUNNY:
            id = R.drawable.weather_icon_00;
            break;
        case WeatherInfo.WEATHER_TYPE_CLOUDY:
            id = R.drawable.weather_icon_01;
            break;
        case WeatherInfo.WEATHER_TYPE_OVERCAST:
            id = R.drawable.weather_icon_02;
            break;
        case WeatherInfo.WEATHER_TYPE_FEW_SHOWERS:
            id = R.drawable.weather_icon_03;
            break;
        case WeatherInfo.WEATHER_TYPE_THUNDERSTORM:
            id = R.drawable.weather_icon_04;
            break;
        case WeatherInfo.WEATHER_TYPE_THUNDER_SHOWER_AND_ACCOMPAINED_BY_HAIL:
            id = R.drawable.weather_icon_05;
            break;
        case WeatherInfo.WEATHER_TYPE_SLEET:
            id = R.drawable.weather_icon_06;
            break;
        case WeatherInfo.WEATHER_TYPE_LIGHT_RAIN:
            id = R.drawable.weather_icon_07;
            break;
        case WeatherInfo.WEATHER_TYPE_MODERATE_RAIN:
            id = R.drawable.weather_icon_08;
            break;
        case WeatherInfo.WEATHER_TYPE_HEAVY_RAIN:
            id = R.drawable.weather_icon_09;
            break;
        case WeatherInfo.WEATHER_TYPE_RAINSTORM:
            id = R.drawable.weather_icon_10;
            break;
        case WeatherInfo.WEATHER_TYPE_HEAVY_RAINSTROM:
            id = R.drawable.weather_icon_11;
            break;
        case WeatherInfo.WEATHER_TYPE_HUGE_RAINSTORM:
            id = R.drawable.weather_icon_12;
            break;
        case WeatherInfo.WEATHER_TYPE_SNOW_SHOWERS:
            id = R.drawable.weather_icon_13;
            break;
        case WeatherInfo.WEATHER_TYPE_LIGHT_SNOW:
            id = R.drawable.weather_icon_14;
            break;
        case WeatherInfo.WEATHER_TYPE_MODERATE_SNOW:
            id = R.drawable.weather_icon_15;
            break;
        case WeatherInfo.WEATHER_TYPE_HEAVY_SNOW:
            id = R.drawable.weather_icon_16;
            break;
        case WeatherInfo.WEATHER_TYPE_SNOWSTORM:
            id = R.drawable.weather_icon_17;
            break;
        case WeatherInfo.WEATHER_TYPE_FOG:
            id = R.drawable.weather_icon_18;
            break;
        case WeatherInfo.WEATHER_TYPE_FREEZING_RAIN:
            id = R.drawable.weather_icon_19;
            break;
        case WeatherInfo.WEATHER_TYPE_SANDSTORM:
            id = R.drawable.weather_icon_20;
            break;
        case WeatherInfo.WEATHER_TYPE_LIGHT_TO_MODERATE_RAIN:
            id = R.drawable.weather_icon_21;
            break;
        case WeatherInfo.WEATHER_TYPE_MODERATE_TO_HEAVY_RAIN:
            id = R.drawable.weather_icon_22;
            break;
        case WeatherInfo.WEATHER_TYPE_HEAVY_HARD_RAIN:
            id = R.drawable.weather_icon_23;
            break;
        case WeatherInfo.WEATHER_TYPE_HEAVY_RAINS_TO_TORRENTIAL_RAIN:
            id = R.drawable.weather_icon_24;
            break;
        case WeatherInfo.WEATHER_TYPE_TO_THE_HEAVY_RAIN:
            id = R.drawable.weather_icon_25;
            break;
        case WeatherInfo.WEATHER_TYPE_SMALL_TO_MODERATE_SNOW:
            id = R.drawable.weather_icon_26;
            break;
        case WeatherInfo.WEATHER_TYPE_MODERATE_TO_HEAVY_SNOW:
            id = R.drawable.weather_icon_27;
            break;
        case WeatherInfo.WEATHER_TYPE_TO_THE_HEAVY_SNOW:
            id = R.drawable.weather_icon_28;
            break;
        case WeatherInfo.WEATHER_TYPE_AERIAL_DUST:
            id = R.drawable.weather_icon_29;
            break;
        case WeatherInfo.WEATHER_TYPE_DUST_BLOWING:
            id = R.drawable.weather_icon_30;
            break;
        case WeatherInfo.WEATHER_TYPE_STRONG_SANDSTORMS:
            id = R.drawable.weather_icon_31;
            break;
        case WeatherInfo.WEATHER_TYPE_DENSE_FOG:
            id = R.drawable.weather_icon_32;
            break;
        case WeatherInfo.WEATHER_TYPE_SNOW:
            id = R.drawable.weather_icon_33;
            break;
        case WeatherInfo.WEATHER_TYPE_STRONG_DENSE_FOG:
            id = R.drawable.weather_icon_49;
            break;
        case WeatherInfo.WEATHER_TYPE_HAZE:
            id = R.drawable.weather_icon_53;
            break;
        case WeatherInfo.WEATHER_TYPE_MODERATE_HAZE:
            id = R.drawable.weather_icon_54;
            break;
        case WeatherInfo.WEATHER_TYPE_STRONG_HAZE:
            id = R.drawable.weather_icon_55;
            break;
        case WeatherInfo.WEATHER_TYPE_HEAVY_HAZE:
            id = R.drawable.weather_icon_56;
            break;
        case WeatherInfo.WEATHER_TYPE_THICK_FOG:
            id = R.drawable.weather_icon_57;
            break;
        case WeatherInfo.WEATHER_TYPE_HEAVY_FOG:
            id = R.drawable.weather_icon_58;
            break;
        case WeatherInfo.WEATHER_TYPE_UNKNOWN:
            id = R.drawable.weather_icon_99;
            break;
        default:
            id = INVALID_WEATHER_ICON_ID;
        }
        return id;
    }

    public static final int DEFAULT_ALARM_ICON_ID = R.drawable.weather_warn_blue;

    public static int getAlarmIconId(String type) {
        int iconId = DEFAULT_ALARM_ICON_ID;
        if (type.equals(WeatherInfo.ALARM_TYPE_BLUE)) {
            iconId = R.drawable.weather_warn_blue;
        } else if (type.equals(WeatherInfo.ALARM_TYPE_YELLOOW)) {
            iconId = R.drawable.weather_warn_yellow;
        } else if (type.equals(WeatherInfo.ALARM_TYPE_ORANGE)) {
            iconId = R.drawable.weather_warn_orange;
        } else if (type.equals(WeatherInfo.ALARM_TYPE_RED)) {
            iconId = R.drawable.weather_warn_red;
        }
        return iconId;
    }

    public static final int DEFAULT_ALARM_COLOR = Color.parseColor("#55AFFE");

    public static int getAlarmColor(String type) {
        int color = DEFAULT_ALARM_COLOR;
        if (type.equals(WeatherInfo.ALARM_TYPE_BLUE)) {
            color = DEFAULT_ALARM_COLOR;
        } else if (type.equals(WeatherInfo.ALARM_TYPE_YELLOOW)) {
            color = Color.parseColor("#FFEE1B");
        } else if (type.equals(WeatherInfo.ALARM_TYPE_ORANGE)) {
            color = Color.parseColor("#F5A623");
        } else if (type.equals(WeatherInfo.ALARM_TYPE_RED)) {
            color = Color.RED;
        }
        return color;
    }

    public static String getStringAQI(int aqi) {
        if (aqi >= 0 && aqi <= 50) {
            return "优";
        } else if (aqi > 50 && aqi <= 100) {
            return "良";
        } else if (aqi > 100 && aqi <= 150) {
            return "轻度污染";
        } else if (aqi > 150 && aqi <= 200) {
            return "中度污染";
        } else if (aqi > 200 && aqi <= 300) {
            return "重度污染";
        } else if (aqi > 300) {
            return "严重污染";
        }
        return "";
    }

    public static String dateLongToShort(String longDate) {
        Date date = strToDateLong(longDate);
        String dateString = dateToStr(date);
        return dateString;
    }

    public static String getTempRange(String day, String night) {
        if (Check.isEmpty(day) | Check.isEmpty(night)) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(day);
        sb.append(Constants.RANGE_DIVIDER);
        sb.append(night);
        // sb.append(Constants.SIGN_CELSIUS);
        return sb.toString();
    }

    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date strtodate = null;
        try {
            strtodate = formatter.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strtodate;
    }

    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date strtodate = null;
        try {
            strtodate = formatter.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strtodate;
    }

    public static String dateToStrLong(java.util.Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    public static String dateToStr(java.util.Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    public static String dateToStrShort(java.util.Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i]).toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTUID() {
        File file = new File("/sys/devices/soc0/soc.1/2100000.aips-bus/21f8000.i2c/i2c-2/2-0050/eeprom_m24");

        byte[] buffer = new byte[(int) file.length()];
        InputStream ios = null;
        try {
            ios = new FileInputStream(file);
            if (ios.read(buffer) == -1) {
                return "";
            }
        } catch (Exception e) {
            Log.i(TAG, e.toString());
            return "";
        } finally {
            try {
                if (ios != null)
                    ios.close();
            } catch (IOException e) {
                return "";
            }
        }

        String version = "";

        try {
            // ignore index 0
            int index = 1;
            while (index <= 16 && index < buffer.length) {

                StringBuilder sb = new StringBuilder(2);
                int hexValue = Integer.parseInt(Integer.toHexString(buffer[index] & 0xff));

                if (hexValue < 10) {
                    sb.append('0');
                }
                sb.append(hexValue);
                version += sb.toString();

                index++;
            }
        } catch (Exception e) {
            Log.i(TAG, e.toString());
            return version;
        }

        return version;
    }
}
