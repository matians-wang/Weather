package com.test.carweather.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.test.carweather.WeatherApplication;
import com.test.carweather.entity.ActiveEntity;
import com.test.carweather.entity.ActiveResponseEntity;
import com.test.carweather.entity.AlarmResponseEntity;
import com.test.carweather.entity.DNSEntity;
import com.test.carweather.entity.DNSResponseEntity;
import com.test.carweather.entity.GeocodeResponseEntity;
import com.test.carweather.entity.GetTokenEntity;
import com.test.carweather.entity.GetTokenResponseEntity;
import com.test.carweather.entity.InfoResponseEntity;
import com.test.carweather.entity.ReplaceTokenEntity;
import com.test.carweather.entity.ReplaceTokenResponseEntity;
import com.test.carweather.entity.WeatherInfoEntity;
import com.test.carweather.entity.WeatherInfoResponseEntity;
import com.test.carweather.network.AppHttpClient;
import com.test.carweather.network.WeatherApi;
import com.test.carweather.utils.CarWeatherUtils;
import com.test.carweather.utils.Check;
import com.test.carweather.utils.Constants;
import com.test.carweather.utils.EncryptUtils;
import com.test.carweather.utils.PreferencesUtil;
import com.test.carweather.utils.TaskExecutor;
import com.test.carweather.widget.WeatherAppWidgetProvider;
import com.google.gson.reflect.TypeToken;

import android.text.TextUtils;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//model for requesting and processing weather data
public class WeatherModel extends BaseModel {
    private static final String TAG = WeatherModel.class.getSimpleName();

    private WeatherApi mWeatherApiService;

    private ModelCallback.WeatherResult mCallback;
    private List<WeatherInfoEntity> weatherList = new ArrayList<WeatherInfoEntity>();
    private List<WeatherInfoEntity> mTempWeatherList = new ArrayList<WeatherInfoEntity>();
    private WeatherInfoEntity mWeatherInfo;
    private WeatherInfoEntity mLocationWeatherinfo;
    private boolean isRequesting;

    public static final int GET_TOKEN_TYPE_ACTIVE = 0;
    public static final int GET_TOKEN_TYPE_WEATHER = 1;
    public static final int GET_TOKEN_TYPE_GEOCODE = 2;
    public static final int GET_TOKEN_TYPE_SRAECH = 3;

    public void onCreate() {
        mWeatherApiService = AppHttpClient.getInstance().getService(WeatherApi.class);
        initData();
    }

    public void initData() {
        String locationJson = PreferencesUtil.get(Constants.KEY_WEATHER_LOCATION_DATA, "");
        mLocationWeatherinfo = WeatherApplication.getGson().fromJson(locationJson, WeatherInfoEntity.class);

        String weatherJson = PreferencesUtil.get(Constants.KEY_WEATHER_LIST_DATA, "");
        if (weatherJson != "") {
            weatherList = WeatherApplication.getGson().fromJson(weatherJson, new TypeToken<List<WeatherInfoEntity>>() {
            }.getType());
        }
    }

    public void updateAllWeather() {
        Log.d(TAG, "updateAllWeather");
        //avoid repeating request
        if (isRequesting) {
            Log.d(TAG, "isRequesting");
            return;
        }

        isRequesting = true;

        List<String> mCityList = new ArrayList<String>();
        mCityList = ModelManager.getModel(CityModel.class).getAddCities();
        final String locationId = ModelManager.getModel(CityModel.class).getDefaultId();
        final List<String> list = new ArrayList<String>(mCityList);

        weatherList.clear();
        mTempWeatherList.clear();

        // get location and added city weather in order separately
        TaskExecutor.executeTaskSerially(new Runnable() {

            @Override
            public void run() {
                // request location
                if (!Check.isEmpty(locationId)) {
                    requestWeather(locationId, true);
                }

                // request added cities
                for (final String id : list) {
                    requestWeather(id, false);
                }

                TaskExecutor.runOnUIThread(new Runnable() {

                    @Override
                    public void run() {
                        onWeatherEntity(mTempWeatherList);
                        WeatherAppWidgetProvider.updateWidget(mLocationWeatherinfo);
                        isRequesting = false;
                    }
                });
            }
        });
    }

    public void updateAlarm(final String cityId) {

    }

