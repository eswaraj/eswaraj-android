package com.eswaraj.app.eswaraj.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.RevGeocodeEvent;
import com.eswaraj.app.eswaraj.fragments.GoogleMapFragment;
import com.eswaraj.app.eswaraj.helpers.ReverseGeocodingTask;
import com.eswaraj.app.eswaraj.util.GenericUtil;
import com.eswaraj.app.eswaraj.util.LocationUtil;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class HomeActivity extends BaseActivity implements OnMapReadyCallback {

    @Inject
    EventBus eventBus;
    @Inject
    LocationUtil locationUtil;
    @Inject
    Context applicationContext;
    @Inject
    UserSessionUtil userSession;

    private GoogleMapFragment googleMapFragment;
    private Boolean mapReady = false;
    private Location lastLocation;
    private ReverseGeocodingTask reverseGeocodingTask;
    
    private ImageView complaints;
    private ImageView leaders;
    private ImageView constituency;
    private ImageView profile;
    private TextView hRevGeocode;
    private Button hCreate;

    private Boolean retryRevGeocoding = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        complaints = (ImageView) findViewById(R.id.hComplaints);
        leaders = (ImageView) findViewById(R.id.hLeaders);
        constituency = (ImageView) findViewById(R.id.hConstituency);
        profile = (ImageView) findViewById(R.id.hProfile);
        hRevGeocode = (TextView) findViewById(R.id.hRevGeocode);
        hCreate = (Button) findViewById(R.id.hCreate);

        complaints.setImageDrawable(getResources().getDrawable(R.drawable.complaint));
        leaders.setImageDrawable(getResources().getDrawable(R.drawable.leader));
        constituency.setImageDrawable(getResources().getDrawable(R.drawable.constituency));
        profile.setImageDrawable(getResources().getDrawable(R.drawable.profile));

        googleMapFragment = (GoogleMapFragment) getSupportFragmentManager().findFragmentById(R.id.hMap);
        googleMapFragment.setContext(this);

        hCreate.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), SelectAmenityActivity.class);
                startActivity(i);
            }
        });

        complaints.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userSession.isUserLoggedIn(v.getContext())) {
                    Intent i = new Intent(v.getContext(), MyComplaintsActivity.class);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(v.getContext(), LoginDialogActivity.class);
                    startActivity(i);
                }
            }
        });
        //TODO:Fix the activity targets
        leaders.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userSession.isUserLoggedIn(v.getContext())) {
                    Intent i = new Intent(v.getContext(), MyComplaintsActivity.class);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(v.getContext(), LoginDialogActivity.class);
                    startActivity(i);
                }
            }
        });
        constituency.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userSession.isUserLoggedIn(v.getContext())) {
                    Intent i = new Intent(v.getContext(), MyComplaintsActivity.class);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(v.getContext(), LoginDialogActivity.class);
                    startActivity(i);
                }
            }
        });
        profile.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userSession.isUserLoggedIn(v.getContext())) {
                    Intent i = new Intent(v.getContext(), MyComplaintsActivity.class);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(v.getContext(), LoginDialogActivity.class);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.register(this);
        locationUtil.subscribe(applicationContext, true);
        mapReady = false;
    }

    @Override
    protected void onStop() {
        locationUtil.unsubscribe();
        eventBus.unregister(this);
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
    }

    public void onEventMainThread(Location location) {
        Double distance;
        Boolean doRevGeoCoding;

        if (mapReady) {
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
            hRevGeocode.setText(event.getRevGeocodedLocation());
            hRevGeocode.setTextColor(Color.parseColor("#929292"));
            retryRevGeocoding = false;
        }
        else {
            retryRevGeocoding = true;
        }
    }
}
