package com.test.carweather;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import com.test.carweather.adapter.HomeFragmentPagerAdapter;
import com.test.carweather.entity.AlarmResponseEntity;
import com.test.carweather.entity.BasicData;
import com.test.carweather.entity.WeatherInfoEntity;
import com.test.carweather.presenter.HomePresenter;
import com.test.carweather.presenter.MainView;
import com.test.carweather.utils.CarWeatherUtils;
import com.test.carweather.utils.Check;
import com.test.carweather.utils.LoadingProgressDialog;
import com.test.carweather.utils.MyDialog;
import com.test.carweather.utils.TimeUtils;
import com.test.carweather.view.HomeFragment;
import com.test.carweather.view.indicator.LinePageIndicator;

public class HomeActivity extends FragmentActivity
        implements MainView, OnClickListener, OnPageChangeListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    public static final String BUNDLE = "home";
    private ViewPager mPager;
    private HomeFragmentPagerAdapter mAdapter;
    private LinePageIndicator mIndicator;
    private TextView mSetting;
    private ImageView mWeatherIcon;
    private TextView mLocation;
    private TextView mUpdate;
    private TextView mUpdateTitle;
    private TextView mLastUpdateTime;
    private View mUpdateInfo;
    private View mHeader;

    private List<WeatherInfoEntity> mWeatherEntities = new ArrayList<WeatherInfoEntity>();
    private WeatherInfoEntity mLocationWeather;
    private HomePresenter mPresenter;

    private LoadingProgressDialog mLoadingView;
    private MyDialog mAlarmDialog;
    private int mCityCount;
    private List<HomeFragment> fragmentList = new ArrayList<HomeFragment>();
    private BasicData mBasicData;
    private int mCurrentPage;
    private List<String> mCityNameList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate:  ");
        printVersionName();

        mPresenter = new HomePresenter(this);
        mHeader = (View) findViewById(R.id.header_home);
        mWeatherIcon = (ImageView) findViewById(R.id.img_weather_condition);
        mLocation = (TextView) findViewById(R.id.location);
        mUpdateInfo = (View) findViewById(R.id.update_info);
        mUpdateTitle = (TextView) findViewById(R.id.update_title);
        mLastUpdateTime = (TextView) findViewById(R.id.time_last_update);
        mUpdate = (TextView) findViewById(R.id.update_title);
        //mUpdate.setOnClickListener(this);
        mSetting = (TextView) findViewById(R.id.setting);
        mSetting.setOnClickListener(this);

        mAdapter = new HomeFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (LinePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        mIndicator.setLineWidth(24);
        mIndicator.setStrokeWidth(5);
        mIndicator.setOnPageChangeListener(this);

    }

    private void printVersionName() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            Log.i(TAG, "current version: " + version);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void onNewData() {
        Log.d(TAG, "onNewData");
        mLocationWeather = mPresenter.getLocationWeather();
        mWeatherEntities = mPresenter.getWeatherList();
        mCityNameList = mPresenter.getAddCityNames();
    }

    private void initFragment() {
        Log.d(TAG, "initFragment");
        onNewData();

        // clear old fragments
        fragmentList.clear();

        // add location fragment
        fragmentList.add(HomeFragment.newInstance(mLocationWeather));

        // add added fragment
        Log.d(TAG, "initFragment WeatherInfoEntity=" + mWeatherEntities.size());
        for (WeatherInfoEntity entity : mWeatherEntities) {
            HomeFragment fragment = HomeFragment.newInstance(entity);
            fragmentList.add(fragment);
        }

        mAdapter.updateData(fragmentList);
        refreshHeader();
        Log.d(TAG, "fragmentList: " + fragmentList.size());
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent:  ");
        super.onNewIntent(intent);
        setIntent(intent);
        processExtraData();
    }

    private void processExtraData() {
        Intent intent = getIntent();
        mCurrentPage = intent.getIntExtra("page", 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume:  ");
        mPresenter.bindModel();
        initFragment();

        mPager.setCurrentItem(mCurrentPage);
    }

    @Override
    public void onPause() {
        hideLoadingView();
        hideAlarmView();
        mPresenter.unBindModel();
        super.onPause();
        Log.d(TAG, "onPause:  ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unbindService();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.setting:
            goToSetting();
            break;
        case R.id.update_title:
            refreshData();
            break;
        case R.id.empty_message_view:
            refreshData();
            break;
        default:
            break;
        }

    }

    public void refreshData() {
        Log.d(TAG, "refreshData:  ");
        mPresenter.refreshlocation();
    }

    private void goToSetting() {
        Intent intent = new Intent(HomeActivity.this, CityActivity.class);
        startActivity(intent);
    }

    private void toggleLoadingView(boolean isVisible) {
        if (isVisible) {
            showLoadingView();
        } else {
            mAdapter.notifyDataSetChanged();
            hideLoadingView();
        }
    }

    private void hideLoadingView() {
        if (mLoadingView != null) {
            mLoadingView.dismiss();
        }
    }

    private void showLoadingView() {
        if (mLoadingView == null) {
            mLoadingView = new LoadingProgressDialog(this);
        }
        mLoadingView.show();
    }

    public void showAlarmView(AlarmResponseEntity mAlarms) {
        Log.d(TAG, "showAlarmView: " + WeatherApplication.getGson().toJson(mAlarms));
        if (mAlarmDialog == null) {
            mAlarmDialog = new MyDialog(getContext());
            mAlarmDialog.setCancelable(true);
        }
        mAlarmDialog.setAlarm(mAlarms);
        mAlarmDialog.show();
    }

    public void hideAlarmView() {
        if (mAlarmDialog != null) {
            mAlarmDialog.dismiss();
        }
    }

    public void refreshHeader() {
        mBasicData = null;
        if (mCurrentPage == 0) {
            mBasicData = new BasicData(mLocationWeather);
        } else if (mWeatherEntities != null && mWeatherEntities.size() > mCurrentPage - 1) {
            mBasicData = new BasicData(mWeatherEntities.get(mCurrentPage - 1));
        }

        if (mBasicData != null) {
            setWeatherIcon(CarWeatherUtils.getWeatherIconId(mBasicData.weatherIconId));
            setLocation(mBasicData.cityName);
            setLastUpdateTime(mBasicData.updateTime);
        }
    }

    public void setWeatherIcon(int resId) {
        mWeatherIcon.setBackgroundResource(resId);
    }

    public void setLocation(String location) {
        boolean needLocationIcon = mCurrentPage == 0 && !Check.isEmpty(location);

        mLocation.setText(location);
        mLocation.setCompoundDrawablesRelativeWithIntrinsicBounds(
                needLocationIcon ? R.drawable.weather_address_current : 0, 0, 0, 0);
    }

    public void setLastUpdateTime(String time) {
//        mLastUpdateTime.setText(time);
//
//        if (!Check.isEmpty(time)) {
//            mUpdateInfo.setVisibility(View.VISIBLE);
//        } else {
//            mUpdateInfo.setVisibility(View.GONE);
//        }

        mLastUpdateTime.setText(TimeUtils.getFullTime(getApplicationContext()));
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onBasicInfo(List<WeatherInfoEntity> weatherDatas) {
        Log.d(TAG, "onBasicInfo:  ");
        initFragment();
        toggleLoadingView(false);
    }

    @Override
    public void onLocationInfo(boolean success) {
        Log.d(TAG, "onLocationInfo:  ");
        if (!success) {
            initFragment();
            toggleLoadingView(false);
        }
    }

    @Override
    public void onAlarmInfo(AlarmResponseEntity weatherDatas, boolean isLocationCity) {
        if (weatherDatas.getAlarms().size() != 0) {
            showAlarmView(weatherDatas);
        }
    }

    @Override
    public void onRefreshing(boolean refreshing) {
        toggleLoadingView(refreshing);
    }

    @Override
    public void onEmpty(boolean isEnabled) {

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        mCurrentPage = arg0;
        refreshHeader();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onAppBack");
        super.onBackPressed();
    }
}
