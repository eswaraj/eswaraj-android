package com.eswaraj.app.eswaraj.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.GetUserEvent;
import com.eswaraj.app.eswaraj.events.GooglePlaceDetailsEvent;
import com.eswaraj.app.eswaraj.fragments.GoogleMapFragment;
import com.eswaraj.app.eswaraj.fragments.GooglePlacesListFragment;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.GooglePlace;
import com.eswaraj.app.eswaraj.util.GooglePlacesUtil;
import com.eswaraj.app.eswaraj.util.LocationUtil;
import com.eswaraj.app.eswaraj.widgets.CustomProgressDialog;
import com.eswaraj.web.dto.UserDto;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.PointTarget;
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
    @Inject
    GooglePlacesUtil googlePlacesUtil;
    @Inject
    Context applicationContext;

    private GoogleMapFragment googleMapFragment;
    private GooglePlacesListFragment googlePlacesListFragment;
    private UserDto userDto;
    private Boolean markerUpdatedOnce;
    private Boolean mapReady;
    private Button mlSaveLocation;
    private EditText mlSearchText;
    private Button mlSearchButton;
    private CustomProgressDialog pDialog;
    private Boolean mapDisplayed;
    private GooglePlace googlePlace;
    private ShowcaseView showcaseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_location);
        googleMapFragment = new GoogleMapFragment();
        googlePlacesListFragment = new GooglePlacesListFragment();

        //Add all fragments
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.mlContainer, googleMapFragment).commit();
            mapDisplayed = true;
        }

        //Initialization
        googleMapFragment.setContext(this);
        mlSaveLocation = (Button) findViewById(R.id.mlSaveLocation);
        mlSearchText = (EditText) findViewById(R.id.mlSearchText);
        mlSearchButton = (Button) findViewById(R.id.mlSearchButton);
        markerUpdatedOnce = false;
        mapReady = false;

        //Event listener
        mlSaveLocation.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                double lat = googleMapFragment.getMarkerLatitude();
                double lng = googleMapFragment.getMarkerLongitude();
                middlewareService.saveUserLocation(view.getContext(), userDto, lat, lng);
                Intent i = new Intent(view.getContext(), SelectAmenityActivity.class);
                view.getContext().startActivity(i);
                finish();
            }
        });

        mlSearchButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapDisplayed) {
                    mapDisplayed = false;
                    mapReady = false;

                    getSupportFragmentManager().beginTransaction().remove(googleMapFragment).commit();
                    getSupportFragmentManager().beginTransaction().add(R.id.mlContainer, googlePlacesListFragment).commit();
                }
                googlePlacesUtil.getPlacesList(mlSearchText.getText().toString());
                hideKeyboard();
            }
        });
        mlSearchText.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapDisplayed) {
                    mapDisplayed = false;
                    mapReady = false;

                    getSupportFragmentManager().beginTransaction().remove(googleMapFragment).commit();
                    getSupportFragmentManager().beginTransaction().add(R.id.mlContainer, googlePlacesListFragment).commit();
                }
            }
        });

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        new ShowcaseView.Builder(this)
                .setTarget(new PointTarget(size.x/2, size.y/2))
                .setContentTitle("Mark your home")
                .setContentText("Touch and hold the marker for a second and then drag to mark your home on the map")
                .hideOnTouchOutside()
                .build();
    }

    @Override
    protected void onStop() {
        eventBus.unregister(this);
        locationUtil.unsubscribe();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.registerSticky(this);
        locationUtil.subscribe(applicationContext, true);

        pDialog = new CustomProgressDialog(this, false, true, "Getting your locating...");
        pDialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        googleMapFragment.setZoomLevel(13);
        if(googlePlace != null) {
            googleMapFragment.updateMarkerLocation(googlePlace.getLatitude(), googlePlace.getLongitude());
            googlePlace = null;
        }
        googleMapFragment.makeMarkerDraggable();
    }

    public void onEventMainThread(Location location) {
        if(mapReady && !markerUpdatedOnce) {
            pDialog.dismiss();
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

    public void onEventMainThread(GooglePlaceDetailsEvent event) {
        if(!mapDisplayed) {
            mapDisplayed = true;
            googleMapFragment = new GoogleMapFragment();
            googleMapFragment.setContext(this);
            getSupportFragmentManager().beginTransaction().remove(googlePlacesListFragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.mlContainer, googleMapFragment).commit();
        }
        if(event.getSuccess()) {
            googlePlace = event.getGooglePlace();
            markerUpdatedOnce = true;
            if(mapReady) {
                googleMapFragment.updateMarkerLocation(googlePlace.getLatitude(), googlePlace.getLongitude());
                googlePlace = null;
            }
        }
        else {
            Toast.makeText(this, "Could not fetch details of selected place. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
