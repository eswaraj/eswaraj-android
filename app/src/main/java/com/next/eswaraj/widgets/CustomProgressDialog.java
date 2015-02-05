package com.next.eswaraj.widgets;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CustomProgressDialog extends SweetAlertDialog {

    private Context context;
    private Boolean onBackButtonExitActivity;

    public CustomProgressDialog(Context context) {
        super(context, SweetAlertDialog.PROGRESS_TYPE);
        getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        this.context = context;
    }

    public CustomProgressDialog(Context context, Boolean cancelable, Boolean onBackButtonExitActivity, String message) {
        super(context, SweetAlertDialog.PROGRESS_TYPE);
        this.onBackButtonExitActivity = onBackButtonExitActivity;
        getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        this.context = context;
        setCancelable(cancelable);
        setTitleText(message);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
        if(onBackButtonExitActivity) {
            ((Activity) context).onBackPressed();
        }
    }
}
