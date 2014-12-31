package com.eswaraj.app.eswaraj.util;


import android.content.Context;
import android.util.Log;

import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.PreferenceConstants;
import com.eswaraj.app.eswaraj.helpers.SharedPreferencesHelper;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.web.dto.UserDto;

import javax.inject.Inject;

public class UserSessionUtil extends BaseClass {

    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    SharedPreferencesHelper sharedPreferencesHelper;

    private UserDto user;
    private String userRevGeocodedLocation;
    private String token;

    public String getUserRevGeocodedLocation() {
        return userRevGeocodedLocation;
    }

    public void setUserRevGeocodedLocation(String userRevGeocodedLocation) {
        this.userRevGeocodedLocation = userRevGeocodedLocation;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
        Log.e("UserSessionUtil", user.toString());
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean isUserLocationKnown() {
        if(user != null) {
            if (user.getPerson().getPersonAddress() != null) {
                if (user.getPerson().getPersonAddress().getLongitude() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean isUserLoggedIn(Context context) {
        return (user != null);
        //return middlewareService.isUserDataAvailable(context);
    }

    public void logoutUser(Context context) {
        //Update the cache with null to indicate that user has logged out and user object in cache is not valid anymore
        middlewareService.updateUserData(context, null);
        setUserSkipMarkLocation(context, false);
    }

    public String getProfilePhoto() {
        return user.getPerson().getProfilePhoto();
    }

    public Boolean didUserSkipMarkLocation(Context context) {
        return sharedPreferencesHelper.getBoolean(context, PreferenceConstants.FILE_USER_PREFS, PreferenceConstants.MARK_LOCATION_SKIPPED, false);
    }

    public void setUserSkipMarkLocation(Context context, Boolean skip) {
        sharedPreferencesHelper.putBoolean(context, PreferenceConstants.FILE_USER_PREFS, PreferenceConstants.MARK_LOCATION_SKIPPED, skip);
    }
}
