package com.eswaraj.app.eswaraj.handlers;


import android.content.Context;
import android.util.Log;
import android.view.View;

import com.eswaraj.app.eswaraj.interfaces.FacebookLoginInterface;
import com.eswaraj.app.eswaraj.interfaces.LoginSkipInterface;

public class SkipButtonClickHandler implements View.OnClickListener{

    private LoginSkipInterface context;

    public SkipButtonClickHandler() {
        this.context = null;
    }

    public SkipButtonClickHandler(LoginSkipInterface context) {
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        if (this.context != null) {
                this.context.onSkipDone();
        }
    }
}
