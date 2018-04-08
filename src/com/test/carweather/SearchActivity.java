package com.test.carweather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.test.carweather.adapter.CommonAdapter;
import com.test.carweather.adapter.ViewHolder;
import com.test.carweather.entity.AlarmResponseEntity;
import com.test.carweather.entity.CityInfoEntity;
import com.test.carweather.entity.WeatherInfoEntity;
import com.test.carweather.model.CityModel;
import com.test.carweather.model.ModelCallback;
import com.test.carweather.model.ModelManager;
import com.test.carweather.model.WeatherModel;
import com.test.carweather.utils.NetWork;
import com.test.carweather.view.MySearchView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SearchActivity extends Activity
        implements OnClickListener, ModelCallback.WeatherResult, ModelCallback.SearchResult {

    private static final String TAG = SearchActivity.class.getSimpleName();
    public static final String BUNDLE = "city";
    private static final int QUERY_TOKEN = 7;
    private static final String[] PROJECTION = { "_id", "province", "city", "district", "area_id" };
    private String mWhereClause;
    private Button mCancel;
    private Button mResultCity;
    private TextView mResultTitle;
    private TextView mSearchHint;
    private MySearchView mSearchBox;
    private ListView lv;
    private SimpleCursorAdapter adapter;
    private List<String> mDatas = new ArrayList<String>();

    private CommonAdapter<CityInfoEntity> mAdapter;
    private List<CityInfoEntity> cityInfoEntities = new ArrayList<CityInfoEntity>();

    private QueryHandler mQueryHandler;
    private ProgressDialog mProgressDialog;

    private String mCityName;
    private String mCityId;
    private Set<String> defaultAdded = new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //createDB();

        mQueryHandler = new QueryHandler(this);
        mSearchBox = (MySearchView) findViewById(R.id.search_box);
        mSearchBox.setQueryHint(getString(R.string.query_hint));
        mSearchBox.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                confirm(query);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchBox.getWindowToken(), 0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    Log.d(TAG, "onClear");
                    if (mAdapter != null) {
                        cityInfoEntities.clear();
                        mAdapter.updateData(cityInfoEntities);
                    }
                }
                return false;
            }
        });

        mCancel = (Button) findViewById(R.id.search_cancel);
        mResultCity = (Button) findViewById(R.id.search_result_city);
        mResultTitle = (TextView) findViewById(R.id.search_result_title);
        mSearchHint = (TextView) findViewById(R.id.search_hint);
        mCancel.setOnClickListener(this);
        mResultCity.setOnClickListener(this);

        lv = (ListView) findViewById(R.id.lv_search_result);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView mProvince = (TextView) view.findViewById(R.id.province_name);
                TextView mCity = (TextView) view.findViewById(R.id.city_name);
                TextView mDistrict = (TextView) view.findViewById(R.id.district_name);
                TextView mId = (TextView) view.findViewById(R.id.area_id);
                //mCityName = new StringBuffer(mCity.getText().toString() + mDistrict.getText().toString()).toString();
                mCityName = mCity.getText().toString();
                mCityId = mId.getText().toString();
                selectCity(mCityId,mCityName);
            }
        });

        mAdapter = new CommonAdapter<CityInfoEntity>(this, R.layout.item_search_result, cityInfoEntities) {

            @Override
            protected void convert(ViewHolder viewHolder, CityInfoEntity item, int position) {
                //viewHolder.setText(R.id.province_name, item.city_province);
                viewHolder.setText(R.id.city_name, item.city_name);
                //viewHolder.setText(R.id.district_name, item.city_county);
            }
        };
        lv.setAdapter(mAdapter);

