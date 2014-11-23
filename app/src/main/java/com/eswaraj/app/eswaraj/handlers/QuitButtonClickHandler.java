package com.eswaraj.app.eswaraj.handlers;


import android.app.Activity;
import android.content.Context;
import android.view.View;

public class QuitButtonClickHandler implements View.OnClickListener{
    Context context;

    public QuitButtonClickHandler() {
        this.context = null;
    }

    public QuitButtonClickHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        if(this.context != null) {
            ((Activity)this.context).finish();
        }
    }
}
