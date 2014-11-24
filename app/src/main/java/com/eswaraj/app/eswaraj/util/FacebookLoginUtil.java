package com.eswaraj.app.eswaraj.util;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.eswaraj.app.eswaraj.interfaces.FacebookLoginInterface;

public class FacebookLoginUtil {

    private FacebookLoginInterface context;

    public FacebookLoginUtil() {
        this.context = null;
    }

    public FacebookLoginUtil(FacebookLoginInterface context) {
        this.context = context;
    }

    public void startFacebookLogin() {
        //This is not actual implementation hence calling the callback handler here.
        //This should be called when the login is complete
        if(this.context != null) {
                this.context.onLoginDone();
        }
        return;
    }

    public Boolean isUserLoggedIn() {
        //Return false by default
        return false;
    }
}
