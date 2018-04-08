package com.test.carweather.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.test.carweather.WeatherApplication;
import com.test.carweather.base.WeatherConstants;
import com.test.carweather.entity.CityInfoEntity;
import com.test.carweather.entity.GeocodeEntity;
import com.test.carweather.entity.GeocodeResponseEntity;
import com.test.carweather.entity.LocationInfo;
import com.test.carweather.entity.LocationResponseEntity;
import com.test.carweather.entity.SearchResponseEntity;
import com.test.carweather.network.AppHttpClient;
import com.test.carweather.network.WeatherApi;
import com.test.carweather.utils.Check;
import com.test.carweather.utils.Constants;
import com.test.carweather.utils.NetWork;
import com.test.carweather.utils.PreferencesUtil;
import com.test.carweather.widget.WeatherAppWidgetProvider;

import android.text.TextUtils;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//model for city list
public class CityModel extends BaseModel {

    private static final String TAG = CityModel.class.getSimpleName();

    private String mCityName;
    private String mDefaultId;
    private GeocodeEntity mLocationInfo;

    private WeatherApi mWeatherApiService;
    private ModelCallback.WeatherCallback mCallback;
    private ModelCallback.SearchResult mSearchCallback;
    private List<String> mCityList = new ArrayList<String>();
    private List<String> mCityNameList = new ArrayList<String>();
    private String listId;
    private String listName;
    private static double longitude;
    private static double latitude;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mWeatherApiService = AppHttpClient.getInstance().getService(WeatherApi.class);
        mDefaultId = PreferencesUtil.get(WeatherConstants.DEFAULT_CITY, "");
        mCityList = getAddCities();
        mCityNameList = getAddCityNames();
    }

    public void updateLocationCity(double longitude, double latitude) {
        Log.d(TAG, "updateLocationCity: " + longitude + " x " + latitude);
        this.longitude = longitude;
        this.latitude = latitude;

        if (longitude == 0 && latitude == 0) {
            return;
        }
        requestLocationGeocode();
    }

    public void requestLocationGeocode() {
        Log.d(TAG, "requestLocationGeocode");
        if (NetWork.isAvailable(WeatherApplication.getContext())) {
            if (Check.isEmpty(Constants.UCS_API_HOST) || Check.isEmpty(Constants.FACTORY_CODE)
                    || Check.isEmpty(Constants.CUSTOMER_KEY_ACTIVE)) {
                ModelManager.getModel(WeatherModel.class).requestDNS();
            }else {
                requestGeocode(latitude, longitude);
            }
        } else {
            onLocationEntity(null);
        }
    }

    private void requestGeocode(double latitude, double longitude) {
        if (Check.isEmpty(Constants.UCS_API_HOST) || Check.isEmpty(Constants.FACTORY_CODE)
                || Check.isEmpty(Constants.CUSTOMER_KEY_ACTIVE)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("http://").append(Constants.UCS_API_HOST).append("/").append(Constants.FACTORY_CODE);
        sb.append("/1.0/tu/").append(Constants.CUSTOMER_KEY_ACTIVE);
        sb.append("/reverse/geocode/");
        sb.append(latitude).append("/").append(longitude).append("/");

        Log.d(TAG, "requestGeocode: " + sb.toString());

        Call<GeocodeResponseEntity> entity = mWeatherApiService.getGeocode(AppHttpClient.getActiveCookieHeader(),
                sb.toString(), AppHttpClient.getCommonHashMap());
        entity.enqueue(new Callback<GeocodeResponseEntity>() {

            @Override
            public void onResponse(Call<GeocodeResponseEntity> arg0, Response<GeocodeResponseEntity> arg1) {
                GeocodeResponseEntity geocodeEntity = arg1.body();
                Log.d(TAG, "deocode onResponse=" + geocodeEntity != null ? WeatherApplication.getGson().toJson(geocodeEntity) : "null");

                if (geocodeEntity != null) {
                    if (!TextUtils.isEmpty(geocodeEntity.error_code)) {
                        if (geocodeEntity.error_code.equals(Constants.ERROR_CODE_TUID)) {
                            ModelManager.getModel(WeatherModel.class).requestGetToken(WeatherModel.GET_TOKEN_TYPE_GEOCODE);
                            return;
                        }
                    }
                }

                if (arg1.isSuccessful() && geocodeEntity != null && geocodeEntity.resp_data != null) {
                    GeocodeEntity gecode = geocodeEntity.resp_data;
                    String cityName = gecode.city_name;
                    if (!Check.isEmpty(cityName)) {
                        setDefaultId(gecode.city_name);
                        setLocationCityName(gecode.city_name);
                        setLocationInfo(gecode);

                        onLocationEntity(gecode);
                        return;
                    }
                }
                onLocationEntity(null);
            }

            @Override
            public void onFailure(Call<GeocodeResponseEntity> arg0, Throwable arg1) {
                arg1.printStackTrace();
                onLocationEntity(null);
            }
        });

    }

    public void requestSearch(String keyword) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://").append(Constants.UCS_API_HOST).append("/").append(Constants.FACTORY_CODE);
        sb.append("/1.0/tu/").append(Constants.CUSTOMER_KEY_ACTIVE);
        sb.append("/soa/weather/citylist/");
        sb.append(keyword).append("/");

        Log.d(TAG, "requestSearch: " + sb.toString());

        Call<SearchResponseEntity> entity = mWeatherApiService.getSearch(AppHttpClient.getActiveCookieHeader(),
                sb.toString(), AppHttpClient.getCommonHashMap());
        entity.enqueue(new Callback<SearchResponseEntity>() {

            @Override
            public void onResponse(Call<SearchResponseEntity> arg0, Response<SearchResponseEntity> arg1) {
                SearchResponseEntity searchEntity = arg1.body();
                Log.d(TAG, "search onResponse=" + searchEntity != null ? WeatherApplication.getGson().toJson(searchEntity) : "null");

                if (searchEntity != null) {
                    if (!TextUtils.isEmpty(searchEntity.error_code)) {
                        if (searchEntity.error_code.equals(Constants.ERROR_CODE_TUID)) {
                            ModelManager.getModel(WeatherModel.class).requestGetToken(WeatherModel.GET_TOKEN_TYPE_SRAECH);
                            if (mSearchCallback != null) {
                                mSearchCallback.onSearch(null);
                            }
                            return;
                        }
                    }
                }

                List<CityInfoEntity> cities = null;
                if (arg1.isSuccessful() && searchEntity != null && searchEntity.resp_data != null) {
                    cities = searchEntity.resp_data;
                }
                if (mSearchCallback != null) {
                    mSearchCallback.onSearch(cities);
                }
            }

            @Override
            public void onFailure(Call<SearchResponseEntity> arg0, Throwable arg1) {
                arg1.printStackTrace();
                if (mSearchCallback != null) {
                    mSearchCallback.onSearch(null);
                }
            }
        });

    }

    public static HashMap<String, String> getQueryHashMap(double longitude, double latitude) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("ihu_id", Constants.IHU_ID);
        hashMap.put("service_id", Constants.SERVICE_ID);
        hashMap.put("longitude", String.valueOf(longitude));
        hashMap.put("latitude", String.valueOf(latitude));
        hashMap.put("language", Constants.LANGUAGE);
        return hashMap;
    }

    public void setLocationInfo(GeocodeEntity info) {
        mLocationInfo = info;
    }

    public GeocodeEntity getLocationInfo() {
        return mLocationInfo;
    }

    public String getDefaultId() {
        return mDefaultId;
    }

    public void setDefaultId(String defaultId) {
        Log.d(TAG, "setDefaultId: " + defaultId);
        if (!Check.isEmpty(defaultId) && mDefaultId != defaultId) {
            mDefaultId = defaultId;
            PreferencesUtil.put(WeatherConstants.DEFAULT_CITY, defaultId);
        }
    }

    public void setLocationCityName(String name) {
        Log.d(TAG, "setLocationCityName: " + name);
        mCityName = name;
    }

    public String getLocationCityName() {
        return mCityName;
    }

    // get city list from SharedPreferences
    public List<String> getAddCities() {
        listId = PreferencesUtil.get(WeatherConstants.CITY_LIST, listId);

        mCityList.clear();
        if (!Check.isEmpty(listId)) {
            String[] stringArr = listId.split(",");
            mCityList = Arrays.asList(stringArr);
            mCityList = new ArrayList<String>(mCityList);
        }
        return mCityList;
    }

    // get city list from SharedPreferences
    public List<String> getAddCityNames() {
        listName = PreferencesUtil.get(WeatherConstants.CITY_NAME_LIST, listName);

        mCityNameList.clear();
        if (!Check.isEmpty(listName)) {
            String[] stringArr = listName.split(",");
            mCityNameList = Arrays.asList(stringArr);
            mCityNameList = new ArrayList<String>(mCityNameList);
        }
        return mCityNameList;
    }

    // save city list as string with SharedPreferences
    public void addCityOnly(String cityId, String cityName) {
        Log.d(TAG, "addCityOnly:  " + cityId);
        if (Check.isEmpty(cityId)) {
            return;
        }
        mCityList = getAddCities();
        if (!mCityList.contains(cityId)) {
            mCityList.add(cityId);
            StringBuffer sb = new StringBuffer();
            for (String city : mCityList) {
                sb.append(city);
                sb.append(",");
            }
            String list = sb.toString();
            PreferencesUtil.put(WeatherConstants.CITY_LIST, list);
        }

        addCityName(cityName);
    }

    // save city list as string with SharedPreferences
    public void addCityName(String cityName) {
        Log.d(TAG, "addCityName:  " + cityName);
        if (Check.isEmpty(cityName)) {
            return;
        }
        mCityNameList = getAddCityNames();
        if (!mCityNameList.contains(cityName)) {
            mCityNameList.add(cityName);
            StringBuffer sb = new StringBuffer();
            for (String city : mCityNameList) {
                sb.append(city);
                sb.append(",");
            }
            String list = sb.toString();
            PreferencesUtil.put(WeatherConstants.CITY_NAME_LIST, list);
        }
    }

    // add and refresh city weather
    public void addCity(String cityId, String cityName) {
        Log.d(TAG, "addCity:  " + cityId);
        addCityOnly(cityName, cityName);
        ModelManager.getModel(WeatherModel.class).updateAllWeather();
    }

    public void removeCity(String cityId, String cityName) {
        Log.d(TAG, "removeCity:  " + cityId);
        mCityList = getAddCities();
        if (!mCityList.contains(cityId)) {
            return;
        }
        mCityList.remove(cityId);
        StringBuffer sb = new StringBuffer();
        for (String city : mCityList) {
            sb.append(city);
            sb.append(",");
        }
        String list = sb.toString();
        PreferencesUtil.put(WeatherConstants.CITY_LIST, list);

        removeCityName(cityName);
    }

    public void removeCityName(String cityName) {
        Log.d(TAG, "removeCityName:  " + cityName);
        mCityNameList = getAddCities();
        if (!mCityNameList.contains(cityName)) {
            return;
        }
        mCityNameList.remove(cityName);
        StringBuffer sb = new StringBuffer();
        for (String city : mCityNameList) {
            sb.append(city);
            sb.append(",");
        }
        String list = sb.toString();
        PreferencesUtil.put(WeatherConstants.CITY_NAME_LIST, list);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setSearchListener(ModelCallback.SearchResult mSearchCallback) {
        this.mSearchCallback = mSearchCallback;
    }

    public void setListener(ModelCallback.WeatherCallback mCallback) {
        this.mCallback = mCallback;
    }

    private void onLocationEntity(GeocodeEntity locationInfo) {
        String cityId = null;
        boolean isSuccess = false;
        if (locationInfo != null) {
            cityId = locationInfo.city_name;
            isSuccess = true;
        }

        if (mCallback != null) {
            mCallback.onLocationComplete(cityId, isSuccess);
        } else {
            ModelManager.getModel(WeatherModel.class).updateAllWeather();
        }
    }

}
