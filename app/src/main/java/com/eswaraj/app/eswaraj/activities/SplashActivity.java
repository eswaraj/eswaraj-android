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
import com.eswaraj.app.eswaraj.events.UserButtonClickEvent;
import com.eswaraj.app.eswaraj.fragments.SplashFragment;
import com.eswaraj.app.eswaraj.util.LocationUtil;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.util.InternetServicesCheckUtil;
import com.eswaraj.app.eswaraj.util.LocationServicesCheckUtil;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.facebook.AppEventsLogger;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class SplashActivity extends BaseActivity {

    private SplashFragment splashFragment;
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

    //Internet and Location service availability
    Boolean hasNeededServices;

    //Maintain async task return state
    Boolean loginDone;
    Boolean serverDataDownloadDone;
    Boolean redirectDone;



    @Override
    protected void onStart() {
        super.onStart();

        locationUtil.subscribe(applicationContext, false);
        middlewareService.loadCategoriesData(this, true);
        hasNeededServices = checkLocationAndInternet();
        splashFragment.notifyServiceAvailability(hasNeededServices);
    }

    @Override
    protected void onStop() {
        locationUtil.unsubscribe();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashFragment = (SplashFragment) getSupportFragmentManager().findFragmentById(R.id.splashFragment);

        eventBus.registerSticky(this);

        //Set up initial state
        loginDone = false;
        serverDataDownloadDone = false;
        redirectDone = false;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        splashFragment.onActivityResult(requestCode, resultCode, data);
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
            Log.d("SplashActivity", "GetUserEvent:Success");
            loginDone = true;
            if(serverDataDownloadDone) {
                appReady();
            }
        }
    }


    public void onEventMainThread(GetCategoriesImagesEvent event) {
        if(event.getSuccess()) {
            Log.d("SplashActivity", "GetCategoriesImagesEvent:Success");
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

    public void onEventMainThread(UserButtonClickEvent event) {
        takeUserToNextScreen();
    }

    private void appReady() {
        splashFragment.setShowInstruction(!userSession.isUserLocationKnown());
        splashFragment.notifyAppReady();
        if(userSession.isUserLocationKnown()) {
            takeUserToNextScreen();
        }
    }

    public void takeUserToNextScreen() {
        Log.d("SplashActivity", "takeUserToNextScreen");
        if(!hasNeededServices) {
            return;
        }
        synchronized(this) {
            if (redirectDone) {
                return;
            } else {
                redirectDone = true;
                Intent i = null;
                if(userSession.isUserLocationKnown()) {
                    i = new Intent(this, SelectAmenityActivity.class);
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
