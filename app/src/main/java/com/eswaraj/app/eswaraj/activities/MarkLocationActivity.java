package com.eswaraj.app.eswaraj.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.GetProfileEvent;
import com.eswaraj.app.eswaraj.events.GooglePlaceDetailsEvent;
import com.eswaraj.app.eswaraj.events.ProfileUpdateEvent;
import com.eswaraj.app.eswaraj.fragments.GoogleMapFragment;
import com.eswaraj.app.eswaraj.fragments.GooglePlacesListFragment;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.GooglePlace;
import com.eswaraj.app.eswaraj.util.GooglePlacesUtil;
import com.eswaraj.app.eswaraj.util.LocationUtil;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.eswaraj.app.eswaraj.widgets.CustomProgressDialog;
import com.github.amlcurran.showcaseview.ShowcaseView;
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
    @Inject
    UserSessionUtil userSession;

    private GoogleMapFragment googleMapFragment;
    private GooglePlacesListFragment googlePlacesListFragment;
    private Boolean markerUpdatedOnce;
    private Boolean mapReady;
    private Button mlSaveLocation;
    private Button mlSkip;
    private EditText mlSearchText;
    private Button mlSearchButton;
    private CustomProgressDialog pDialog;
    private Boolean mapDisplayed;
    private GooglePlace googlePlace;
    private Boolean dialogMode;
    private Boolean showAlways;

    private Boolean gotLocation = false;
    private Boolean gotProfileUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_location);

        dialogMode = getIntent().getBooleanExtra("MODE", false);
        showAlways = getIntent().getBooleanExtra("ALWAYS", false);

        googleMapFragment = new GoogleMapFragment();
        googlePlacesListFragment = new GooglePlacesListFragment();

        eventBus.registerSticky(this);
        middlewareService.loadProfileUpdates(this, userSession.getToken());

        //Add all fragments
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.mlContainer, googleMapFragment).commit();
            mapDisplayed = true;
        }

        //Initialization
        googleMapFragment.setContext(this);
        mlSaveLocation = (Button) findViewById(R.id.mlSaveLocation);
        mlSkip = (Button) findViewById(R.id.mlSkip);
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
                middlewareService.updateProfile(view.getContext(), userSession.getToken(), userSession.getUser().getPerson().getName(), lat, lng);
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
        mlSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogMode) {
                    finish();
                }
                else {
                    userSession.setUserSkipMarkLocation(v.getContext(), true);
                    Intent i = new Intent(v.getContext(), HomeActivity.class);
                    v.getContext().startActivity(i);
                    finish();
                }
            }
        });

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        new ShowcaseView.Builder(this)
                .setTarget(new PointTarget(size.x/2, size.y/2))
                .setContentTitle("Mark your home")
                .setContentText("Touch and hold the marker for a second and then drag to mark your home on the map")
                .setStyle(R.style.CustomShowcaseTheme2)
                .singleShot(42)
                .hideOnTouchOutside()
                .build();
    }

    @Override
    protected void onStop() {
        locationUtil.unsubscribe();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationUtil.subscribe(applicationContext, true);

        pDialog = new CustomProgressDialog(this, false, true, "Getting your location...");
        pDialog.show();
    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
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
            gotLocation = true;
            if(gotProfileUpdate) {
                pDialog.dismiss();
            }
            googleMapFragment.updateMarkerLocation(location.getLatitude(), location.getLongitude());
            markerUpdatedOnce = true;
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

    public void onEventMainThread(ProfileUpdateEvent event) {
        if(event.getSuccess()) {
            userSession.setUser(event.getUserDto());
            if (dialogMode) {
                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, null);
                } else {
                    getParent().setResult(Activity.RESULT_OK, null);
                }
                finish();
            }
            else {
                Intent i = new Intent(this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        }
        else {
            Toast.makeText(this, "Could not save location to server. Please retry. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

    public void onEventMainThread(GetProfileEvent event) {
        if(event.getSuccess()) {
            userSession.setUser(event.getUserDto());
            if(userSession.isUserLocationKnown()) {
                if(dialogMode) {
                    if(!showAlways) {
                        if (getParent() == null) {
                            setResult(Activity.RESULT_OK, null);
                        } else {
                            getParent().setResult(Activity.RESULT_OK, null);
                        }
                        finish();
                    }
                }
                else {
                    Intent i = new Intent(this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }
        else {
            Toast.makeText(this, "Could not get profile updates from server" + event.getError(), Toast.LENGTH_LONG).show();
        }
        gotProfileUpdate = true;
        if(gotLocation) {
            pDialog.dismiss();
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
