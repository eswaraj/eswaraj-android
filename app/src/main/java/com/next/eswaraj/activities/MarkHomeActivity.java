package com.next.eswaraj.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.CameraPosition;
import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.events.ProfileUpdateEvent;
import com.next.eswaraj.events.RevGeocodeEvent;
import com.next.eswaraj.events.UserActionRevGeocodeEvent;
import com.next.eswaraj.fragments.GoogleMapFragment;
import com.next.eswaraj.helpers.GoogleAnalyticsTracker;
import com.next.eswaraj.helpers.ReverseGeocodingTask;
import com.next.eswaraj.middleware.MiddlewareServiceImpl;
import com.next.eswaraj.util.LocationUtil;
import com.next.eswaraj.util.UserSessionUtil;
import com.next.eswaraj.widgets.CustomProgressDialog;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.PointTarget;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class MarkHomeActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener {

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
    GoogleAnalyticsTracker googleAnalyticsTracker;

    private GoogleMapFragment googleMapFragment;
    private Boolean dialogMode;
    private Button mhSaveLocation;
    private Button mhSkip;
    private EditText mhSearchText;
    private CustomProgressDialog pDialogSave;
    private Boolean markerUpdatedOnce = false;
    private int SEARCH_REQUEST = 8888;
    private Double lat, lng;
    private ReverseGeocodingTask reverseGeocodingTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_home);
        setTitle("Mark location");

        dialogMode = getIntent().getBooleanExtra("MODE", false);
        googleMapFragment = new GoogleMapFragment();
        googleMapFragment.setContext(this);
        googleMapFragment.addCameraChangeListener(this);

        eventBus.register(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.mhContainer, googleMapFragment).commit();
        }
        mhSaveLocation = (Button) findViewById(R.id.mhSaveLocation);
        mhSkip = (Button) findViewById(R.id.mhSkip);
        mhSearchText = (EditText) findViewById(R.id.mhSearchText);

        mhSaveLocation.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "MarkHome: Save Location");
                pDialogSave = new CustomProgressDialog(view.getContext(), false, true, "Saving your location...");
                pDialogSave.show();
                middlewareService.updateProfile(view.getContext(), userSession.getToken(), userSession.getUser().getPerson().getName(), userSession.getUser().getPerson().getVoterId(), googleMapFragment.getMarkedLatitude(), googleMapFragment.getMarkedLongitude());
            }
        });

        mhSearchText.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), PlaceSearchActivity.class);
                startActivityForResult(i, SEARCH_REQUEST);
            }
        });

        mhSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "MarkHome: Skip Location");
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
                .setContentTitle("Mark your location of interest")
                .setContentText("Drag to take the marker to correct location and press save")
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
        if(pDialogSave != null && pDialogSave.isShowing()) {
            pDialogSave.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(ProfileUpdateEvent event) {
        pDialogSave.dismiss();
        if(event.getSuccess()) {
            userSession.setUser(event.getUserDto());
            middlewareService.updateLeaders(this, null);
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

    public void onEventMainThread(Location location) {
        if(!markerUpdatedOnce) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            markerUpdatedOnce = googleMapFragment.centreMapAt(location.getLatitude(), location.getLongitude());
            if(markerUpdatedOnce) {
                lat = null;
                lng = null;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SEARCH_REQUEST && resultCode == RESULT_OK) {
            lat = data.getDoubleExtra("LAT", 0);
            lng = data.getDoubleExtra("LNG", 0);
            Boolean moved = googleMapFragment.centreMapAt(lat, lng);
            if(moved) {
                lat = null;
                lng = null;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapFragment.showMyLocationButton();
        if(lat != null) {
            googleMapFragment.centreMapAt(lat, lng);
            lat = null;
            lng = null;
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if(reverseGeocodingTask != null) {
            if(reverseGeocodingTask.getStatus() == AsyncTask.Status.FINISHED) {
                startRevGeocodeTask(cameraPosition);
            }
            else {
                reverseGeocodingTask.cancel(true);
                startRevGeocodeTask(cameraPosition);
            }
        }
        else {
            startRevGeocodeTask(cameraPosition);
        }
    }

    private void startRevGeocodeTask(CameraPosition cameraPosition) {
        Location location = new Location("Map");
        location.setLatitude(cameraPosition.target.latitude);
        location.setLongitude(cameraPosition.target.longitude);
        reverseGeocodingTask = new ReverseGeocodingTask(this, location, true);
        reverseGeocodingTask.execute();
    }

    public void onEventMainThread(UserActionRevGeocodeEvent event) {
        if(event.getSuccess()) {
            mhSearchText.setText(event.getRevGeocodedLocation());
        }
    }
}