    public void requestDNS() {
        Constants.setDefaultKey(CarWeatherUtils.getTUID());
        StringBuilder sb = new StringBuilder();
        sb.append("http://dns.yttsp.com/cfg/1.0/sn/");
        sb.append(Constants.CUSTOMER_KEY);
        sb.append("/dns/");

        Log.d(TAG, "requestDNS: " + sb.toString());

        Call<DNSResponseEntity> entity = mWeatherApiService.getDNS(AppHttpClient.getCookieHeader(), sb.toString(),
                AppHttpClient.getCommonHashMap());
        entity.enqueue(new Callback<DNSResponseEntity>() {

            @Override
            public void onResponse(Call<DNSResponseEntity> arg0, Response<DNSResponseEntity> arg1) {
                DNSResponseEntity dnsEntity = arg1.body();
                Log.d(TAG, "dns onResponse=" + dnsEntity != null ? WeatherApplication.getGson().toJson(dnsEntity) : "null");
                if (arg1.isSuccessful() && dnsEntity != null && dnsEntity.resp_data != null) {
                    DNSEntity dns = dnsEntity.resp_data;

                    Constants.setHost(dns.ucs_api_host);
                    Constants.setFactoryCode(dns.factory_code);

                    requestActive();
                }else {

                }
            }

            @Override
            public void onFailure(Call<DNSResponseEntity> arg0, Throwable arg1) {
                arg1.printStackTrace();

            }
        });

    }

    private void requestActive() {
        if (!TextUtils.isEmpty(Constants.CUSTOMER_KEY_ACTIVE) && !TextUtils.isEmpty(Constants.ACCESS_TOKEN_ACTIVE)) {
            Log.d(TAG, "requestActive: alreay actived" );
            ModelManager.getModel(CityModel.class).requestLocationGeocode();
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("http://").append(Constants.UCS_API_HOST).append("/").append(Constants.FACTORY_CODE);
        sb.append("/1.0/sn/").append(Constants.CUSTOMER_KEY);
        sb.append("/active/");

        Log.d(TAG, "requestActive: " + sb.toString());

        Call<ActiveResponseEntity> entity = mWeatherApiService.postActive(AppHttpClient.getCookieHeader(), sb.toString(),
                AppHttpClient.getCommonHashMap());
        entity.enqueue(new Callback<ActiveResponseEntity>() {

            @Override
            public void onResponse(Call<ActiveResponseEntity> arg0, Response<ActiveResponseEntity> arg1) {
                ActiveResponseEntity activeEntity = arg1.body();
                Log.d(TAG, "active onResponse=" + activeEntity != null ? WeatherApplication.getGson().toJson(activeEntity) : "null");

                if (activeEntity != null) {
                    if (!TextUtils.isEmpty(activeEntity.error_code)) {
                        if (activeEntity.error_code.equals(Constants.ERROR_CODE_SN)) {
                            requestGetToken(GET_TOKEN_TYPE_ACTIVE);
                            return;
                        }
                    }
                }

                if (arg1.isSuccessful() && activeEntity != null && activeEntity.resp_data != null) {
                    ActiveEntity active = activeEntity.resp_data;
                    Constants.setActiveKey(active.tuid);
                    Constants.setActiveToken(active.access_token);
                    PreferencesUtil.put(Constants.PREFS_KEY_CUSTOMER_KEY, active.tuid);
                    PreferencesUtil.put(Constants.PREFS_KEY_ACCESS_TOKEN, active.access_token);

                    ModelManager.getModel(CityModel.class).requestLocationGeocode();

                }else {

                }
            }

            @Override
            public void onFailure(Call<ActiveResponseEntity> arg0, Throwable arg1) {
                arg1.printStackTrace();

            }
        });

    }

    //reactive
    public void requestGetToken(final int type) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://").append(Constants.UCS_API_HOST).append("/").append(Constants.FACTORY_CODE);
        sb.append("/1.0/sn/").append(Constants.CUSTOMER_KEY);
        sb.append("/gettoken/");
        sb.append(EncryptUtils.md5(Constants.CUSTOMER_KEY + Constants.ACCESS_TOKEN)).append("/");

        Log.d(TAG, "requestGetToken: " + sb.toString());

        Call<GetTokenResponseEntity> entity = mWeatherApiService.postGetToken(AppHttpClient.getCookieHeader(), sb.toString(),
                AppHttpClient.getCommonHashMap());
        entity.enqueue(new Callback<GetTokenResponseEntity>() {

            @Override
            public void onResponse(Call<GetTokenResponseEntity> arg0, Response<GetTokenResponseEntity> arg1) {
                GetTokenResponseEntity tokenEntity = arg1.body();
                Log.d(TAG, "requestGetToken onResponse: " + tokenEntity != null ? WeatherApplication.getGson().toJson(tokenEntity) : "null");
                if (arg1.isSuccessful() && tokenEntity != null && tokenEntity.resp_data != null) {
                    GetTokenEntity active = tokenEntity.resp_data;
                    Constants.setActiveKey(active.tuid);
                    Constants.setActiveToken(active.access_token);
                    PreferencesUtil.put(Constants.PREFS_KEY_CUSTOMER_KEY, active.tuid);
                    PreferencesUtil.put(Constants.PREFS_KEY_ACCESS_TOKEN, active.access_token);

                    switch (type) {
                    case GET_TOKEN_TYPE_ACTIVE:
                        ModelManager.getModel(CityModel.class).requestLocationGeocode();
                        break;
                    case GET_TOKEN_TYPE_WEATHER:
                        ModelManager.getModel(CityModel.class).requestLocationGeocode();
                        break;
                    case GET_TOKEN_TYPE_GEOCODE:
                        ModelManager.getModel(CityModel.class).requestLocationGeocode();
                        break;
                    case GET_TOKEN_TYPE_SRAECH:
                        break;
                    }

                }else {

                }
            }

            @Override
            public void onFailure(Call<GetTokenResponseEntity> arg0, Throwable arg1) {
                arg1.printStackTrace();
            }
        });

    }

