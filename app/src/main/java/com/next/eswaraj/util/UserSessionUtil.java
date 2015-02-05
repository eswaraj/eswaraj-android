package com.next.eswaraj.util;


import android.content.Context;
import android.graphics.Bitmap;

import com.next.eswaraj.base.BaseClass;
import com.next.eswaraj.config.PreferenceConstants;
import com.next.eswaraj.events.GetProfileImageEvent;
import com.next.eswaraj.helpers.SharedPreferencesHelper;
import com.next.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.web.dto.UserDto;
import com.facebook.Session;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class UserSessionUtil extends BaseClass {

    @Inject
    SharedPreferencesHelper sharedPreferencesHelper;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    EventBus eventBus;

    private UserDto user;
    private String userRevGeocodedLocation;
    private String token;
    private Bitmap userProfilePhoto;
    private Boolean loading = false;

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
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean isUserLocationKnown() {
        if(user != null) {
            if(user.getPerson() != null) {
                if (user.getPerson().getPersonAddress() != null) {
                    if (user.getPerson().getPersonAddress().getLongitude() != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Boolean isUserLoggedIn(Context context) {
        return (user != null);
    }

    public void logoutUser(Context context) {
        //Update the cache with null to indicate that user has logged out and user object in cache is not valid anymore
        middlewareService.updateUserData(context, null);
        setUserSkipMarkLocation(context, false);
        Session session = Session.getActiveSession();
        if (session != null) {
            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
            }
        }
        user = null;
    }

    public Bitmap getUserProfilePhoto() {
        return userProfilePhoto;
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

    public void loadUserProfilePhoto(Context context) {
        if(user != null && getProfilePhoto() != null && !getProfilePhoto().equals("")) {
            if(!loading) {
                eventBus.register(this);
            }
            loading = true;
            middlewareService.loadProfileImage(context, getProfilePhoto(), user.getPerson().getId(), false);
        }
    }

    public void onEventMainThread(GetProfileImageEvent event) {
        if(event.getId().equals(user.getPerson().getId())) {
            if(event.getSuccess()) {
                userProfilePhoto = event.getBitmap();
            }
            eventBus.unregister(this);
            loading = false;
        }
    }

}
