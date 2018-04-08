package com.test.carweather.view;

import java.lang.reflect.Field;

import com.test.carweather.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

public class MySearchView extends SearchView {

    public MySearchView(Context context) {
        super(context);
        styleSearchView();
    }

    public MySearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        styleSearchView();
    }

    public void styleSearchView() {
        //设置背景
         this.setBackgroundResource(R.drawable.widget_search_bg);
        //提示文本内容
        //this.setQueryHint("请输入城市名");
        //默认展开
        this.setIconifiedByDefault(false);
        //去除下划线
        int plateId = this.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        LinearLayout plate = (LinearLayout)this.findViewById(plateId);
        plate.setBackgroundColor(Color.TRANSPARENT);
        //设置搜索框EditText
        int searchPlateId = this.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchPlate = (EditText)this.findViewById(searchPlateId);
        //提示文本颜色
        searchPlate.setHintTextColor(getResources().getColor(R.color.white));
        searchPlate.setTextSize(30);
        searchPlate.setTextColor(Color.WHITE);
        searchPlate.setBackgroundColor(Color.TRANSPARENT);
        searchPlate.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        //设置光标样式
        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(searchPlate, R.drawable.cursor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //自定义搜索图标
        int search_mag_icon_id = this.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView search_mag_icon = (ImageView) this.findViewById(search_mag_icon_id);
        search_mag_icon.setImageResource(R.drawable.widget_search_icon);
        search_mag_icon.setScaleType(ImageView.ScaleType.CENTER);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = 40;
        search_mag_icon.setLayoutParams(layoutParams);
        //自定义清除图标
        int search_close_icon_id = this.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView search_close_btn = (ImageView) this.findViewById(search_close_icon_id);
        search_close_btn.setImageResource(R.drawable.widget_search_del);
        search_close_btn.setBackgroundResource(0);
    }
}
