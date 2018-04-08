package com.test.carweather.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.test.carweather.R;

import android.content.Context;

public class TimeUtils {

    private static final int DEFAULT = 0;
    private static final int NOW_TIME = 1;
    private static final int WEEK = 2;
    private static final int CALENDAR = 3;


    private static String getTimeInfo(Context context,int style) {
        Date date = new Date();
        String pattern = null;
        switch (style) {
        case NOW_TIME:
            pattern = "HH:mm";
            break;
        case WEEK:
            if (context != null) {
                String oldText = context.getString(R.string.week_prefix);
                pattern = oldText + "EEEEE";
            }
            break;
        case CALENDAR:
            pattern = "yyyy-MM-dd";
            break;
        default:
            pattern = "yyyy-MM-dd HH:mm:ss";
        }

        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
        return format.format(date);
    }

    public static String getNowTime(Context context) {
        return getTimeInfo(context,NOW_TIME);
    }

    public static String getWeek(Context context) {
        return getTimeInfo(context, WEEK);
    }

    /**
     * Only include year,month,day
     * @return "yyyy-MM-dd"
     */
    public static String getCalendar(Context context) {
        return getTimeInfo(context, CALENDAR);
    }

    public static String getFullTime(Context context) {
        return getTimeInfo(context, DEFAULT);
    }

    public static boolean isMidNight(Context context) {
        return getNowTime(context).equals("00:00");
    }

    public static String getWeek(String pTime) {
          String Week = "";
          SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
          Calendar c = Calendar.getInstance();
          try {
              if(pTime != null){
                  c.setTime(format.parse(pTime));
              }
          } catch (ParseException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
          }
          if (c.get(Calendar.DAY_OF_WEEK) == 1) {
           Week += "星期日";
          }
          if (c.get(Calendar.DAY_OF_WEEK) == 2) {
           Week += "星期一";
          }
          if (c.get(Calendar.DAY_OF_WEEK) == 3) {
           Week += "星期二";
          }
          if (c.get(Calendar.DAY_OF_WEEK) == 4) {
           Week += "星期三";
          }
          if (c.get(Calendar.DAY_OF_WEEK) == 5) {
           Week += "星期四";
          }
          if (c.get(Calendar.DAY_OF_WEEK) == 6) {
           Week += "星期五";
          }
          if (c.get(Calendar.DAY_OF_WEEK) == 7) {
           Week += "星期六";
          }
          return Week;
         }

    //Get weekday basing on today
    //today: d = 0
    public static String getWeek(Context context, int d){
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        i += d;
        switch (i % 7) {
        case 0:
            return context.getString(R.string.saturday);
        case 1:
            return context.getString(R.string.sunday);
        case 2:
            return context.getString(R.string.monday);
        case 3:
            return context.getString(R.string.tuesday);
        case 4:
            return context.getString(R.string.wednesday);
        case 5:
            return context.getString(R.string.thursday);
        case 6:
            return context.getString(R.string.friday);
        case 7:
            return context.getString(R.string.saturday);
        default:
            return "";
        }
    }

    public static Date getNextDate(Date date, int delta) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, delta);
        return cal.getTime();
    }
}
