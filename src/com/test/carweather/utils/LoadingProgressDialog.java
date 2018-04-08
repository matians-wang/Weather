package com.test.carweather.utils;

import com.test.carweather.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

public class LoadingProgressDialog extends ProgressDialog{

    public LoadingProgressDialog(Context context) {
        super(context, R.style.CustomDialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_progress_dialog);
    }
}
