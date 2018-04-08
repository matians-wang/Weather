package com.test.carweather.entity;

import java.util.List;

import com.test.carweather.entity.ForecastResponseEntity.ForecastsBean;
import com.test.carweather.utils.CarWeatherUtils;
import com.test.carweather.utils.Check;

public class SimpleInfo {

    public String cityId;
    public String cityName;
    public String temp;
    public String weatherStatus;
    public String weatherIconId;

    public SimpleInfo(WeatherInfoEntity weatherEntity) {
        if (weatherEntity != null) {
            this.cityId = weatherEntity.city_name;
            this.cityName = weatherEntity.city_name;

            if (weatherEntity.current_conditions != null) {
                CurrentInfoEntity currentInfoEntity = weatherEntity.current_conditions;
                this.temp = currentInfoEntity.current_temperature;
                this.weatherStatus = currentInfoEntity.weather_condition;
                this.weatherIconId = currentInfoEntity.weather_type;
            }

        }
    }
}
