package com.eswaraj.app.eswaraj.util;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.eswaraj.app.eswaraj.base.BaseClass;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class LocationUtil extends BaseClass implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;

    private Location lastKnownLocation;
    private Long lastLocationServiceStartTime = 0L;
    private Long lastLocationServiceStopTime = 0L;

    @Inject
    EventBus eventBus;

    public LocationUtil() {

    }

    public void setup(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        // Create the LocationRequest object
        locationRequest = LocationRequest.create();
        // Use high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 3 seconds
        locationRequest.setInterval(3000);
        // Set the fastest update interval to 1 second
        locationRequest.setFastestInterval(1000);
    }

    public void startLocationService() {
        lastLocationServiceStartTime = System.currentTimeMillis();
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    public void stopLocationService() {
        lastLocationServiceStopTime = System.currentTimeMillis();
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
        googleApiClient.disconnect();
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
        this.eventBus.postSticky(location);
        stopLocationListenerIfNeeded();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("LocationService", "Connection Failed");
    }
    public Location getLastKnownLocation() {
        startLocationListenerIfNeeded();
        return lastKnownLocation;
    }

    public void setLastKnownLocation(Location lastKnownLocation) {
        this.lastKnownLocation = lastKnownLocation;
    }

    /**
     * Automatically Start Location Services if needed
     */
    private void startLocationListenerIfNeeded(){
        if(lastKnownLocation == null && googleApiClient.isConnected()){
            //if we dont have last Known Location and we are not connected yet
            startLocationService();
        }
        if(System.currentTimeMillis() - lastLocationServiceStopTime > 60000){
            //If location is not tracked since last one minute Start updates again
            startLocationService();
        }
    }

    /**
     * Automatically Stop Location Services if needed
     */
    private void stopLocationListenerIfNeeded(){
        if(lastKnownLocation != null && googleApiClient.isConnected() && (System.currentTimeMillis() - lastLocationServiceStartTime) > 60000){
            //if we dont have last Known Location and we are not connected yet
            stopLocationService();
        }
    }

}
