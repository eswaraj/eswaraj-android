package com.eswaraj.app.eswaraj.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesImagesEvent;
import com.eswaraj.app.eswaraj.events.GetUserEvent;
import com.eswaraj.app.eswaraj.events.UserContinueEvent;
import com.eswaraj.app.eswaraj.fragments.LoginFragment;
import com.eswaraj.app.eswaraj.util.LocationUtil;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
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
    MiddlewareServiceImpl middlewareService;
    @Inject
    EventBus eventBus;
    @Inject
    UserSessionUtil userSession;
    @Inject
    Context applicationContext;


    //Maintain async task return state
    Boolean loginDone;
    Boolean serverDataDownloadDone;
    Boolean redirectDone;



    @Override
    protected void onStart() {
        super.onStart();

        locationUtil.subscribe(applicationContext, false);
        loginFragment.notifyServiceAvailability(checkLocationAndInternet() || (middlewareService.isCategoriesDataAvailable(this) && middlewareService.isCategoriesImagesAvailable(this)));
    }

    @Override
    protected void onStop() {
        locationUtil.unsubscribe();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.loginFragment);

        eventBus.registerSticky(this);

        //Set up initial state
        loginDone = false;
        serverDataDownloadDone = false;
        redirectDone = false;

        middlewareService.loadCategoriesData(this, true);

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

    public void onEventMainThread(GetCategoriesDataEvent event) {
        if(event.getSuccess()) {
            //Launch image download now.
            middlewareService.loadCategoriesImages(this, event.getCategoryList(), false);
        }
        else {
            Toast.makeText(this, "Could not fetch categories from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
            //Show retry button which will re-trigger the request.
        }
    }

    public void onEventMainThread(GetUserEvent event) {
        if(event.getSuccess()) {
            Log.d("LoginActivity", "GetUserEvent:Success");
            loginDone = true;
            if(serverDataDownloadDone) {
                appReady();
            }
        }
    }


    public void onEventMainThread(GetCategoriesImagesEvent event) {
        if(event.getSuccess()) {
            Log.d("LoginActivity", "GetCategoriesImagesEvent:Success");
            serverDataDownloadDone = true;
            if (loginDone) {
                appReady();
            }
        }
        else {
            Toast.makeText(this, "Could not fetch all categories images from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
            //Show retry button which will re-trigger the request.
            serverDataDownloadDone = true;
            if (loginDone) {
                appReady();
            }
        }
    }

    public void onEventMainThread(UserContinueEvent event) {
        takeUserToNextScreen();
    }


    private void appReady() {
        loginFragment.setShowInstruction(!userSession.isUserLocationKnown() && userSession.isUserLoggedIn(this));
        loginFragment.notifyAppReady();
        if(userSession.isUserLocationKnown()) {
            takeUserToNextScreen();
        }
    }

    public void takeUserToNextScreen() {
        Log.d("LoginActivity", "takeUserToNextScreen");
        if(!checkLocationAndInternet()) {
            return;
        }
        synchronized(this) {
            if (redirectDone) {
                return;
            } else {
                redirectDone = true;
                Intent i = null;
                if(userSession.isUserLocationKnown() || !userSession.isUserLoggedIn(this)) {
                    i = new Intent(this, HomeActivity.class);
                }
                else {
                    i = new Intent(this, MarkLocationActivity.class);
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
