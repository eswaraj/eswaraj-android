package com.next.eswaraj.fragments;


import android.content.Context;
import android.os.Bundle;


import com.next.eswaraj.application.EswarajApplication;
import com.next.eswaraj.events.MarkerClickEvent;
import com.next.eswaraj.models.ComplaintDto;
import com.next.eswaraj.models.GoogleMapCluster;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class GoogleMapFragment extends SupportMapFragment implements OnMapReadyCallback {

    @Inject
    EventBus eventBus;

    private OnMapReadyCallback callback;
    private GoogleMap.OnCameraChangeListener cameraChangeListener;
    private GoogleMap googleMap;
    private Marker marker;
    private int zoomLevel;
    private MarkerOptions markerOptions;
    private Boolean draggable;
    private Map<String, ComplaintDto> markerMap = new HashMap<>();
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    private Boolean markersDisplayed = false;
    private Boolean heatmapDisplayed = false;
    private Boolean clusterDisplayed = false;

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

    public void addCameraChangeListener(GoogleMap.OnCameraChangeListener context) {
        cameraChangeListener = context;
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mapView = super.onCreateView(inflater, container, savedInstanceState);

        // Get the button view
        View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
        // and next place it, for example, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);

        return mapView;
    }
    */

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        if(callback != null) {
            callback.onMapReady(googleMap);
        }
        if(cameraChangeListener != null) {
            googleMap.setOnCameraChangeListener(cameraChangeListener);
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

    public Boolean centreMapAt(double lat, double lng) {
        if(googleMap != null) {
            LatLng location = new LatLng(lat, lng);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(location)
                    .zoom(zoomLevel)
                    .bearing(0)
                    .tilt(45)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            return true;
        }
        return false;
    }

    public void showMyLocationButton() {
        if(googleMap != null) {
            UiSettings uiSettings = googleMap.getUiSettings();
            googleMap.setMyLocationEnabled(true);
            uiSettings.setMyLocationButtonEnabled(true);
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

    public double getMarkedLatitude() {
        return googleMap.getCameraPosition().target.latitude;
    }

    public double getMarkedLongitude() {
        return googleMap.getCameraPosition().target.longitude;
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
        clearMap();
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
        else if(complaintDtos.size() > 0) {
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

        markersDisplayed = true;
        heatmapDisplayed = false;
        clusterDisplayed = false;
    }

    public void removeMarkers() {
        googleMap.clear();
        markerMap.clear();
    }

    public void addHeatMap(List<ComplaintDto> complaintDtos) {
        clearMap();
        List<LatLng> list = new ArrayList<LatLng>();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLng latLng = null;

        for(ComplaintDto complaintDto : complaintDtos) {
            latLng = new LatLng(complaintDto.getLattitude(), complaintDto.getLongitude());
            list.add(latLng);
            builder.include(latLng);
        }

        if(list.size() < 1) {
            return;
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
        else if(complaintDtos.size() > 0) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(zoomLevel)
                    .bearing(0)
                    .tilt(45)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        markersDisplayed = false;
        heatmapDisplayed = true;
        clusterDisplayed = false;
    }

    public void removeHeatMap() {
        mOverlay.remove();
    }

    public void addCluster(List<ComplaintDto> complaintDtos) {
        clearMap();
        GoogleMapCluster googleMapCluster;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        ClusterManager<GoogleMapCluster> mClusterManager;
        mClusterManager = new ClusterManager<GoogleMapCluster>(getActivity(), googleMap);
        googleMap.setOnCameraChangeListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
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
        else if(complaintDtos.size() > 0) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(complaintDtos.get(0).getLattitude(), complaintDtos.get(0).getLongitude()))
                    .zoom(zoomLevel)
                    .bearing(0)
                    .tilt(45)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        markersDisplayed = false;
        heatmapDisplayed = false;
        clusterDisplayed = true;
    }

    public void removeCluster() {
        googleMap.clear();
        googleMap.setOnCameraChangeListener(null);
        googleMap.setOnMarkerClickListener(null);
    }

    public void clearMap() {
        if(markersDisplayed) {
            removeMarkers();
        }
        else if(clusterDisplayed) {
            removeCluster();
        }
        else if(heatmapDisplayed) {
            removeHeatMap();
        }
    }

}
