package com.test.carweather.entity;

import java.util.List;

public class WeatherInfoEntity {

    public String city_name;
    public String release_time;
    public CurrentInfoEntity current_conditions;
    public List<ForecastInfoEntity> forecast_conditions;

}
