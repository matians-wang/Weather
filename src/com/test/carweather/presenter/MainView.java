package com.test.carweather.presenter;

import com.test.carweather.entity.AlarmResponseEntity;
import com.test.carweather.entity.WeatherInfoEntity;

import java.util.List;

/**
 * Created by b913 on 2017/4/7.
 */

public interface MainView extends BaseView {
    void onBasicInfo(List<WeatherInfoEntity> weatherDatas);
    void onLocationInfo(boolean success);
    void onAlarmInfo(AlarmResponseEntity weatherDatas, boolean isLocationCity);
    void onRefreshing(boolean refreshing);
    void onEmpty(boolean isEnabled);
}
