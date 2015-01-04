package com.eswaraj.app.eswaraj.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.LoginStatusEvent;
import com.eswaraj.app.eswaraj.events.UserContinueEvent;
import com.eswaraj.app.eswaraj.fragments.LoginFragment;
import com.eswaraj.app.eswaraj.util.LocationUtil;
import com.eswaraj.app.eswaraj.util.InternetServicesCheckUtil;
import com.eswaraj.app.eswaraj.util.LocationServicesCheckUtil;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.facebook.AppEventsLogger;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class LoginActivity extends BaseActivity {

    private LoginFragment loginFragment;

    @Inject
    LocationUtil locationUtil;
    @Inject
    InternetServicesCheckUtil internetServicesCheckUtil;
    @Inject
    LocationServicesCheckUtil locationServicesCheckUtil;
    @Inject
    EventBus eventBus;
    @Inject
    UserSessionUtil userSession;
    @Inject
    Context applicationContext;


    //Maintain async task return state
    Boolean redirectDone;
    Boolean dialogMode;



    @Override
    protected void onStart() {
        super.onStart();

        locationUtil.subscribe(applicationContext, false);
        if(!dialogMode) {
            loginFragment.notifyServiceAvailability(checkLocationAndInternet());
        }
    }

    @Override
    protected void onStop() {
        locationUtil.unsubscribe();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogMode = getIntent().getBooleanExtra("MODE", false);

        if(dialogMode) {
            setContentView(R.layout.activity_login);
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int screenWidth = (int) (metrics.widthPixels);
            int screenHeight = (int) (metrics.heightPixels * 0.50);
            getWindow().setLayout(screenWidth, screenHeight);

        }
        else {
            setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            setContentView(R.layout.activity_login);
        }

        //loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.loginFragment);
        loginFragment = new LoginFragment();
        loginFragment.setMode(dialogMode);
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, loginFragment).commit();

        eventBus.register(this);

        //Set up initial state
        redirectDone = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }


    public void onEventMainThread(LoginStatusEvent event) {
        if(event.getSuccess()) {
            Log.d("LoginActivity", "UserDataAvailable:Success");
            appReady();
        }
    }


    public void onEventMainThread(UserContinueEvent event) {
        takeUserToNextScreen();
    }


    private void appReady() {
        if(dialogMode) {
            if (getParent() == null) {
                setResult(Activity.RESULT_OK, null);
            } else {
                getParent().setResult(Activity.RESULT_OK, null);
            }
            finish();
        }
        else {
            loginFragment.setShowInstruction(!userSession.isUserLocationKnown() && userSession.isUserLoggedIn(this) && !userSession.didUserSkipMarkLocation(this));
            loginFragment.notifyAppReady();
            if (userSession.isUserLocationKnown()) {
                takeUserToNextScreen();
            }
        }
    }

    public void takeUserToNextScreen() {
        Log.d("LoginActivity", "takeUserToNextScreen");
        /*
        if(!checkLocationAndInternet()) {
            return;
        }
        */
        synchronized(this) {
            if (redirectDone) {
                return;
            } else {
                redirectDone = true;
                Intent i = null;
                if(userSession.isUserLocationKnown() || !userSession.isUserLoggedIn(this) || userSession.didUserSkipMarkLocation(this)) {
                    i = new Intent(this, HomeActivity.class);
                }
                else {
                    i = new Intent(this, MarkLocationActivity.class);
                    i.putExtra("MODE", false);
                }
                startActivity(i);
                locationUtil.stopLocationService();
                eventBus.unregister(this);
                finish(); //User cant press back to return to this activity
            }
        }
    }

    private Boolean checkLocationAndInternet() {
        return internetServicesCheckUtil.isServiceAvailable(this) && locationServicesCheckUtil.isServiceAvailable(this);
    }

}
