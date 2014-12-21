package com.eswaraj.app.eswaraj.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesImagesEvent;
import com.eswaraj.app.eswaraj.events.GetUserEvent;
import com.eswaraj.app.eswaraj.fragments.SplashFragment;
import com.eswaraj.app.eswaraj.interfaces.FacebookLoginInterface;
import com.eswaraj.app.eswaraj.util.LocationUtil;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.util.InternetServicesCheckUtil;
import com.eswaraj.app.eswaraj.util.LocationServicesCheckUtil;
import com.eswaraj.web.dto.UserDto;
import com.facebook.AppEventsLogger;
import com.facebook.Session;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class SplashActivity extends BaseActivity implements FacebookLoginInterface{

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

    //Logged-in user
    UserDto userDto;

    //Internet and Location service availability
    Boolean hasNeededServices;

    //Maintain async task return state
    Boolean loginDone;
    Boolean serverDataDownloadDone;
    Boolean redirectDone;
    Boolean userLocationKnown;

    //Facebook Session
    Session session;

    //TODO:Remove this after testing
    Boolean debugMode = false;

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.registerSticky(this);

        //Start location service
        locationUtil.startLocationService();
        //Start data download from server, if needed
        middlewareService.loadCategoriesData(this, true);
        //Check if Internet connection and Location services are present. If not, don't proceed.
        hasNeededServices = checkLocationAndInternet();
        splashFragment.notifyServiceAvailability(hasNeededServices);
    }

    @Override
    protected void onStop() {
        //Stop location service
        locationUtil.stopLocationService();
        eventBus.unregister(this);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashFragment = SplashFragment.newInstance("", "");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.SplashFragmentContainer, splashFragment).commit();
        }

        locationUtil.setup(this);

        //Set up initial state
        loginDone = false;
        serverDataDownloadDone = false;
        redirectDone = false;
        userLocationKnown = false;

    }

    @Override
    public void onFacebookLoginDone(Session session) {
        this.session = session;
        middlewareService.loadUserData(this, session);
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
            userDto = event.getUserDto();
            loginDone = true;
            if(userDto.getPerson().getPersonAddress() != null) {
                if(userDto.getPerson().getPersonAddress().getLongitude() != null) {
                    userLocationKnown = true;
                }
            }
            if(serverDataDownloadDone) {
                takeUserToNextScreen();
            }
        }
        else {
            Toast.makeText(this, "Could not fetch user details from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
            //Show retry button which will re-trigger the request.
        }
    }


    public void onEventMainThread(GetCategoriesImagesEvent event) {
        if(event.getSuccess()) {
            Log.d("SplashActivity", "GetCategoriesImagesEvent:Success");
            serverDataDownloadDone = true;
            if (loginDone) {
                takeUserToNextScreen();
            }
        }
        else {
            Toast.makeText(this, "Could not fetch all categories images from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
            //Show retry button which will re-trigger the request.
            serverDataDownloadDone = true;
            if (loginDone) {
                takeUserToNextScreen();
            }
        }
    }

    private void takeUserToNextScreen() {
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
                if(userLocationKnown) {
                    i = new Intent(this, SelectAmenityActivity.class);
                }
                else {
                    i = new Intent(this, MarkLocationActivity.class);
                }
                if(debugMode) {
                    i = new Intent(this, LaunchActivity.class);
                }
                startActivity(i);
                finish(); //User cant press back to return to this activity
            }
        }
    }

    private Boolean checkLocationAndInternet() {
        return internetServicesCheckUtil.isServiceAvailable(this) && locationServicesCheckUtil.isServiceAvailable(this);
    }

}
