package com.test.carweather.model;

import java.util.List;

import com.test.carweather.entity.AlarmResponseEntity;
import com.test.carweather.entity.CityInfoEntity;
import com.test.carweather.entity.WeatherInfoEntity;

public interface ModelCallback {
    interface WeatherCallback {
        void onLocationComplete(String cityId, boolean success);

        void onLocationChange(double latitude, double longitude);

        void getAlarm();

        void updateWeather();
    }

    interface WeatherResult {
        void onWeather(List<WeatherInfoEntity> mWeathers);

        void onAlarmEntity(AlarmResponseEntity alarmEntity);

    }

    interface SearchResult {
        void onSearch(List<CityInfoEntity> cityInfoEntities);

    }
}
