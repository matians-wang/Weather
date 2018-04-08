package com.test.carweather.entity;

public class ForecastInfoEntity {

    public ForecastInfoEntity(CurrentInfoEntity currentInfoEntity) {
        if (currentInfoEntity != null) {
            forecast_date = currentInfoEntity.weather_date;
            low_temperature = currentInfoEntity.low_temperature;
            high_temperature = currentInfoEntity.high_temperature;
            condition = currentInfoEntity.weather_condition;
            weather_type = currentInfoEntity.weather_type;
            phenomenon_type = currentInfoEntity.phenomenon_type;
            wind_direction = currentInfoEntity.wind_direction;
            wind_force = currentInfoEntity.wind_force;
        }
    }

    public String forecast_date;
    public String low_temperature;
    public String high_temperature;
    public String condition;
    public String weather_type;
    public String phenomenon_type;
    public String wind_direction;
    public String wind_force;

}