    //replace token
    private void requestReplaceToken() {
        if (TextUtils.isEmpty(Constants.CUSTOMER_KEY_ACTIVE)) {
            return;
        }
        Constants.setDefaultKey(CarWeatherUtils.getTUID());
        StringBuilder sb = new StringBuilder();
        sb.append("http://").append(Constants.UCS_API_HOST).append("/").append(Constants.FACTORY_CODE);
        sb.append("/1.0/tu/").append(Constants.CUSTOMER_KEY_ACTIVE);
        sb.append("/replace/token/");

        Log.d(TAG, "requestReplaceToken: " + sb.toString());

        Call<ReplaceTokenResponseEntity> entity = mWeatherApiService.postReplaceToken(AppHttpClient.getActiveCookieHeader(), sb.toString(),
                AppHttpClient.getCommonHashMap(), "1.0", "1.0");
        entity.enqueue(new Callback<ReplaceTokenResponseEntity>() {

            @Override
            public void onResponse(Call<ReplaceTokenResponseEntity> arg0, Response<ReplaceTokenResponseEntity> arg1) {
                ReplaceTokenResponseEntity tokenEntity = arg1.body();
                Log.d(TAG, "requestReplaceToken onResponse: " + tokenEntity != null ? WeatherApplication.getGson().toJson(tokenEntity) : "null");
                if (arg1.isSuccessful() && tokenEntity != null && tokenEntity.resp_data != null) {
                    ReplaceTokenEntity active = tokenEntity.resp_data;
                    Constants.setActiveKey(active.tuid);
                    Constants.setActiveToken(active.access_token);
                    PreferencesUtil.put(Constants.PREFS_KEY_CUSTOMER_KEY, active.tuid);
                    PreferencesUtil.put(Constants.PREFS_KEY_ACCESS_TOKEN, active.access_token);

                    ModelManager.getModel(CityModel.class).requestLocationGeocode();
                }else {

                }
            }

            @Override
            public void onFailure(Call<ReplaceTokenResponseEntity> arg0, Throwable arg1) {
                arg1.printStackTrace();
            }
        });

    }

