package com.eswaraj.app.eswaraj.util;

import com.eswaraj.app.eswaraj.interfaces.FacebookLoginInterface;

public class FacebookLoginUtil {

    public void startFacebookLogin(FacebookLoginInterface context) {
        //This is not actual implementation hence calling the callback handler here.
        //This should be called when the login is complete
        context.onFacebookLoginDone();
        return;
    }

    public Boolean isUserLoggedIn() {
        //Return false by default
        return false;
    }
}
