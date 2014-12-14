package com.eswaraj.app.eswaraj.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.interfaces.FacebookLoginInterface;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import java.util.Arrays;

import javax.inject.Inject;

public class FacebookLoginUtil extends BaseClass {

    @Inject
    Cache cache;

    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback;
    private String TAG = "FacebookLoginUtil";

    public void onCreate(final FacebookLoginInterface context, Bundle savedInstanceState) {
        callback = new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChange(context, session, state, exception);
            }
        };
        uiHelper = new UiLifecycleHelper(((Fragment)context).getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    public void onResume(FacebookLoginInterface context) {
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(context, session, session.getState(), null);
        }
        uiHelper.onResume();
    }

    public void onPause() {
        uiHelper.onPause();
    }

    public void onDestroy() {
        uiHelper.onDestroy();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    public void onSaveInstanceState(Bundle outState) {
        uiHelper.onSaveInstanceState(outState);
    }

    public Boolean isUserLoggedIn() {
        Session session = Session.getActiveSession();
        return (session != null && session.isOpened());
    }

    public void setup(LoginButton loginButton) {
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
    }

    public void onSessionStateChange(FacebookLoginInterface context, Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.d(TAG, "Logged in...");
            context.onFacebookLoginDone(session);
        } else if (state.isClosed()) {
            Log.d(TAG, "Logged out...");
            //Update the cache with null to indicate that user has logged out and user object in cache is not valid anymore
            cache.updateUserData(((Fragment)context).getActivity(), null);
        }
    }
}
