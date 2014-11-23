package com.eswaraj.app.eswaraj.handlers;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.eswaraj.app.eswaraj.interfaces.FacebookLoginInterface;
import com.eswaraj.app.eswaraj.util.FacebookLoginUtil;

public class LoginButtonClickHandler implements View.OnClickListener{
    private Context context;
    private FacebookLoginUtil facebookLoginUtil;

    public LoginButtonClickHandler() {
        this.context = null;
        this.facebookLoginUtil = null;
    }

    public LoginButtonClickHandler(Context context, FacebookLoginUtil facebookLoginUtil) {
        this.context = context;
        this.facebookLoginUtil = facebookLoginUtil;
    }
    @Override
    public void onClick(View view) {
        facebookLoginUtil.startFacebookLogin();
    }

}
