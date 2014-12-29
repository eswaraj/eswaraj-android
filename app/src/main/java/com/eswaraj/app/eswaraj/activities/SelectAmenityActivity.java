package com.eswaraj.app.eswaraj.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.AmenitySelectEvent;
import com.eswaraj.app.eswaraj.events.RevGeocodeEvent;
import com.eswaraj.app.eswaraj.fragments.AmenitiesFragment;
import com.eswaraj.app.eswaraj.fragments.BottomMenuBarFragment;
import com.eswaraj.app.eswaraj.fragments.GoogleMapFragment;
import com.eswaraj.app.eswaraj.helpers.ReverseGeocodingTask;
import com.eswaraj.app.eswaraj.helpers.WindowAnimationHelper;
import com.eswaraj.app.eswaraj.util.GenericUtil;
import com.eswaraj.app.eswaraj.util.LocationUtil;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.Serializable;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class SelectAmenityActivity extends BaseActivity implements OnMapReadyCallback {

    @Inject
    LocationUtil locationUtil;
    @Inject
    EventBus eventBus;
    @Inject
    Context applicationContext;
    @Inject
    UserSessionUtil userSession;

    private AmenitiesFragment amenitiesFragment;
    private GoogleMapFragment googleMapFragment;
    private Location lastLocation;
    private ReverseGeocodingTask reverseGeocodingTask;
    private Boolean mapReady;
    private Boolean retryRevGeocoding = false;
    TextView asRevGeocode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_amenity);
        lastLocation = null;
        mapReady = false;
        amenitiesFragment = AmenitiesFragment.newInstance();
        googleMapFragment = new GoogleMapFragment();

        //Add all fragments
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.asAmenities, amenitiesFragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.asMapContainer, googleMapFragment).commit();
        }

        asRevGeocode = (TextView) findViewById(R.id.asRevGeocode);

        //Do setup
        googleMapFragment.setContext(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.registerSticky(this);
        locationUtil.subscribe(applicationContext, true);
    }

    @Override
    protected void onStop() {
        locationUtil.unsubscribe();
        eventBus.unregister(this);
        super.onStop();
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
                    reverseGeocodingTask = new ReverseGeocodingTask(this, location);
                    reverseGeocodingTask.execute();
                }
            }
            else {
                reverseGeocodingTask = new ReverseGeocodingTask(this, location);
                reverseGeocodingTask.execute();
            }
        }
    }

    public void onEventMainThread(RevGeocodeEvent event) {
        if(event.getSuccess()) {
            asRevGeocode.setText(event.getRevGeocodedLocation());
            asRevGeocode.setTextColor(Color.parseColor("#929292"));
            userSession.setUserRevGeocodedLocation(event.getRevGeocodedFullData());
            retryRevGeocoding = false;
        }
        else {
            retryRevGeocoding = true;
        }
    }

    public void onEventMainThread(AmenitySelectEvent event) {
        if(event.getSuccess()) {
            Intent i = new Intent(this, SelectTemplateActivity.class);
            i.putExtra("AMENITY", (Serializable) event.getAmenity());
            startActivity(i);
        }
        else {
            //This will never happen
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapFragment.disableGestures();
        mapReady = true;
    }
}
