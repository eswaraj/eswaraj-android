package com.eswaraj.app.eswaraj.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationUtil implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

    Context context;
    LocationRequest locationRequest;
    LocationClient locationClient;

    public LocationUtil() {
        this.context = null;
    }

    public LocationUtil(Context context) {
        this.context = context;
        locationClient = new LocationClient(this.context, this, this);
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
        locationClient.connect();
    }

    public void stopLocationService() {
        if (locationClient.isConnected()) {
            locationClient.removeLocationUpdates(this);
        }
        locationClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("LocationService", "Connected");
        locationClient.requestLocationUpdates(locationRequest, this);
    }

    @Override
    public void onDisconnected() {
        Log.d("LocationService", "Disconnected");
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("LocationService", "Connection Failed");
    }
}
