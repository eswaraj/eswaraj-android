package com.eswaraj.app.eswaraj.fragments;


import android.os.Bundle;
import android.util.Log;

import com.eswaraj.app.eswaraj.application.EswarajApplication;
import com.eswaraj.app.eswaraj.events.MarkerClickEvent;
import com.eswaraj.app.eswaraj.models.ComplaintDto;
import com.eswaraj.web.dto.UserDto;
import com.eswaraj.app.eswaraj.models.GoogleMapCluster;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class GoogleMapFragment extends SupportMapFragment implements OnMapReadyCallback {

    @Inject
    EventBus eventBus;

    private OnMapReadyCallback callback;
    private GoogleMap googleMap;
    private Marker marker;
    private int zoomLevel;
    private MarkerOptions markerOptions;
    private Boolean draggable;
    private Map<String, ComplaintDto> markerMap = new HashMap<>();
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    public GoogleMapFragment() {
        zoomLevel = 14;
        draggable = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((EswarajApplication)getActivity().getApplication()).inject(this);
    }

    public void setContext(OnMapReadyCallback context) {
        this.callback = context;
        super.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        if(callback != null) {
            callback.onMapReady(googleMap);
        }
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
            }
        });
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

    public void addMarkers(List<ComplaintDto> complaintDtos) {
        Marker m;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for(ComplaintDto complaintDto : complaintDtos) {
            markerOptions = new MarkerOptions();
            markerOptions.visible(true);
            markerOptions.position(new LatLng(complaintDto.getLattitude(), complaintDto.getLongitude()));
            markerOptions.draggable(false);
            m = googleMap.addMarker(markerOptions);
            markerMap.put(m.getId(), complaintDto);
            builder.include(m.getPosition());
        }

        if(complaintDtos.size() > 1) {
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
        googleMap.animateCamera(cu);
        }
        else {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(complaintDtos.get(0).getLattitude(), complaintDtos.get(0).getLongitude()))
                    .zoom(zoomLevel)
                    .bearing(0)
                    .tilt(45)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                MarkerClickEvent event = new MarkerClickEvent();
                event.setComplaintDto(markerMap.get(marker.getId()));
                eventBus.post(event);
                return true;
            }
        });
    }

    public void removeMarkers() {
        googleMap.clear();
        markerMap.clear();
    }

    public void addHeatMap(List<ComplaintDto> complaintDtos) {
        List<LatLng> list = null;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLng latLng = null;

        for(ComplaintDto complaintDto : complaintDtos) {
            latLng = new LatLng(complaintDto.getLattitude(), complaintDto.getLongitude());
            list.add(latLng);
            builder.include(latLng);
        }

        mProvider = new HeatmapTileProvider.Builder()
                    .data(list)
                    .build();
        mOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

        if(complaintDtos.size() > 1) {
            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
            googleMap.animateCamera(cu);
        }
        else {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(zoomLevel)
                    .bearing(0)
                    .tilt(45)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void removeHeatMap() {
        mOverlay.remove();
    }

    public void addCluster(List<ComplaintDto> complaintDtos) {
        GoogleMapCluster googleMapCluster;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        ClusterManager<GoogleMapCluster> mClusterManager;
        mClusterManager = new ClusterManager<GoogleMapCluster>(getActivity(), googleMap);
        for(ComplaintDto complaintDto : complaintDtos) {
            googleMapCluster = new GoogleMapCluster(complaintDto.getLattitude(), complaintDto.getLongitude());
            mClusterManager.addItem(googleMapCluster);
            builder.include(googleMapCluster.getPosition());
        }

        if(complaintDtos.size() > 1) {
            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
            googleMap.animateCamera(cu);
        }
        else {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(complaintDtos.get(0).getLattitude(), complaintDtos.get(0).getLongitude()))
                    .zoom(zoomLevel)
                    .bearing(0)
                    .tilt(45)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void removeCluster() {
        googleMap.clear();
    }
}
