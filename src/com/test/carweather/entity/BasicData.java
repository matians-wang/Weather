package com.test.carweather.entity;

import com.test.carweather.utils.Check;
import com.test.carweather.utils.Constants;

public class BasicData {

    public boolean hasData;
    public String cityId;
    public String cityName = "";
    public String updateTime;
    public String temp;
    public String weatherStatus;
    public String weatherIconId;
    public String weatherDate;
    public String indexAir;
    public String indexUltraviolet;
    public String indexCar;
    public String indexDress;
    public String indexCold;
    public String indexTraffic;

    public BasicData(WeatherInfoEntity weatherEntity) {
        if (weatherEntity != null) {
            this.cityId = weatherEntity.city_name;
            this.cityName = weatherEntity.city_name;
            this.updateTime = weatherEntity.release_time;

            if (weatherEntity.current_conditions != null) {
                CurrentInfoEntity currentInfoEntity = weatherEntity.current_conditions;
                this.temp = currentInfoEntity.current_temperature;
                this.weatherStatus = currentInfoEntity.weather_condition;
                this.weatherIconId = currentInfoEntity.weather_type;
                this.weatherDate = currentInfoEntity.weather_date;

                this.indexAir = currentInfoEntity.airpollution_index;
                this.indexUltraviolet = currentInfoEntity.ultravioletray_index;
                this.indexCar = currentInfoEntity.washcar_index;
                this.indexDress = currentInfoEntity.dress_index;
                this.indexCold = currentInfoEntity.cold_index;
                this.indexTraffic = currentInfoEntity.traffic_index;

                if (!Check.isEmpty(temp)) {
                    hasData = true;
                }
            }

        }
    }
}
