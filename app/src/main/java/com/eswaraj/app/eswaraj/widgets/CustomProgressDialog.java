package com.eswaraj.app.eswaraj.widgets;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

public class CustomProgressDialog extends ProgressDialog {

    private Context context;
    private Boolean onBackButtonExitActivity;

    public CustomProgressDialog(Context context) {
        super(context);
        this.context = context;
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public CustomProgressDialog(Context context, Boolean cancelable, Boolean onBackButtonExitActivity, String message) {
        super(context);
        this.onBackButtonExitActivity = onBackButtonExitActivity;
        this.context = context;
        setCancelable(cancelable);
        setMessage(message);
    }

    public CustomProgressDialog(Context context, int theme, Boolean cancelable, Boolean onBackButtonExitActivity, String message) {
        super(context, theme);
        this.onBackButtonExitActivity = onBackButtonExitActivity;
        this.context = context;
        setCancelable(cancelable);
        setMessage(message);
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
