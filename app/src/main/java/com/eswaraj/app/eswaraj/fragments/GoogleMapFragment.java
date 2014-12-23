package com.eswaraj.app.eswaraj.fragments;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapFragment extends SupportMapFragment implements OnMapReadyCallback {

    private OnMapReadyCallback callback;
    private GoogleMap googleMap;
    private Marker marker;
    private int zoomLevel;
    private MarkerOptions markerOptions;
    private Boolean draggable;

    public GoogleMapFragment() {
        zoomLevel = 14;
        draggable = false;
    }

    public void setContext(OnMapReadyCallback context) {
        this.callback = context;
        super.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if(callback != null) {
            callback.onMapReady(googleMap);
        }
    }

    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
        if(googleMap != null) {
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(this.zoomLevel);
            googleMap.animateCamera(zoom);
        }
    }

    public void updateMarkerLocation(double lat, double lng) {
        LatLng location = new LatLng(lat, lng);

        if(marker == null) {
            markerOptions = new MarkerOptions();
            markerOptions.visible(true);
            markerOptions.position(location);
            markerOptions.draggable(draggable);
            marker = googleMap.addMarker(markerOptions);
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location)
                .zoom(zoomLevel)
                .bearing(0)
                .tilt(45)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        marker.setPosition(location);
    }

    public void makeMarkerDraggable() {
        draggable = true;
        if(marker != null) {
            marker.setDraggable(true);
        }
    }

    public double getMarkerLatitude() {
        return marker.getPosition().latitude;
    }

    public double getMarkerLongitude() {
        return marker.getPosition().longitude;
    }

    public void disableGestures() {
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setZoomGesturesEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setScrollGesturesEnabled(false);
        uiSettings.setCompassEnabled(false);
    }
}
