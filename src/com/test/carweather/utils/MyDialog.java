package com.test.carweather.utils;

import java.util.ArrayList;
import java.util.List;

import com.test.carweather.R;
import com.test.carweather.adapter.CommonAdapter;
import com.test.carweather.adapter.ViewHolder;
import com.test.carweather.entity.AlarmInfo;
import com.test.carweather.entity.AlarmResponseEntity;
import com.test.carweather.entity.AlarmResponseEntity.AlarmsBean;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

public class MyDialog extends Dialog {

    private ListView lv;
    private AlarmResponseEntity mAlarms;
    private List<AlarmsBean> mData = new ArrayList<AlarmsBean>();
    private CommonAdapter<AlarmsBean> mAdapter;
    private Context mContext;

    public MyDialog(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_alarm);
        lv = (ListView) findViewById(R.id.alarm_lv);
        mAdapter = new CommonAdapter<AlarmsBean>(mContext, R.layout.item_alarm, mData) {

            @Override
            protected void convert(ViewHolder viewHolder, AlarmsBean item, int position) {
                if (mAlarms != null) {
                    AlarmInfo alarmInfo = new AlarmInfo(item);
                    viewHolder.setTextDrawableLeft(R.id.alarm_title, alarmInfo.getIconId());
                    viewHolder.setTextColor(R.id.alarm_title, alarmInfo.getColor());
                    viewHolder.setText(R.id.alarm_title, alarmInfo.getWarningFullName());
                    viewHolder.setText(R.id.alarm_content, alarmInfo.getWarningContent());
                }
            }
        };
        lv.setAdapter(mAdapter);
    }

    public void setAlarm(AlarmResponseEntity mAlarms) {
        this.mAlarms = mAlarms;
        mData = mAlarms.getAlarms();
        mAdapter.updateData(mData);
    }
}
