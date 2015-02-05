package com.next.eswaraj.util;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.next.eswaraj.base.BaseClass;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class LocationUtil extends BaseClass implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;

    private Location lastKnownLocation;
    private Long lastLocationServiceStartTime = 0L;
    private Long lastLocationServiceStopTime = 0L;

    private Boolean setupDone = false;
    private Boolean getContinuousUpdates = false;
    private Boolean nextActivityNeedsUpdates = true;

    @Inject
    EventBus eventBus;

    public LocationUtil() {

    }

    public void setup(Context context) {
        if(!setupDone) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(3000);
            locationRequest.setFastestInterval(1000);
            setupDone = true;
        }
    }

    public void startLocationService() {
        lastLocationServiceStartTime = System.currentTimeMillis();
        if(!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    public void stopLocationService() {
        lastLocationServiceStopTime = System.currentTimeMillis();
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("LocationService", "Connected");
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("LocationService", "Suspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        stopLocationListenerIfNeeded();
        setLastKnownLocation(location);
        eventBus.post(location);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("LocationService", "Connection Failed");
    }

    //This is the user entry point to receive location updates
    public void subscribe(Context context, Boolean getContinuousUpdates) {
        this.getContinuousUpdates = getContinuousUpdates;
        setup(context);
        startLocationListenerIfNeeded();
        if(lastKnownLocation != null) {
            eventBus.post(lastKnownLocation);
        }
        nextActivityNeedsUpdates = !nextActivityNeedsUpdates;
    }

    public void unsubscribe() {
        if(!nextActivityNeedsUpdates) {
            stopLocationService();
            nextActivityNeedsUpdates = true;
        }
        else {
            nextActivityNeedsUpdates = false;
        }
    }

    public void setLastKnownLocation(Location lastKnownLocation) {
        this.lastKnownLocation = lastKnownLocation;
    }

    private void startLocationListenerIfNeeded() {
        if((lastKnownLocation == null && !googleApiClient.isConnected()) || getContinuousUpdates || (System.currentTimeMillis() - lastLocationServiceStopTime > 60000)) {
            startLocationService();
        }
    }

    private void stopLocationListenerIfNeeded() {
        if(lastKnownLocation != null && googleApiClient.isConnected() && !getContinuousUpdates && (System.currentTimeMillis() - lastLocationServiceStartTime) > 60000) {
            stopLocationService();
        }
    }
}
