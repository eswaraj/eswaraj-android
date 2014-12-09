package com.eswaraj.app.eswaraj.helpers;

import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapHelper {

    private FragmentActivity context;
    private SupportMapFragment supportMapFragment;
    private GoogleMap googleMap;
    private Marker marker;
    private MarkerOptions markerOptions;
    private int mapContainer;
    private int zoomLevel;

    public GoogleMapHelper() {
        this.context = null;
        this.googleMap = null;
        this.marker = null;
        this.mapContainer = 0;
        this.zoomLevel = 14;
    }

    public GoogleMapHelper(FragmentActivity context, int mapContainer) {
        this.context = context;
        this.mapContainer = mapContainer;
    }

    public void addMapAndMarker() {
        supportMapFragment = SupportMapFragment.newInstance();
        this.context.getSupportFragmentManager().beginTransaction().add(mapContainer, supportMapFragment).commit();
        addMarker();
    }

    private void addMarker() {
        googleMap = supportMapFragment.getMap();
        if(googleMap != null) {
            markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(0, 0));
            markerOptions.visible(false);
            markerOptions.draggable(false);
            marker = googleMap.addMarker(markerOptions);
        }
        else {
            try {
                Thread.sleep(500);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            addMarker();
        }
    }

    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public void updateMarkerLocation(double lat, double lng) {
        LatLng location = new LatLng(lat, lng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(this.zoomLevel);
        CameraUpdate center = CameraUpdateFactory.newLatLng(location);
        this.marker.setVisible(true);
        this.marker.setPosition(location);
        this.googleMap.moveCamera(center);
        this.googleMap.animateCamera(zoom);
    }

    public void makeMarkerDraggable() {
        this.marker.setDraggable(true);
    }

    public double getMarkerLatitude() {
        return this.marker.getPosition().latitude;
    }

    public double getMarkerLongitude() {
        return this.marker.getPosition().longitude;
    }
}
