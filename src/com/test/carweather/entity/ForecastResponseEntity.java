package com.test.carweather.entity;

import java.util.List;

/**
 * Created by b913 on 2017/4/6.
 */

public class ForecastResponseEntity {
    /**
     * area_id : 101010100
     * area_en : beijing
     * area_zh : 北京
     * city_en : beijing
     * city_zh : 北京
     * province_en : beijing
     * province_zh : 北京
     * state_en : china
     * state_zh : 中国
     * record_count : 2
     * publish_time : 201601251800
     * forecasts : [{"forecast_date":"20160125","day_weather_phenomena":"晴","day_c_temperature":"2","night_weather_phenomena":"晴","night_c_temperature":"-7","day_wind_direction":"无持续风向","day_wind_force":"3-4 级","night_wind_direction":"无持续风向","night_wind_force":"3-4 级","day_weather_code":"00","day_weather_image":null,"night_weather_code":"00","night_weather_image":null,"sunrd":"07:30|17:23"},{"forecast_date":"20160126","day_weather_phenomena":"晴","day_c_temperature":"2","night_weather_phenomena":"晴","night_c_temperature":"-9","day_wind_direction":"无持续风向","day_wind_force":"3-4 级","night_wind_direction":"无持续风向","night_wind_force":"微风","day_weather_code":"00","day_weather_image":null,"night_weather_code":"00","night_weather_image":null,"sunrd":"07:30|17:25"}]
     */

    private String area_id;
    private String area_en;
    private String area_zh;
    private String city_en;
    private String city_zh;
    private String province_en;
    private String province_zh;
    private String state_en;
    private String state_zh;
    private String record_count;
    private String publish_time;
    private List<ForecastsBean> forecasts;

    public String getArea_id() {
        return area_id;
    }

    public void setArea_id(String area_id) {
        this.area_id = area_id;
    }

    public String getArea_en() {
        return area_en;
    }

    public void setArea_en(String area_en) {
        this.area_en = area_en;
    }

    public String getArea_zh() {
        return area_zh;
    }

    public void setArea_zh(String area_zh) {
        this.area_zh = area_zh;
    }

    public String getCity_en() {
        return city_en;
    }

    public void setCity_en(String city_en) {
        this.city_en = city_en;
    }

    public String getCity_zh() {
        return city_zh;
    }

    public void setCity_zh(String city_zh) {
        this.city_zh = city_zh;
    }

    public String getProvince_en() {
        return province_en;
    }

    public void setProvince_en(String province_en) {
        this.province_en = province_en;
    }

    public String getProvince_zh() {
        return province_zh;
    }

    public void setProvince_zh(String province_zh) {
        this.province_zh = province_zh;
    }

    public String getState_en() {
        return state_en;
    }

    public void setState_en(String state_en) {
        this.state_en = state_en;
    }

    public String getState_zh() {
        return state_zh;
    }

    public void setState_zh(String state_zh) {
        this.state_zh = state_zh;
    }

    public String getRecord_count() {
        return record_count;
    }

    public void setRecord_count(String record_count) {
        this.record_count = record_count;
    }

    public String getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

    public List<ForecastsBean> getForecasts() {
        return forecasts;
    }

    public void setForecasts(List<ForecastsBean> forecasts) {
        this.forecasts = forecasts;
    }

    public static class ForecastsBean {
        /**
         * forecast_date : 20160125
         * day_weather_phenomena : 晴
         * day_c_temperature : 2
         * night_weather_phenomena : 晴
         * night_c_temperature : -7
         * day_wind_direction : 无持续风向
         * day_wind_force : 3-4 级
         * night_wind_direction : 无持续风向
         * night_wind_force : 3-4 级
         * day_weather_code : 00
         * day_weather_image : null
         * night_weather_code : 00
         * night_weather_image : null
         * sunrd : 07:30|17:23
         */

        private String forecast_date;
        private String day_weather_phenomena;
        private String day_c_temperature;
        private String night_weather_phenomena;
        private String night_c_temperature;
        private String day_wind_direction;
        private String day_wind_force;
        private String night_wind_direction;
        private String night_wind_force;
        private String day_weather_code;
        private Object day_weather_image;
        private String night_weather_code;
        private Object night_weather_image;
        private String sunrd;

        public String getForecast_date() {
            return forecast_date;
        }

        public void setForecast_date(String forecast_date) {
            this.forecast_date = forecast_date;
        }

        public String getDay_weather_phenomena() {
            return day_weather_phenomena;
        }

        public void setDay_weather_phenomena(String day_weather_phenomena) {
            this.day_weather_phenomena = day_weather_phenomena;
        }

        public String getDay_c_temperature() {
            return day_c_temperature;
        }

        public void setDay_c_temperature(String day_c_temperature) {
            this.day_c_temperature = day_c_temperature;
        }

        public String getNight_weather_phenomena() {
            return night_weather_phenomena;
        }

        public void setNight_weather_phenomena(String night_weather_phenomena) {
            this.night_weather_phenomena = night_weather_phenomena;
        }

        public String getNight_c_temperature() {
            return night_c_temperature;
        }

        public void setNight_c_temperature(String night_c_temperature) {
            this.night_c_temperature = night_c_temperature;
        }

        public String getDay_wind_direction() {
            return day_wind_direction;
        }

        public void setDay_wind_direction(String day_wind_direction) {
            this.day_wind_direction = day_wind_direction;
        }

        public String getDay_wind_force() {
            return day_wind_force;
        }

        public void setDay_wind_force(String day_wind_force) {
            this.day_wind_force = day_wind_force;
        }

        public String getNight_wind_direction() {
            return night_wind_direction;
        }

        public void setNight_wind_direction(String night_wind_direction) {
            this.night_wind_direction = night_wind_direction;
        }

        public String getNight_wind_force() {
            return night_wind_force;
        }

        public void setNight_wind_force(String night_wind_force) {
            this.night_wind_force = night_wind_force;
        }

        public String getDay_weather_code() {
            return day_weather_code;
        }

        public void setDay_weather_code(String day_weather_code) {
            this.day_weather_code = day_weather_code;
        }

        public Object getDay_weather_image() {
            return day_weather_image;
        }

        public void setDay_weather_image(Object day_weather_image) {
            this.day_weather_image = day_weather_image;
        }

        public String getNight_weather_code() {
            return night_weather_code;
        }

        public void setNight_weather_code(String night_weather_code) {
            this.night_weather_code = night_weather_code;
        }

        public Object getNight_weather_image() {
            return night_weather_image;
        }

        public void setNight_weather_image(Object night_weather_image) {
            this.night_weather_image = night_weather_image;
        }

        public String getSunrd() {
            return sunrd;
        }

        public void setSunrd(String sunrd) {
            this.sunrd = sunrd;
        }
    }
}
