package com.next.eswaraj.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseFragment;
import com.next.eswaraj.helpers.GoogleAnalyticsTracker;
import com.next.eswaraj.models.ComplaintDto;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.List;

import javax.inject.Inject;


public class ComplaintsMapFragment extends BaseFragment implements OnMapReadyCallback {

    @Inject
    GoogleAnalyticsTracker googleAnalyticsTracker;

    private Button heatmap;
    private Button cluster;
    private Button markers;
    private LinearLayout mapButtons;

    private Boolean mapDisplayed = false;
    private Boolean mapReady = false;

    private Boolean heatmapMode = true;
    private Boolean clusterMode = false;
    private Boolean markersMode = false;

    private GoogleMapFragment googleMapFragment;
    private List<ComplaintDto> complaintDtoList;

    public ComplaintsMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleMapFragment = new GoogleMapFragment();
        googleMapFragment.setContext(this);
        if(savedInstanceState == null) {
            getChildFragmentManager().beginTransaction().add(R.id.cMap, googleMapFragment).commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_constituency_map, container, false);
        heatmap = (Button) rootView.findViewById(R.id.heatmap);
        cluster = (Button) rootView.findViewById(R.id.cluster);
        markers = (Button) rootView.findViewById(R.id.marker);
        mapButtons = (LinearLayout) rootView.findViewById(R.id.cMapButtons);

        heatmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Constituency: Show Heatmap");
                heatmapMode = true;
                clusterMode = false;
                markersMode = false;
                addDataToMap();
            }
        });
        cluster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Constituency: Show Cluster");
                heatmapMode = false;
                clusterMode = true;
                markersMode = false;
                addDataToMap();
            }
        });
        markers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Constituency: Show Markers");
                heatmapMode = false;
                clusterMode = false;
                markersMode = true;
                addDataToMap();
            }
        });

        addDataToMap();
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        addDataToMap();
    }

    public Boolean setComplaintsData(List<ComplaintDto> complaintDtoList) {
        this.complaintDtoList = complaintDtoList;
        return addDataToMap();
    }

    private Boolean addDataToMap() {
        if(complaintDtoList == null || !mapReady || !mapDisplayed) {
            return false;
        }

        if(heatmapMode) {
            googleMapFragment.addHeatMap(complaintDtoList);
        }
        else if(clusterMode) {
            googleMapFragment.addCluster(complaintDtoList);
        }
        else {
            googleMapFragment.addMarkers(complaintDtoList);
        }
        return true;
    }

    public void setMapDisplayed(Boolean displayed) {
        mapDisplayed = displayed;
    }

    public void dontShowModeSelecter() {
        mapButtons.setVisibility(View.GONE);
    }
}
