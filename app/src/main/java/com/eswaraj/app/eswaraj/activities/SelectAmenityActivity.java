package com.eswaraj.app.eswaraj.activities;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.RevGeocodeEvent;
import com.eswaraj.app.eswaraj.fragments.AmenitiesFragment;
import com.eswaraj.app.eswaraj.fragments.BottomMenuBarFragment;
import com.eswaraj.app.eswaraj.fragments.GoogleMapFragment;
import com.eswaraj.app.eswaraj.helpers.ReverseGeocodingTask;
import com.eswaraj.app.eswaraj.util.LocationUtil;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class SelectAmenityActivity extends BaseActivity {

    //@Inject
    LocationUtil locationUtil;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    EventBus eventBus;

    private BottomMenuBarFragment bottomMenuBarFragment;
    private AmenitiesFragment amenitiesFragment;
    private GoogleMapFragment googleMapFragment;
    private Location lastLocation;
    private ReverseGeocodingTask reverseGeocodingTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_amenity);
        lastLocation = null;
        locationUtil = new LocationUtil(this);
        bottomMenuBarFragment = BottomMenuBarFragment.newInstance();
        amenitiesFragment = AmenitiesFragment.newInstance();
        googleMapFragment = new GoogleMapFragment();

        //Add all fragments
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.asAmenities, amenitiesFragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.asMenubar, bottomMenuBarFragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.asMapContainer, googleMapFragment).commit();
        }
    }

    @Override
    protected void onStart() {
        eventBus.registerSticky(this);
        super.onStart();
        locationUtil.startLocationService();
    }

    @Override
    protected void onStop() {
        locationUtil.stopLocationService();
        eventBus.unregister(this);
        super.onStop();
    }

    public void onEventMainThread(Location location) {
        googleMapFragment.updateMarkerLocation(location.getLatitude(), location.getLongitude());
        if(lastLocation != null) {
            //If difference between location and lastLocation is greater than 100m then
            //TODO:Add condition
            //1. Update lastLocation
            lastLocation = location;
            //2. Start rev geocoding task
            if(reverseGeocodingTask != null) {
                if(reverseGeocodingTask.getStatus() != AsyncTask.Status.FINISHED) {
                    reverseGeocodingTask.cancel(true);
                }
            }
            reverseGeocodingTask = new ReverseGeocodingTask(this, location);
            reverseGeocodingTask.execute();
        }
        else {
            lastLocation = location;
            if(reverseGeocodingTask != null) {
                if(reverseGeocodingTask.getStatus() != AsyncTask.Status.FINISHED) {
                    reverseGeocodingTask.cancel(true);
                }
            }
            reverseGeocodingTask = new ReverseGeocodingTask(this, location);
            reverseGeocodingTask.execute();
        }
    }

    public void onEventMainThread(RevGeocodeEvent event) {
        TextView asRevGeocode = (TextView) findViewById(R.id.asRevGeocode);
        if(event.getSuccess()) {
            asRevGeocode.setText(event.getRevGeocodedLocation());
        }
    }
}
