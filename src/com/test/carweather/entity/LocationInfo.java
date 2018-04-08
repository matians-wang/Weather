package com.test.carweather.entity;

import com.test.carweather.entity.LocationResponseEntity.GeoBean;
import com.test.carweather.entity.LocationResponseEntity.GeoBean.CityInfoBean;

import android.util.Log;

public class LocationInfo {

    public String location;
    public String level;
    public String county;
    public String city;
    public String province;
    public String state;

    public LocationInfo(LocationResponseEntity locationResponseEntity) {
        Log.d("LocationInfo", "LocationInfo");
        if (locationResponseEntity != null) {
            GeoBean geo = locationResponseEntity.getGeo();
            if (geo != null) {
                Log.d("LocationInfo", "geo != null");
                CityInfoBean cityInfo = geo.getCity_info();
                if (cityInfo != null) {
                    Log.d("LocationInfo", "cityInfo != null");
                    this.location = cityInfo.getLocation();
                    this.level = cityInfo.getLevel();
                    this.county = cityInfo.getCounty();
                    this.city = cityInfo.getCity();
                    this.province = cityInfo.getProvince();
                    this.state = cityInfo.getState();
                    Log.d("LocationInfo", "location:" + location);
                    Log.d("LocationInfo", "level:" + level);
                    Log.d("LocationInfo", "county:" + county);
                    Log.d("LocationInfo", "city:" + city);
                    Log.d("LocationInfo", "province:" + province);
                    Log.d("LocationInfo", "state:" + state);
                }
            }
        }
    }
}
