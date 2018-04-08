package com.test.carweather.entity;

import java.util.List;

import com.test.carweather.entity.ForecastResponseEntity.ForecastsBean;
import com.test.carweather.utils.CarWeatherUtils;
import com.test.carweather.utils.Check;
import com.test.carweather.utils.Constants;

public class WidgetInfo {

    public boolean hasData;
    public String cityId;
    public String cityName = "";
    public String updateTime;
    public String temp;
    public String weatherStatus;
    public String weatherIconId;
    public String indexAir;
    public String temp1;
    public String temp2;
    public String temp3;
    public String temp4;

    public WidgetInfo(WeatherInfoEntity weatherEntity) {
        if (weatherEntity != null) {
            this.cityId = weatherEntity.city_name;
            this.cityName = weatherEntity.city_name;
            this.updateTime = weatherEntity.release_time;

            if (weatherEntity.current_conditions != null) {
                CurrentInfoEntity currentInfoEntity = weatherEntity.current_conditions;
                this.temp = currentInfoEntity.current_temperature;
                this.weatherStatus = currentInfoEntity.weather_condition;
                this.weatherIconId = currentInfoEntity.weather_type;

                this.indexAir = currentInfoEntity.airpollution_index;

                if (!Check.isEmpty(temp)) {
                    hasData = true;
                }
            }

            if (weatherEntity.forecast_conditions != null) {
                List<ForecastInfoEntity> forecasts = weatherEntity.forecast_conditions;
                if (!Check.isEmpty(forecasts) && forecasts.size() >= 5) {
                    this.temp1 = CarWeatherUtils.getTempRange(forecasts.get(1).low_temperature,
                            forecasts.get(1).high_temperature);
                    this.temp2 = CarWeatherUtils.getTempRange(forecasts.get(2).low_temperature,
                            forecasts.get(2).high_temperature);
                    this.temp3 = CarWeatherUtils.getTempRange(forecasts.get(3).low_temperature,
                            forecasts.get(3).high_temperature);
                    this.temp4 = CarWeatherUtils.getTempRange(forecasts.get(4).low_temperature,
                            forecasts.get(4).high_temperature);
                }
            }
        }
    }
}
