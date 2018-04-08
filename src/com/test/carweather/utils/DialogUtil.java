package com.test.carweather.utils;

import com.test.carweather.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class DialogUtil {

    public static void showCommonDialog(Context context, final String msg, final View.OnClickListener confirmListener) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        Window window = dialog.getWindow();
        dialog.show();
        window.setContentView(R.layout.alert_common);

        TextView message = (TextView) window.findViewById(R.id.alert_message);
        message.setText(msg);

        Button leftButton = (Button) window.findViewById(R.id.alert_bt_left);
        Button rightButton = (Button) window.findViewById(R.id.alert_bt_right);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (confirmListener != null) {
                    confirmListener.onClick(v);
                }
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public static AlertDialog showNetDialog(Context context, final View.OnClickListener confirmListener) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        Window window = dialog.getWindow();
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
        window.setContentView(R.layout.dialog_net);

        Button leftButton = (Button) window.findViewById(R.id.alert_bt_left);
        Button rightButton = (Button) window.findViewById(R.id.alert_bt_right);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (confirmListener != null) {
                    confirmListener.onClick(v);
                }
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                case KeyEvent.KEYCODE_HOME:
                case KeyEvent.KEYCODE_NAVI:
                case KeyEvent.KEYCODE_CALL:
                case KeyEvent.KEYCODE_SETTINGS:
                case KeyEvent.KEYCODE_F5:
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });
        return dialog;
    }

//    public static void showHintDialog(Context context, final int id, final String msg) {
//        final AlertDialog dialog = new AlertDialog.Builder(context).create();
//        Window window = dialog.getWindow();
//        dialog.show();
//        window.setContentView(R.layout.alert_hint);
//        TextView message = (TextView) window.findViewById(R.id.alert_message);
//        message.setText(msg);
//    }

    public static Dialog createLoadingDialog(Context context, String msg) {
        View view = LayoutInflater.from(context).inflate(R.layout.load_dialog, null);
        TextView tipText = (TextView) view.findViewById(R.id.message);
        tipText.setText(msg);

        Dialog loadingDialog = new Dialog(context, R.style.CustomDialog);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 150));
        return loadingDialog;
    }
}
