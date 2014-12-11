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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapFragment extends BaseFragment {

    private SupportMapFragment supportMapFragment;
    private GoogleMap googleMap;
    private Marker marker;
    private MarkerOptions markerOptions;
    private int zoomLevel = 14;

    public GoogleMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_google_map, container, false);
        supportMapFragment = SupportMapFragment.newInstance();
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.map, supportMapFragment).commit();

        //Add marker to map
        markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(0, 0));
        markerOptions.visible(false);
        markerOptions.draggable(false);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public void updateMarkerLocation(double lat, double lng) {
        LatLng location = new LatLng(lat, lng);

        if(googleMap == null) {
            if(supportMapFragment != null) {
                googleMap = supportMapFragment.getMap();
                if (googleMap == null) {
                    return;
                } else {
                    markerOptions.position(location);
                    marker = googleMap.addMarker(markerOptions);
                }
            }
            else {
                return;
            }
        }


        CameraUpdate zoom = CameraUpdateFactory.zoomTo(this.zoomLevel);
        CameraUpdate center = CameraUpdateFactory.newLatLng(location);
        marker.setPosition(location);
        googleMap.moveCamera(center);
        googleMap.animateCamera(zoom);
        marker.setVisible(true);
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