    private void requestWeather(String cityName, boolean isLocation) {
        mWeatherInfo = new WeatherInfoEntity();

        StringBuilder sb = new StringBuilder();
        sb.append("http://").append(Constants.UCS_API_HOST).append("/").append(Constants.FACTORY_CODE);
        sb.append("/1.0/tu/").append(Constants.CUSTOMER_KEY_ACTIVE);
        sb.append("/soa/weather/info/");
        sb.append(cityName).append("/");

        Log.d(TAG, "requestWeather: " + sb.toString());

        Call<WeatherInfoResponseEntity> entity = mWeatherApiService.getWeather(AppHttpClient.getActiveCookieHeader(), sb.toString(),
                AppHttpClient.getCommonHashMap());

        try {
            Response<WeatherInfoResponseEntity> response = entity.execute();
            WeatherInfoResponseEntity weatherEntity = response.body();
            Log.d(TAG, "requestWeather onResponse="  + weatherEntity != null ? WeatherApplication.getGson().toJson(weatherEntity) : "null");

            if (weatherEntity != null) {
                if (!TextUtils.isEmpty(weatherEntity.error_code)) {
                    if (weatherEntity.error_code.equals(Constants.ERROR_CODE_TUID)) {
                        requestGetToken(GET_TOKEN_TYPE_WEATHER);
                        return;
                    }
                }
            }

            if (response.isSuccessful() && weatherEntity != null && weatherEntity.resp_data != null) {
                mWeatherInfo = weatherEntity.resp_data;

                if (isLocation) {
                    setLocationWeather(mWeatherInfo);
                }else {
                    mTempWeatherList.add(mWeatherInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        entity.enqueue(new Callback<WeatherInfoResponseEntity>() {
//
//            @Override
//            public void onResponse(Call<WeatherInfoResponseEntity> arg0, Response<WeatherInfoResponseEntity> arg1) {
//                WeatherInfoResponseEntity weatherEntity = arg1.body();
//                if (arg1.isSuccessful() && weatherEntity != null && weatherEntity.resp_data != null) {
//                    Log.d(TAG, "weather onResponse="  + WeatherApplication.getGson().toJson(weatherEntity));
//                    weatherList.add(weatherEntity.resp_data);
//
//                }else {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<WeatherInfoResponseEntity> arg0, Throwable arg1) {
//                arg1.printStackTrace();
//
//            }
//        });

    }

    public static HashMap<String, String> getObserveQueryMap(String location) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("ihu_id", Constants.IHU_ID);
        hashMap.put("service_id", Constants.SERVICE_ID);
        hashMap.put("location", location);
        hashMap.put("temperature_scale", Constants.TEMPERATURE_SCALE);
        hashMap.put("language", Constants.LANGUAGE);
        return hashMap;
    }

    public static HashMap<String, String> getForecastQueryMap(String location, String days, String startTime) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("ihu_id", Constants.IHU_ID);
        hashMap.put("service_id", Constants.SERVICE_ID);
        hashMap.put("location", location);
        hashMap.put("temperature_scale", Constants.TEMPERATURE_SCALE);
        hashMap.put("language", Constants.LANGUAGE);
        return hashMap;
    }

    public static HashMap<String, String> getBasicQueryMap(String location) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("ihu_id", Constants.IHU_ID);
        hashMap.put("service_id", Constants.SERVICE_ID);
        hashMap.put("location", location);
        hashMap.put("language", Constants.LANGUAGE);
        return hashMap;
    }

    public static HashMap<String, String> getInfoQueryMap(String cityName) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("city_name", cityName);
        hashMap.put("tuid", "0");
        return hashMap;
    }

    public void setListener(ModelCallback.WeatherResult mCallback) {
        this.mCallback = mCallback;
    }

    public void setLocationWeather(WeatherInfoEntity mLocationWeather) {
        String locationWeather = WeatherApplication.getGson().toJson(mLocationWeather);
        PreferencesUtil.put(Constants.KEY_WEATHER_LOCATION_DATA, locationWeather);
        this.mLocationWeatherinfo = mLocationWeather;
    }

    public WeatherInfoEntity getLocationWeather() {
        return mLocationWeatherinfo;
    }

    public void setWeatherList(List<WeatherInfoEntity> mWeathers) {
        Log.d(TAG, "setWeatherList=" + mWeathers.size());
        this.weatherList = mWeathers;
        String weathers = WeatherApplication.getGson().toJson(mWeathers);
        PreferencesUtil.put(Constants.KEY_WEATHER_LIST_DATA, weathers);
    }

    public List<WeatherInfoEntity> getWeatherList() {
         Log.d(TAG, "getWeatherList=" + weatherList.size());
        return weatherList;
    }

    private void onWeatherEntity(List<WeatherInfoEntity> mWeathers) {
        Log.d(TAG, "onWeatherEntity=" + mWeathers.size());
        this.weatherList = mWeathers;
        String weathers = WeatherApplication.getGson().toJson(mWeathers);
        PreferencesUtil.put(Constants.KEY_WEATHER_LIST_DATA, weathers);
        if (mCallback != null) {
            mCallback.onWeather(mWeathers);
        }
    }

    private void onAlarmEntity(AlarmResponseEntity alarmEntity) {
        if (mCallback != null) {
            mCallback.onAlarmEntity(alarmEntity);
        }
    }
}
