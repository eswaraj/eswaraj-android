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

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class LocationUtil extends BaseClass implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;

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
        // Set the update interval to 30 seconds
        locationRequest.setInterval(30000);
        // Set the fastest update interval to 10 second
        locationRequest.setFastestInterval(10000);
    }

    public void startLocationService() {
        googleApiClient.connect();
    }

    public void stopLocationService() {
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
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("LocationService", "Connection Failed");
    }
}
