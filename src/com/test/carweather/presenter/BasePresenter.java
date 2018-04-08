package com.test.carweather.presenter;

import com.test.carweather.WeatherApplication;

import android.content.Context;


public abstract class BasePresenter<T extends BaseView> {

    protected T mPresentView;
    private Context mContext = WeatherApplication.getContext();

    public Context getContext() {
        return mContext;
    }

    public BasePresenter(T presenterView) {
        attachView(presenterView);
    }

    private void attachView(T presenterView) {
        mPresentView = presenterView;
    }

    public void onDetchView() {
        mPresentView = null;
    }


}
