package com.eswaraj.app.eswaraj.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapFragment extends BaseFragment implements OnMapReadyCallback {

    private OnMapReadyCallback context;
    private SupportMapFragment supportMapFragment;
    private GoogleMap googleMap;
    private Marker marker;
    private MarkerOptions markerOptions;
    private int zoomLevel = 14;
    private Boolean draggable = false;

    public GoogleMapFragment() {

    }

    public void setContext(OnMapReadyCallback context) {
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_google_map, container, false);
        supportMapFragment = SupportMapFragment.newInstance();
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.map, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
        if(googleMap != null) {
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(this.zoomLevel);
            googleMap.animateCamera(zoom);
        }
    }

    public void updateMarkerLocation(double lat, double lng) {
        if(googleMap == null) {
            return;
        }
        if(marker == null) {
            markerOptions.position(new LatLng(lat, lng));
            marker = googleMap.addMarker(markerOptions);
            marker.setDraggable(draggable);
        }

        LatLng location = new LatLng(lat, lng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(this.zoomLevel);
        CameraUpdate center = CameraUpdateFactory.newLatLng(location);
        marker.setPosition(location);
        googleMap.moveCamera(center);
        googleMap.animateCamera(zoom);
        marker.setVisible(true);
    }

    public void makeMarkerDraggable() {
        draggable = true;
        if(marker != null) {
            marker.setDraggable(true);
        }
    }

    public double getMarkerLatitude() {
        return this.marker.getPosition().latitude;
    }

    public double getMarkerLongitude() {
        return this.marker.getPosition().longitude;
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        //Add marker to map
        markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(0, 0));
        markerOptions.visible(false);
        markerOptions.draggable(false);


        //Call callback for parent activity
        if(context != null) {
            context.onMapReady(googleMap);
        }
    }
}
