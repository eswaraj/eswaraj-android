package com.next.eswaraj.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.events.RevGeocodeEvent;
import com.next.eswaraj.fragments.GoogleMapFragment;
import com.next.eswaraj.helpers.GoogleAnalyticsTracker;
import com.next.eswaraj.helpers.ReverseGeocodingTask;
import com.next.eswaraj.util.GenericUtil;
import com.next.eswaraj.util.InternetServicesCheckUtil;
import com.next.eswaraj.util.LocationServicesCheckUtil;
import com.next.eswaraj.util.LocationUtil;
import com.next.eswaraj.util.UserSessionUtil;
import com.next.eswaraj.widgets.ProgressTextView;
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
    @Inject
    InternetServicesCheckUtil internetServicesCheckUtil;
    @Inject
    LocationServicesCheckUtil locationServicesCheckUtil;
    @Inject
    GoogleAnalyticsTracker googleAnalyticsTracker;

    private GoogleMapFragment googleMapFragment;
    private Boolean mapReady = false;
    private Location lastLocation;
    private ReverseGeocodingTask reverseGeocodingTask;

    private ImageView complaints;
    private ImageView leaders;
    private ImageView constituency;
    private ImageView profile;
    private ProgressTextView hRevGeocode;
    private ImageView hCreate;

    private Boolean retryRevGeocoding = false;

    private final int REQUEST_MY_COMPLAINTS = 0;
    private final int REQUEST_MY_CONSTITUENCY = 1;
    private final int REQUEST_MY_LEADERS = 2;
    private final int REQUEST_MY_PROFILE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle(getResources().getString(R.string.titleDocumentActivity));
        eventBus.register(this);

        complaints = (ImageView) findViewById(R.id.hComplaints);
        leaders = (ImageView) findViewById(R.id.hLeaders);
        constituency = (ImageView) findViewById(R.id.hConstituency);
        profile = (ImageView) findViewById(R.id.hProfile);
        hRevGeocode = (ProgressTextView) findViewById(R.id.hRevGeocode);
        hCreate = (ImageView) findViewById(R.id.hCreate);

        hRevGeocode.setTextColor(Color.parseColor("#929292"));

        googleMapFragment = (GoogleMapFragment) getSupportFragmentManager().findFragmentById(R.id.hMap);
        googleMapFragment.setContext(this);

        hCreate.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, getResources().getString(R.string.createComplaintLabel));
                if(locationServicesCheckUtil.isServiceAvailable(v.getContext())) {
                    Intent i = new Intent(v.getContext(), SelectAmenityActivity.class);
                    startActivity(i);
                }
                else {
                    googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.NO_SERVICE, getResources().getString(R.string.createComplaintNoLocation));
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), AlertDialog.THEME_HOLO_LIGHT);
                    builder.setMessage(getResources().getString(R.string.needLocationServiceLabel))
                            .setCancelable(false)
                            .setTitle(getResources().getString(R.string.locationServiceNeeded))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton(getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(settingsIntent);
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        complaints.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, getResources().getString(R.string.myComplaints));
                if(internetServicesCheckUtil.isServiceAvailable(v.getContext())) {
                    if (userSession.isUserLoggedIn(v.getContext())) {
                        Intent i = new Intent(v.getContext(), UserSnapshotActivity.class);
                        startActivity(i);
                    } else {
                        googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.ACCESS_DENIED, getResources().getString(R.string.myComplaintsNotLoggedIn));
                        Intent i = new Intent(v.getContext(), LoginActivity.class);
                        i.putExtra("MODE", true);
                        startActivityForResult(i, REQUEST_MY_COMPLAINTS);
                    }
                }
                else {
                    googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.NO_SERVICE, getResources().getString(R.string.myComplaintsNoInternet));
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), AlertDialog.THEME_HOLO_LIGHT);
                    final Context c = v.getContext();
                    builder.setMessage(getResources().getString(R.string.needInternetService))
                            .setCancelable(false)
                            .setTitle(getResources().getString(R.string.internetConnNeeded))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton(getResources().getString(R.string.enableWiFi), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    GenericUtil.enableWifi(c);
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        leaders.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, getResources().getString(R.string.myLeadersLabel));
                if(internetServicesCheckUtil.isServiceAvailable(v.getContext())) {
                    if (userSession.isUserLoggedIn(v.getContext()) && userSession.isUserLocationKnown()) {
                        Intent i = new Intent(v.getContext(), LeaderListActivity.class);
                        startActivity(i);
                    } else if (userSession.isUserLoggedIn(v.getContext()) && !userSession.isUserLocationKnown()) {
                        googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.ACCESS_DENIED, getResources().getString(R.string.myLeadersLocationNotMarked));
                        Intent i = new Intent(v.getContext(), MarkHomeActivity.class);
                        i.putExtra("MODE", true);
                        startActivityForResult(i, REQUEST_MY_LEADERS);
                    } else {
                        googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.ACCESS_DENIED, getResources().getString(R.string.myLeadersNotLoggedIn));
                        Intent i = new Intent(v.getContext(), LoginActivity.class);
                        i.putExtra("MODE", true);
                        startActivityForResult(i, REQUEST_MY_LEADERS);
                    }
                }
                else {
                    googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.NO_SERVICE, getResources().getString(R.string.myLeadersNoInternet));
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), AlertDialog.THEME_HOLO_LIGHT);
                    final Context c = v.getContext();
                    builder.setMessage(getResources().getString(R.string.needInternetService))
                            .setCancelable(false)
                            .setTitle(getResources().getString(R.string.internetConnNeeded))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton(getResources().getString(R.string.enableWiFi), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    GenericUtil.enableWifi(c);
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        constituency.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, getResources().getString(R.string.myConstituencyLabel));
                if(internetServicesCheckUtil.isServiceAvailable(v.getContext())) {
                    if(userSession.isUserLoggedIn(v.getContext()) && userSession.isUserLocationKnown()) {
                        Intent i = new Intent(v.getContext(), LocationListActivity.class);
                        startActivity(i);
                    }
                    else if(userSession.isUserLoggedIn(v.getContext()) && !userSession.isUserLocationKnown()) {
                        googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.ACCESS_DENIED, getResources().getString(R.string.myConstituencyLocationNotMarked));
                        Intent i = new Intent(v.getContext(), MarkHomeActivity.class);
                        i.putExtra("MODE", true);
                        startActivityForResult(i, REQUEST_MY_CONSTITUENCY);
                    }
                    else {
                        googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.ACCESS_DENIED, getResources().getString(R.string.myConstituencyNotLoggedIn));
                        Intent i = new Intent(v.getContext(), LoginActivity.class);
                        i.putExtra("MODE", true);
                        startActivityForResult(i, REQUEST_MY_CONSTITUENCY);
                    }
                }
                else {
                    googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.NO_SERVICE, getResources().getString(R.string.myConstituencyNoInternet));
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), AlertDialog.THEME_HOLO_LIGHT);
                    final Context c = v.getContext();
                    builder.setMessage(getResources().getString(R.string.needInternetService))
                            .setCancelable(false)
                            .setTitle(getResources().getString(R.string.internetConnNeeded))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton(getResources().getString(R.string.enableWiFi), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    GenericUtil.enableWifi(c);
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        profile.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, getResources().getString(R.string.myProfileLabel));
                if(internetServicesCheckUtil.isServiceAvailable(v.getContext())) {
                    if(userSession.isUserLoggedIn(v.getContext())) {
                        Intent i = new Intent(v.getContext(), MyProfileActivity.class);
                        startActivity(i);
                    }
                    else {
                        googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.ACCESS_DENIED, getResources().getString(R.string.myProfileNotLoggedIn));
                        Intent i = new Intent(v.getContext(), LoginActivity.class);
                        i.putExtra("MODE", true);
                        startActivityForResult(i, REQUEST_MY_PROFILE);
                    }
                }
                else {
                    googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.NO_SERVICE, getResources().getString(R.string.myProfileNoInternet));
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), AlertDialog.THEME_HOLO_LIGHT);
                    final Context c = v.getContext();
                    builder.setMessage(getResources().getString(R.string.needInternetService))
                            .setCancelable(false)
                            .setTitle(getResources().getString(R.string.internetConnNeeded))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton(getResources().getString(R.string.enableWiFi), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    GenericUtil.enableWifi(c);
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationUtil.subscribe(applicationContext, true);
        mapReady = false;
    }

    @Override
    protected void onStop() {
        locationUtil.unsubscribe();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
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
            hRevGeocode.setActualText(event.getRevGeocodedLocation());
            userSession.setUserRevGeocodedLocation(event.getRevGeocodedFullData());
            retryRevGeocoding = false;
        }
        else {
            retryRevGeocoding = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_MY_COMPLAINTS:
                    complaints.performClick();
                    break;
                case REQUEST_MY_CONSTITUENCY:
                    constituency.performClick();
                    break;
                case REQUEST_MY_LEADERS:
                    leaders.performClick();
                    break;
                case REQUEST_MY_PROFILE:
                    profile.performClick();
                    break;
            }
        }
    }
}
