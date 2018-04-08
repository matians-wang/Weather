package com.test.carweather.presenter;

import java.util.ArrayList;
import java.util.List;
import com.test.carweather.CarWeatherService;
import com.test.carweather.entity.AlarmResponseEntity;
import com.test.carweather.entity.WeatherInfoEntity;
import com.test.carweather.model.CityModel;
import com.test.carweather.model.ModelCallback;
import com.test.carweather.model.ModelManager;
import com.test.carweather.model.WeatherModel;
import com.test.carweather.utils.Check;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class HomePresenter extends BasePresenter<MainView>
        implements ModelCallback.WeatherCallback, ModelCallback.WeatherResult {

    private static final String TAG = HomePresenter.class.getSimpleName();
    private CityModel mCityModel;
    private WeatherModel mWeatherModel;

    private MainView mMainView;
    private ServiceConnection conn;
    private boolean isBound;
    private CarWeatherService mService;

    private List<String> mCityList = new ArrayList<String>();
    private List<String> mCityNameList = new ArrayList<String>();

    public HomePresenter(MainView presenterView) {
        super(presenterView);
        mMainView = presenterView;
        mCityModel = ModelManager.getModel(CityModel.class);
        mWeatherModel = ModelManager.getModel(WeatherModel.class);
        bindModel();
        getAddedCities();
        bindService();
        mWeatherModel.requestDNS();
    }

    public void bindModel() {
        mCityModel.setListener(this);
        mWeatherModel.setListener(this);
    }

    public void unBindModel() {
        mCityModel.setListener(null);
        mWeatherModel.setListener(null);
    }

    private void getAddedCities() {
        mCityList = mCityModel.getAddCities();
    }

    public List<String> getAddCityNames() {
        mCityNameList = mCityModel.getAddCityNames();
        return mCityNameList;
    }

    public int getCityCount() {
        getAddedCities();
        return mCityList.size() + 1;
    }

    public WeatherInfoEntity getLocationWeather() {
        return mWeatherModel.getLocationWeather();
    }

    public List<WeatherInfoEntity> getWeatherList() {
        return mWeatherModel.getWeatherList();
    }

    private void bindService() {
        conn = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                Log.d(TAG, "onServiceDisconnected");
                if (mService != null) {
                    mService.setPresenterListener(null);
                    mService = null;
                }
            }

            @Override
            public void onServiceConnected(ComponentName component, IBinder binder) {
                Log.d(TAG, "onServiceConnected");
                mService = ((CarWeatherService.CarWeatherBInder) binder).getService();
                mService.setPresenterListener(HomePresenter.this);
                refreshlocation();
            }
        };

        Intent intent = new Intent(mMainView.getContext(), CarWeatherService.class);
        mMainView.getContext().getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    public void unbindService() {
        if (isBound) {
            getContext().getApplicationContext().unbindService(conn);
            isBound = false;
        }
    }

    public void refreshlocation() {
        mMainView.onRefreshing(true);
        mService.startLocation();
    }

    public boolean isLocationFailed() {
        return Check.isEmpty(mCityModel.getDefaultId());
    }

    @Override
    public void onLocationComplete(String cityId, boolean success) {
        Log.d(TAG, "onLocationComplete: cityId " + cityId);
        if (!success && Check.isEmpty(mCityModel.getDefaultId()) &&  mCityModel.getAddCities().size() == 0) {
            mMainView.onLocationInfo(false);
        }else {
            updateWeather();
        }
    }

    @Override
    public void onLocationChange(double latitude, double longitude) {
        Log.d(TAG, "onLocationChange: ");
        mCityModel.updateLocationCity(longitude, latitude);
    }

    @Override
    public void getAlarm() {
        mWeatherModel.updateAlarm(mCityModel.getDefaultId());
    }

    @Override
    public void updateWeather() {
        mWeatherModel.updateAllWeather();
    }

    @Override
    public void onWeather(List<WeatherInfoEntity> mWeathers) {
        Log.d(TAG, "onWeather: ");
        mMainView.onBasicInfo(mWeathers);
    }

    @Override
    public void onAlarmEntity(AlarmResponseEntity alarmEntity) {
        Log.d(TAG, "onAlarmEntity: ");
        if (alarmEntity != null) {
            mMainView.onAlarmInfo(alarmEntity, true);
        }
        mMainView.onRefreshing(false);
    }

}
