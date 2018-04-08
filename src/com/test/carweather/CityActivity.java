package com.test.carweather;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import com.test.carweather.adapter.ItemViewDelegate;
import com.test.carweather.adapter.MultiItemTypeAdapter;
import com.test.carweather.adapter.ViewHolder;
import com.test.carweather.entity.SimpleInfo;
import com.test.carweather.entity.WeatherInfoEntity;
import com.test.carweather.model.CityModel;
import com.test.carweather.model.ModelManager;
import com.test.carweather.model.WeatherModel;
import com.test.carweather.utils.CarWeatherUtils;
import com.test.carweather.utils.Check;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

public class CityActivity extends Activity implements OnClickListener {

    private static final String TAG = CityActivity.class.getSimpleName();

    private GridView gridView;
    private MultiItemTypeAdapter<String> mAdapter;
    private int mCityCount = 1;
    private final int CARD_WIDTH = 230;
    private final int CARD_SPACING = 10;
    private TextView mEdit;
    private TextView mFinishEdit;
    private boolean isEditState;
    private int mSelectedPosition;
    private List<String> mDatas = new ArrayList<String>();
    private List<String> defaultAdded = new ArrayList<String>();
    private List<String> mCityNameList = new ArrayList<String>();
    private WeatherInfoEntity mLocationWeather;
    private List<WeatherInfoEntity> mWeatherEntities = new ArrayList<WeatherInfoEntity>();
    private CityModel mCityModel;
    private WeatherModel mWeatherModel;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        mCityModel = ModelManager.getModel(CityModel.class);
        mWeatherModel = ModelManager.getModel(WeatherModel.class);

        mEdit = (TextView) findViewById(R.id.card_edit);
        mEdit.setOnClickListener(this);
        mFinishEdit = (TextView) findViewById(R.id.edit_complete);
        mFinishEdit.setOnClickListener(this);

        gridView = (GridView) findViewById(R.id.city_grid);
        //gridView.setNumColumns(10);
        gridView.setColumnWidth(CARD_WIDTH);
        gridView.setHorizontalSpacing(CARD_SPACING);
        //gridView.setVerticalSpacing(30);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));

        mAdapter = new MultiItemTypeAdapter(this, mDatas);
        mAdapter.addItemViewDelegate(new CommonItemDelagate());
        mAdapter.addItemViewDelegate(new AddItemDelagate());

