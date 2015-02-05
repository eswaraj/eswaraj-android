package com.next.eswaraj.fragments;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.next.eswaraj.R;
import com.next.eswaraj.adapters.AmenityListAdapter;
import com.next.eswaraj.base.BaseFragment;
import com.next.eswaraj.events.AmenitySelectEvent;
import com.next.eswaraj.events.RevGeocodeEvent;
import com.next.eswaraj.helpers.ReverseGeocodingTask;
import com.next.eswaraj.middleware.MiddlewareServiceImpl;
import com.next.eswaraj.util.GenericUtil;
import com.next.eswaraj.util.GlobalSessionUtil;
import com.next.eswaraj.util.LocationUtil;
import com.next.eswaraj.util.UserSessionUtil;
import com.next.eswaraj.widgets.ProgressTextView;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class SelectAmenityFragment extends BaseFragment implements OnMapReadyCallback {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    LocationUtil locationUtil;
    @Inject
    Context applicationContext;
    @Inject
    UserSessionUtil userSession;
    @Inject
    GlobalSessionUtil globalSession;

    private GridView gvAmenityList;
    private GoogleMapFragment googleMapFragment;
    private Location lastLocation;
    private ReverseGeocodingTask reverseGeocodingTask;
    private Boolean mapReady;
    private Boolean retryRevGeocoding = false;
    private ProgressTextView asRevGeocode;


    public static SelectAmenityFragment newInstance() {
        SelectAmenityFragment fragment = new SelectAmenityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SelectAmenityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastLocation = null;
        mapReady = false;
        googleMapFragment = new GoogleMapFragment();
        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction().add(R.id.asMapContainer, googleMapFragment).commit();
        }
        //Do setup
        googleMapFragment.setContext(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        eventBus.register(this);
        locationUtil.subscribe(applicationContext, true);
    }

    @Override
    public void onStop() {
        locationUtil.unsubscribe();
        eventBus.unregister(this);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_amenities, container, false);
        asRevGeocode = (ProgressTextView) rootView.findViewById(R.id.asRevGeocode);
        gvAmenityList = (GridView) rootView.findViewById(R.id.gvAmenityList);

        asRevGeocode.setTextColor(Color.parseColor("#929292"));

        AmenityListAdapter amenityListAdapter = new AmenityListAdapter(getActivity(), R.layout.item_amenity_list, globalSession.getCategoryDtoList(), null, null);
        gvAmenityList.setAdapter(amenityListAdapter);
        gvAmenityList.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                AmenitySelectEvent event = new AmenitySelectEvent();
                event.setSuccess(true);
                event.setAmenity((CategoryWithChildCategoryDto) gvAmenityList.getAdapter().getItem(pos));
                eventBus.post(event);
            }
        });
        return rootView;
    }

    public void onEventMainThread(Location location) {
        Double distance;
        Boolean doRevGeoCoding;

        if(mapReady) {
            googleMapFragment.updateMarkerLocation(location.getLatitude(), location.getLongitude());
        }

        if(lastLocation != null) {
            distance = GenericUtil.calculateDistance(location.getLatitude(), location.getLongitude(), lastLocation.getLatitude(), lastLocation.getLongitude());
            if (distance > 100) {
                doRevGeoCoding = true;
            }
            else {
                doRevGeoCoding = false;
            }
        }
        else {
            doRevGeoCoding = true;
        }

        if(doRevGeoCoding || retryRevGeocoding) {
            lastLocation = location;
            if(reverseGeocodingTask != null) {
                if(reverseGeocodingTask.getStatus() == AsyncTask.Status.FINISHED) {
                    reverseGeocodingTask = new ReverseGeocodingTask(getActivity(), location);
                    reverseGeocodingTask.execute();
                }
            }
            else {
                reverseGeocodingTask = new ReverseGeocodingTask(getActivity(), location);
                reverseGeocodingTask.execute();
            }
        }
    }

    public void onEventMainThread(RevGeocodeEvent event) {
        if(event.getSuccess()) {
            asRevGeocode.setActualText(event.getRevGeocodedLocation());
            userSession.setUserRevGeocodedLocation(event.getRevGeocodedFullData());
            retryRevGeocoding = false;
        }
        else {
            retryRevGeocoding = true;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapFragment.disableGestures();
        mapReady = true;
    }
}
