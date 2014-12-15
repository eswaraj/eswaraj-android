package com.eswaraj.app.eswaraj.activities;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.GetUserEvent;
import com.eswaraj.app.eswaraj.fragments.GoogleMapFragment;
import com.eswaraj.app.eswaraj.middleware.MiddlewareService;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.util.LocationUtil;
import com.eswaraj.web.dto.UserDto;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class MarkLocationActivity extends BaseActivity implements OnMapReadyCallback {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    LocationUtil locationUtil;

    private GoogleMapFragment googleMapFragment;
    private UserDto userDto;
    private Boolean markerUpdatedOnce;
    private Boolean mapReady;
    private Button mlSaveLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_location);
        googleMapFragment = new GoogleMapFragment();

        //Add all fragments
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.mlContainer, googleMapFragment).commit();
        }

        //Initialization
        locationUtil.setup(this);
        googleMapFragment.setContext(this);
        mlSaveLocation = (Button) findViewById(R.id.mlSaveLocation);
        markerUpdatedOnce = false;
        mapReady = false;

        //Event listener
        mlSaveLocation.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                double lat = googleMapFragment.getMarkerLatitude();
                double lng = googleMapFragment.getMarkerLongitude();
                Log.d("MarkLocationActivity", "Picked User location: " + lat + " " + lng);
                middlewareService.saveUserLocation(view.getContext(), userDto, lat, lng);
                Intent i = new Intent(view.getContext(), SelectAmenityActivity.class);
                view.getContext().startActivity(i);
            }
        });
    }

    @Override
    protected void onStop() {
        locationUtil.stopLocationService();
        eventBus.unregister(this);
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.registerSticky(this);
        locationUtil.startLocationService();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        googleMapFragment.setZoomLevel(12);
        googleMapFragment.makeMarkerDraggable();
    }

    public void onEventMainThread(Location location) {
        if(mapReady && !markerUpdatedOnce) {
            googleMapFragment.updateMarkerLocation(location.getLatitude(), location.getLongitude());
            markerUpdatedOnce = true;
        }
    }

    public void onEventMainThread(GetUserEvent event) {
        if(event.getSuccess()) {
            this.userDto = event.getUserDto();
        }
        else {
            Toast.makeText(this, "Could not fetch user details from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
            //Show retry button which will re-trigger the request.
        }
    }
}
