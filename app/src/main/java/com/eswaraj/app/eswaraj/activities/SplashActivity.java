package com.eswaraj.app.eswaraj.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.fragments.SplashFragment;
import com.eswaraj.app.eswaraj.interfaces.DeviceRegisterInterface;
import com.eswaraj.app.eswaraj.interfaces.FacebookLoginInterface;
import com.eswaraj.app.eswaraj.interfaces.LocationInterface;
import com.eswaraj.app.eswaraj.interfaces.LoginSkipInterface;
import com.eswaraj.app.eswaraj.interfaces.ServerDataInterface;

public class SplashActivity extends FragmentActivity implements FacebookLoginInterface, DeviceRegisterInterface, LoginSkipInterface, ServerDataInterface, LocationInterface{

    private SplashFragment splashFragment;

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
    public void onLoginDone() {
        splashFragment.onLoginDone();
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
    public void onServerDataAvailable() {
        splashFragment.onServerDataAvailable();
    }

    @Override
    public void onLocationChanged() {
        splashFragment.onLocationChanged();
    }
}
