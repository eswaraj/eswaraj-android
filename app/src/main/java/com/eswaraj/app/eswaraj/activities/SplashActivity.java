package com.eswaraj.app.eswaraj.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.config.ServerAccessEnums;
import com.eswaraj.app.eswaraj.fragments.SplashFragment;
import com.eswaraj.app.eswaraj.helpers.SharedPreferencesHelper;
import com.eswaraj.app.eswaraj.interfaces.DatastoreInterface;
import com.eswaraj.app.eswaraj.interfaces.DeviceRegisterInterface;
import com.eswaraj.app.eswaraj.interfaces.FacebookLoginInterface;
import com.eswaraj.app.eswaraj.interfaces.LocationInterface;
import com.eswaraj.app.eswaraj.interfaces.LoginSkipInterface;
import com.eswaraj.app.eswaraj.interfaces.ServerDataInterface;
import com.eswaraj.app.eswaraj.location.LocationUtil;
import com.eswaraj.app.eswaraj.util.DeviceUtil;
import com.eswaraj.app.eswaraj.util.ServerDataUtil;

public class SplashActivity extends FragmentActivity implements FacebookLoginInterface, DeviceRegisterInterface, LoginSkipInterface, DatastoreInterface, LocationInterface{

    private SplashFragment splashFragment;
    //Device Register Utility
    DeviceUtil deviceUtil;
    //Location Utility
    LocationUtil locationUtil;
    //ServerData Utility
    ServerDataUtil serverDataUtil;
    //SharedPreferences Helper
    SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onStart() {
        super.onStart();
        //Start location service
        locationUtil.startLocationService();
        //Start data download from server, if needed
        serverDataUtil.getData(ServerAccessEnums.GET_CATEGORIES, false);
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
        deviceUtil = new DeviceUtil(this);
        locationUtil = new LocationUtil(this);
        sharedPreferencesHelper = new SharedPreferencesHelper(this);
        serverDataUtil = new ServerDataUtil(this);


        splashFragment = SplashFragment.newInstance("", "");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.SplashFragmentContainer, splashFragment).commit();
        }
    }

    @Override
    public void onLoginDone() {
        splashFragment.onLoginDone();
        //Register the device now
        deviceUtil.startDeviceRegistration();
    }

    @Override
    public void onDeviceRegistered() {
        splashFragment.onDeviceRegistered();
    }

    @Override
    public void onSkipDone() {
        splashFragment.onSkipDone();
    }

    @Override
    public void onDataAvailable(ServerAccessEnums resource, Bundle bundle) {
        splashFragment.onDataAvailable(resource, bundle);
    }

    @Override
    public void onLocationChanged() {
        splashFragment.onLocationChanged();
    }
}
