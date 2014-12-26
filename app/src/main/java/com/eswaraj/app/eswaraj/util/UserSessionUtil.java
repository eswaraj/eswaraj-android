package com.eswaraj.app.eswaraj.util;


import android.content.Context;

import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.web.dto.UserDto;

import javax.inject.Inject;

public class UserSessionUtil extends BaseClass {

    @Inject
    MiddlewareServiceImpl middlewareService;

    private UserDto user;

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public Boolean isUserLocationKnown() {
        if(user.getPerson().getPersonAddress() != null) {
            if(user.getPerson().getPersonAddress().getLongitude() != null) {
                 return true;
            }
        }
        return false;
    }

    public Boolean isUserLoggedIn(Context context) {
        return middlewareService.isUserDataAvailable(context);
    }

    public void logoutUser(Context context) {
        //Update the cache with null to indicate that user has logged out and user object in cache is not valid anymore
        middlewareService.updateUserData(context, null);
    }
}
