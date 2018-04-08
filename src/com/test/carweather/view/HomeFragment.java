package com.test.carweather.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.test.carweather.HomeActivity;
import com.test.carweather.R;
import com.test.carweather.WeatherApplication;
import com.test.carweather.adapter.CommonAdapter;
import com.test.carweather.adapter.ViewHolder;
import com.test.carweather.base.WeatherConstants;
import com.test.carweather.entity.AlarmInfo;
import com.test.carweather.entity.AlarmResponseEntity;
import com.test.carweather.entity.BasicData;
import com.test.carweather.entity.CurrentInfoEntity;
import com.test.carweather.entity.ForecastInfoEntity;
import com.test.carweather.entity.AlarmResponseEntity.AlarmsBean;
import com.test.carweather.entity.WeatherInfoEntity;
import com.test.carweather.utils.CarWeatherUtils;
import com.test.carweather.utils.Check;
import com.test.carweather.utils.TimeUtils;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeFragment extends Fragment implements OnClickListener {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private GridView mGridView;
    private TextView mAlarmPrompt;
    private TextView mTempture;
    private TextView mState;
    private TextView mAirState;
    private TextView mWashcarState;
    private TextView mUltraviolaterayState;
    private TextView mUmbrellaState;
    private LinearLayout mIndex;
    private ImageView mImageView;
    private TextView mEmptyText;
    private RelativeLayout mEmptyView;
    private View mPanel;
    private CommonAdapter mAdapter;
    private List<String> mDatas = new ArrayList<String>();

    private AlarmResponseEntity alarmDatas;
    private AlarmInfo alarmInfo;
    private CurrentInfoEntity currentInfoEntity;
    private BasicData mBasicData;

    private ProgressDialog mProgressDialog;
    private WeatherInfoEntity mWeatherDatas;
    private boolean hasData;
    private String weatherDateStr;
    private Date weatherDate;

    private List<ForecastInfoEntity> forecasts = new ArrayList<ForecastInfoEntity>();

    public HomeFragment() {

    }

    public static HomeFragment newInstance(WeatherInfoEntity mWeatherEntity) {
        HomeFragment myFragment = new HomeFragment();
        myFragment.mWeatherDatas = mWeatherEntity;
        return myFragment;
    }

    private void processData() {
        if (mWeatherDatas != null) {
            mBasicData = new BasicData(mWeatherDatas);

            weatherDateStr = mBasicData.weatherDate;
            if (Check.isEmpty(weatherDateStr)) {
                weatherDateStr = TimeUtils.getNowTime(WeatherApplication.getContext());
            }
            weatherDate = CarWeatherUtils.strToDate(weatherDateStr);

            currentInfoEntity = mWeatherDatas.current_conditions;
            if (currentInfoEntity != null &&  currentInfoEntity.weather_condition != null) {
                hasData = true;
            }

            forecasts.clear();
            ForecastInfoEntity current = new ForecastInfoEntity(currentInfoEntity);
            forecasts.add(current);
            for (int i = 0; i < mWeatherDatas.forecast_conditions.size(); i++) {
                if (i < 4) {
                    forecasts.add(mWeatherDatas.forecast_conditions.get(i));
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        processData();
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pager_home, container, false);
        mPanel = (View) view.findViewById(R.id.basic_panel);
        mEmptyView = (RelativeLayout) view.findViewById(R.id.empty_message_frame);
        mEmptyText = (TextView) view.findViewById(R.id.empty_message_view);
        mEmptyText.setOnClickListener(this);
        if (!hasData) {
            mPanel.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
        mAlarmPrompt = (TextView) view.findViewById(R.id.prompt_alarm);
        mAlarmPrompt.setOnClickListener(this);
        mTempture = (TextView) view.findViewById(R.id.tempter_current);
        mState = (TextView) view.findViewById(R.id.state_current);
        mAirState = (TextView) view.findViewById(R.id.air_state);
        mWashcarState = (TextView) view.findViewById(R.id.washcar_state);
        mUltraviolaterayState = (TextView) view.findViewById(R.id.ultravioletray_state);
        mUmbrellaState = (TextView) view.findViewById(R.id.umbrella_state);
        mIndex = (LinearLayout) view.findViewById(R.id.index);

        mGridView = (GridView) view.findViewById(R.id.info_list);
        mGridView.setNumColumns(5);
        mGridView.setHorizontalSpacing(20);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));

        mAdapter = new CommonAdapter<ForecastInfoEntity>(this.getActivity(), R.layout.item_info, forecasts) {

            @Override
            protected void convert(ViewHolder viewHolder, ForecastInfoEntity item, int position) {
                if (position > 4) {
                    return;
                }
                String weekDay = getString(R.string.today);
//                if (position != 0) {
//                    weekDay = TimeUtils.getWeek(getActivity(), position);
//                }
                weekDay = TimeUtils.getWeek(getActivity(), position);

                Date date = TimeUtils.getNextDate(weatherDate, position);
                String dateStr = CarWeatherUtils.dateToStrShort(date);

                String weatherType = item.weather_type;
                String condition;
                int index;
                if (Check.isEmpty(weatherType)) {
                    index = -1;
                    condition = "";
                }else {
                    index = Integer.valueOf(weatherType);
                    condition = WeatherConstants.WEATHER_CONDITIONS[index];
                }

                viewHolder.setText(R.id.weekday, weekDay);
                viewHolder.setText(R.id.date, dateStr);
                viewHolder.setText(R.id.tempter_high, item.high_temperature);
                viewHolder.setText(R.id.tempter_low, item.low_temperature);
                viewHolder.setText(R.id.condition, condition);
                viewHolder.setText(R.id.wind_state, item.wind_direction);
                viewHolder.setText(R.id.wind_level, item.wind_force);

            }
        };
        mGridView.setAdapter(mAdapter);
        refreshBasicData(mBasicData);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initData() {
        mDatas.clear();
        for (int i = 0; i < 4; i++) {
            mDatas.add(i, String.valueOf(i));
        }
    }

    private void refreshBasicData(BasicData mBasicData){
        if (mBasicData == null) {
            return;
        }
        mTempture.setText(mBasicData.temp);
        mState.setText(mBasicData.weatherStatus);
        mIndex.setVisibility(View.VISIBLE);
        mAirState.setText(getIndex(mBasicData.indexAir));
        mWashcarState.setText(getIndex(mBasicData.indexCar));
        mUltraviolaterayState.setText(getIndex(mBasicData.indexUltraviolet));
        //mUmbrellaState.setText(mBasicData.indexCar);
    }

    private String getIndex(String index){
        String[] data = index.split("，");
        if (Check.isEmpty(data)) {
            return index;
        }
        return data[0];
    }

    private void refreshAlarmData(AlarmResponseEntity weatherDatas) {
        if (weatherDatas != null && !Check.isEmpty(weatherDatas.getAlarms())) {
            AlarmsBean info = weatherDatas.getAlarms().get(0);
            alarmInfo = new AlarmInfo(info);
            mAlarmPrompt.setCompoundDrawablesRelativeWithIntrinsicBounds(alarmInfo.getIconId(), 0, 0, 0);
            mAlarmPrompt.setTextColor(alarmInfo.getColor());
            mAlarmPrompt.setText(alarmInfo.getWarningFullName());
            mAlarmPrompt.setVisibility(View.VISIBLE);
        } else {
            mAlarmPrompt.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.prompt_alarm:
            ((HomeActivity) getActivity()).showAlarmView(alarmDatas);
            break;
        case R.id.empty_message_view:
            ((HomeActivity) getActivity()).refreshData();
            break;
        default:
            break;
        }
    }
}