//        adapter = new SimpleCursorAdapter(SearchActivity.this, R.layout.item_search_result, null,
//                new String[] { CarWeather.Cities.COLUMN_NAME_PROVINCE, CarWeather.Cities.COLUMN_NAME_CITY,
//                        CarWeather.Cities.COLUMN_NAME_DISTRICT, CarWeather.Cities.COLUMN_NAME_POLYPHONE },
//                new int[] { R.id.province_name, R.id.city_name, R.id.district_name, R.id.area_id }, 0);
//        lv.setAdapter(adapter);
    }

    public void createDB() {

        DBHelper helper = new DBHelper(getApplicationContext());
        try {
            helper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bindSearchModel() {
        ModelManager.getModel(CityModel.class).setSearchListener(this);
    }

    public void unBindModel() {
        ModelManager.getModel(CityModel.class).setSearchListener(null);
        ModelManager.getModel(CityModel.class).setListener(null);
        ModelManager.getModel(WeatherModel.class).setListener(null);
    }

    @Override
    public void onResume() {
        bindSearchModel();
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        closeCursor();
        hideProgress();
        unBindModel();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        hideProgress();
        super.onDestroy();
    }

    private void refreshData(String result) {
        mResultCity.setVisibility(View.VISIBLE);
        mResultTitle.setVisibility(View.VISIBLE);
        mResultCity.setText(result);
    }

    private void selectCity(String cityId, String cityName) {
        if (!NetWork.isAvailable(WeatherApplication.getContext())) {
            toggleSearchingView(true, R.string.empty_message_hint);
            return;
        }
        showProgress();
        ModelManager.getModel(WeatherModel.class).setListener(SearchActivity.this);
        ModelManager.getModel(CityModel.class).addCity(cityId, cityName);
    }

    private void goToSetting() {
        finish();
    }

    private void confirm(String query) {
        searchCity(query);
    }

    private void cancel() {
        finish();
    }

    private void searchCity(String query) {
        toggleSearchingView(true, R.string.search_hint_searching);
        startQuery(query);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.search_cancel:
            cancel();
            break;
        case R.id.search_result_city:
            selectCity(mCityId, mCityName);
            break;
        default:
            break;
        }
    }

    private void showProgress() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.progress_dialog_msg));
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    private void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void closeCursor() {
        if (adapter != null && adapter.getCursor() != null) {
            adapter.getCursor().close();
        }
    }

    private void startQuery(String mCityName) {
        Log.d(TAG, "mCityName: " + mCityName);
        ModelManager.getModel(CityModel.class).requestSearch(mCityName);
//        if (mQueryHandler == null) {
//            return;
//        }
//        mQueryHandler.cancelOperation(QUERY_TOKEN);
//
//        // fuzzy query
//        StringBuffer whereClause = new StringBuffer(
//                CarWeather.Cities.COLUMN_NAME_PROVINCE + " LIKE '%" + mCityName + "%'");
//        whereClause.append(" OR " + CarWeather.Cities.COLUMN_NAME_CITY + " LIKE '%" + mCityName + "%'");
//        whereClause.append(" OR " + CarWeather.Cities.COLUMN_NAME_DISTRICT + " LIKE '%" + mCityName + "%'");
//        mWhereClause = whereClause.toString();
//        mQueryHandler.startQuery(QUERY_TOKEN, null, CarWeather.Cities.CONTENT_URI, PROJECTION, mWhereClause,
//                (String[]) null, CarWeather.Cities.DEFAULT_SORT_ORDER);
    }

    private void toggleSearchingView(boolean isVisible, int resId) {

        if (isVisible) {
            lv.setVisibility(View.GONE);
            mResultTitle.setVisibility(View.GONE);

            mSearchHint.setText(resId);
            mSearchHint.setVisibility(View.VISIBLE);
        } else {
            mSearchHint.setVisibility(View.GONE);

            mResultTitle.setVisibility(View.VISIBLE);
            lv.setVisibility(View.VISIBLE);
        }

    }

    private final class QueryHandler extends AsyncQueryHandler {

        public QueryHandler(Context context) {
            super(context.getContentResolver());
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            Log.d(TAG, "onQueryComplete: ");
            if (cursor != null && cursor.getCount() != 0) {
                adapter.changeCursor(cursor);
                toggleSearchingView(false, 0);
            } else {
                toggleSearchingView(true, R.string.search_hint_empty);
            }
        }
    }

    @Override
    public void onWeather(List<WeatherInfoEntity> mWeathers) {
        Log.d(TAG, "onWeather");
        hideProgress();
        goToSetting();
    }

    @Override
    public void onAlarmEntity(AlarmResponseEntity alarmEntity) {

    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onAppBack");
        super.onBackPressed();
    }

    @Override
    public void onSearch(List<CityInfoEntity> cityInfoEntities) {
        Log.d(TAG, "onSearch");
        if (cityInfoEntities != null) {
            this.cityInfoEntities = cityInfoEntities;
            mAdapter.updateData(cityInfoEntities);
            toggleSearchingView(false, 0);
        }else {
             toggleSearchingView(true, R.string.search_hint_empty);
        }
    }

}