        gridView.setAdapter(mAdapter);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        //defaultAdded = mCityModel.getAddCities();
        defaultAdded = new ArrayList<String>(mCityModel.getAddCities());
        //mCityNameList = mCityModel.getAddCityNames();
        mCityNameList = new ArrayList<String>(mCityModel.getAddCityNames());
        mCityCount = defaultAdded.size() + 1;
        mLocationWeather = mWeatherModel.getLocationWeather();
        //mWeatherEntities = mWeatherModel.getWeatherList();
        mWeatherEntities = new ArrayList<WeatherInfoEntity>(mWeatherModel.getWeatherList());
        refreshDatas(mCityCount);
    }

    private void refreshDatas(int length) {
        Log.d(TAG, "refreshDatas: " + length);
        mDatas.clear();
        for (int i = 0; i < length; i++) {
            mDatas.add((char) i + "");
        }
        mAdapter.updateData(mDatas);

        Drawable drawable = getResources().getDrawable(length > 1 ? R.drawable.manage_edit : R.drawable.manage_edit_gray);
        mEdit.setEnabled(length > 1);
        mEdit.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        mEdit.setTextColor(length > 1 ? Color.WHITE : Color.GRAY);

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.width = length * (CARD_WIDTH + CARD_SPACING);
        gridView.setLayoutParams(params);
        gridView.setNumColumns(length);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void goToSearch() {
        Intent intent = new Intent(CityActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    private void goHome(int position) {
        Intent intent = new Intent(CityActivity.this, HomeActivity.class);
        intent.putExtra("page", position);
        startActivity(intent);
        finish();
    }

    private void deleteCity(String id, String name) {
        //showDialog(id, name);
        showCustomDialog(id, name);
    }

    private void confirmDelete(String id, String cityName) {
        mCityCount--;
        mWeatherEntities.remove(mSelectedPosition);
        ModelManager.getModel(WeatherModel.class).setWeatherList(mWeatherEntities);
        ModelManager.getModel(CityModel.class).removeCity(id,cityName);
        refreshDatas(mCityCount);
    }

    private void refreshSelectedCity(int position) {
        goHome(position + 1);
    }

    private void addCity() {
        if (mCityCount >= 10) {
            Toast.makeText(this, getString(R.string.prompt_max_city), Toast.LENGTH_SHORT).show();;
            return;
        }
        goToSearch();
    }

    private void editCityList() {
        isEditState = true;
        mEdit.setVisibility(View.GONE);
        mFinishEdit.setVisibility(View.VISIBLE);
        mAdapter.notifyDataSetChanged();

    }

    private void finishEdit() {
        isEditState = false;
        mEdit.setVisibility(View.VISIBLE);
        mFinishEdit.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.card_edit:
            editCityList();
            break;
        case R.id.edit_complete:
            finishEdit();
            break;

        default:
            break;
        }
    }

    private void showCustomDialog(final String id, final String msg) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        Window window = dialog.getWindow();
        dialog.show();
        window.setContentView(R.layout.alert_common);

        TextView message = (TextView) window.findViewById(R.id.alert_message);
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.prompt_delete)).append("“").append(msg).append("”").append("吗？");
        message.setText(sb.toString());

        Button leftButton = (Button) window.findViewById(R.id.alert_bt_left);
        Button rightButton = (Button) window.findViewById(R.id.alert_bt_right);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete(id,msg);
                dialog.dismiss();
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private class CommonItemDelagate implements ItemViewDelegate {
        @Override
        public int getItemViewLayoutId() {
            return R.layout.card_city;
        }

        @Override
        public boolean isForViewType(Object item, int position) {
            return position != mCityCount - 1;
        }

        @Override
        public void convert(ViewHolder holder, Object o, int position) {
            final int currentPosition = position;
            WeatherInfoEntity mWeatherEntity = null;
//            if (position == 0) {
//                mWeatherEntity = mLocationWeather;
//            } else if (!Check.isEmpty(mWeatherEntities) && mWeatherEntities.size() > position - 1) {
//                mWeatherEntity = mWeatherEntities.get(position - 1);
//            }

            if (!Check.isEmpty(mWeatherEntities) && mWeatherEntities.size() > position) {
                mWeatherEntity = mWeatherEntities.get(position);
            }

            if (mWeatherEntity == null) {
                holder.setVisible(R.id.city_card_view, false);
                holder.setVisible(R.id.city_card_empty, true);
            } else {
                holder.setVisible(R.id.city_card_view, true);
                holder.setVisible(R.id.city_card_empty, false);

                SimpleInfo info = new SimpleInfo(mWeatherEntity);
                holder.setText(R.id.city_card_location, info.cityName);
                holder.setText(R.id.city_card_state, info.weatherStatus);
                holder.setText(R.id.city_card_tempture, info.temp);
                holder.setImageResource(R.id.img_card_state, CarWeatherUtils.getWeatherIconId(info.weatherIconId));
            }

//            if (position != 0) {
//                holder.setTextDrawableLeft(R.id.city_card_location, 0);
//                holder.setTextDrawableLeft(R.id.city_card_empty, 0);
//                //holder.setText(R.id.city_card_location, mCityNameList.get(currentPosition - 1));
//            }
            holder.setTextDrawableLeft(R.id.city_card_location, 0);
            holder.setTextDrawableLeft(R.id.city_card_empty, 0);

            holder.setOnClickListener(R.id.card_city, new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mSelectedPosition = currentPosition;
                    if (!isEditState) {
                        refreshSelectedCity(mSelectedPosition);
                    }
                }
            });

            final WeatherInfoEntity mCurrentWeatherEntity = mWeatherEntity;
            holder.setOnClickListener(R.id.card_delete, new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mSelectedPosition = currentPosition;
                    if (isEditState) {
                        deleteCity(mCurrentWeatherEntity.city_name, mCurrentWeatherEntity.city_name);
                    }
                }
            });
            if (isEditState) {
                holder.setVisible(R.id.city_card_tempture, false);
                holder.setVisible(R.id.city_card_state, false);
//                if (currentPosition != 0) {
//                    holder.setVisible(R.id.card_delete, true);
//                }
                holder.setVisible(R.id.card_delete, true);
            } else {
                holder.setVisible(R.id.city_card_tempture, true);
                holder.setVisible(R.id.city_card_state, true);
                holder.setVisible(R.id.card_delete, false);
            }
        }
    }

    private class AddItemDelagate implements ItemViewDelegate {
        @Override
        public int getItemViewLayoutId() {
            return R.layout.card_add;
        }

        @Override
        public boolean isForViewType(Object item, int position) {
            return position == mCityCount - 1;
        }

        @Override
        public void convert(ViewHolder holder, Object o, int position) {
            holder.setOnClickListener(R.id.card_add, new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!isEditState) {
                        addCity();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        if (isEditState) {
            finishEdit();
            return;
        }
        super.onBackPressed();
    }
}
