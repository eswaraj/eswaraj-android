package com.eswaraj.app.eswaraj.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.fragments.SplashFragment;
import com.eswaraj.app.eswaraj.interfaces.FacebookLoginInterface;
import com.eswaraj.app.eswaraj.interfaces.LoginSkipInterface;
import com.eswaraj.app.eswaraj.location.LocationUtil;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class SplashActivity extends FragmentActivity implements FacebookLoginInterface, LoginSkipInterface{

    private SplashFragment splashFragment;
    @Inject
    LocationUtil locationUtil;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    private EventBus eventBus;

    @Override
    protected void onStart() {
        super.onStart();
        //Start location service
        locationUtil.startLocationService();
        //Start data download from server, if needed
        middlewareService.loadCategoriesData(this);
    }

    @Override
    protected void onStop() {
        //Stop location service
        locationUtil.stopLocationService();
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
    }

    @Override
    public void onFacebookLoginDone() {
        splashFragment.onFacebookLoginDone();
        //TODO: create object of type RegisterFacebookAccountRequest and pass it instead of null
        middlewareService.registerFacebookUser(this, null);
    }

    @Override
    public void onSkipDone() {
        splashFragment.onSkipDone();
    }

}
