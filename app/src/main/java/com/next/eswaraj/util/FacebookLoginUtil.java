package com.next.eswaraj.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.next.eswaraj.base.BaseClass;
import com.next.eswaraj.events.FacebookSessionEvent;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import java.util.Arrays;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class FacebookLoginUtil extends BaseClass {

    @Inject
    EventBus eventBus;

    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback;
    private String TAG = "FacebookLoginUtil";

    public void onCreate(Activity context, Bundle savedInstanceState) {
        callback = new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChange(session, state, exception);
            }
        };
        uiHelper = new UiLifecycleHelper(context, callback);
        uiHelper.onCreate(savedInstanceState);
    }

    public void onResume() {
        Session session = Session.getActiveSession();
        if (session != null &&  (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
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

    public void onSessionStateChange(Session session, SessionState state, Exception exception) {
        FacebookSessionEvent event = new FacebookSessionEvent();
        event.setSuccess(true);
        event.setSession(session);
        if (state.isOpened()) {
            Log.d(TAG, "Logged in...");
            event.setLogin(true);
            eventBus.post(event);
        } else if (state.isClosed()) {
            Log.d(TAG, "Logged out...");
            event.setLogin(false);
            eventBus.post(event);
        }
    }
}
