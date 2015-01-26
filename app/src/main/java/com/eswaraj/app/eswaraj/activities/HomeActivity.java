package com.eswaraj.app.eswaraj.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.adapters.DialogAdapter;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.GetLeadersEvent;
import com.eswaraj.app.eswaraj.events.GetProfileImageEvent;
import com.eswaraj.app.eswaraj.events.RevGeocodeEvent;
import com.eswaraj.app.eswaraj.fragments.GoogleMapFragment;
import com.eswaraj.app.eswaraj.helpers.GoogleAnalyticsTracker;
import com.eswaraj.app.eswaraj.helpers.ReverseGeocodingTask;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.DialogItem;
import com.eswaraj.app.eswaraj.models.PoliticalBodyAdminDto;
import com.eswaraj.app.eswaraj.util.GenericUtil;
import com.eswaraj.app.eswaraj.util.InternetServicesCheckUtil;
import com.eswaraj.app.eswaraj.util.LocationServicesCheckUtil;
import com.eswaraj.app.eswaraj.util.LocationUtil;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.eswaraj.app.eswaraj.widgets.ProgressTextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

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
    MiddlewareServiceImpl middlewareService;
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
    private Button hCreate;
    private LinearLayout sError;
    private Button sClose;

    private Boolean retryRevGeocoding = false;

    private ArrayList<DialogItem> constituencyDialogItems = new ArrayList<DialogItem>();
    private GridView constituencyGridView;
    private ProgressWheel constituencyProgressWheel;
    private AlertDialog constituencyAlertDialog;

    private ArrayList<DialogItem> leaderDialogItems = new ArrayList<DialogItem>();
    private GridView leaderGridView;
    private ProgressWheel leaderProgressWheel;
    private AlertDialog leaderAlertDialog;
    private AtomicInteger leaderCount = new AtomicInteger(0);
    private AtomicInteger leaderTotal = new AtomicInteger(0);

    private final int REQUEST_MY_COMPLAINTS = 0;
    private final int REQUEST_MY_CONSTITUENCY = 1;
    private final int REQUEST_MY_LEADERS = 2;
    private final int REQUEST_MY_PROFILE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        complaints = (ImageView) findViewById(R.id.hComplaints);
        leaders = (ImageView) findViewById(R.id.hLeaders);
        constituency = (ImageView) findViewById(R.id.hConstituency);
        profile = (ImageView) findViewById(R.id.hProfile);
        hRevGeocode = (ProgressTextView) findViewById(R.id.hRevGeocode);
        hCreate = (Button) findViewById(R.id.hCreate);

        hRevGeocode.setTextColor(Color.parseColor("#929292"));

        complaints.setImageDrawable(getResources().getDrawable(R.drawable.complaint));
        leaders.setImageDrawable(getResources().getDrawable(R.drawable.leader));
        constituency.setImageDrawable(getResources().getDrawable(R.drawable.constituency));
        profile.setImageDrawable(getResources().getDrawable(R.drawable.profile));

        googleMapFragment = (GoogleMapFragment) getSupportFragmentManager().findFragmentById(R.id.hMap);
        googleMapFragment.setContext(this);

        hCreate.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Create Complaint");
                if(locationServicesCheckUtil.isServiceAvailable(v.getContext())) {
                    Intent i = new Intent(v.getContext(), SelectAmenityActivity.class);
                    startActivity(i);
                }
                else {
                    googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.NO_SERVICE, "Create Complaint: No Location Service");
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), AlertDialog.THEME_HOLO_LIGHT);
                    builder.setMessage("You need location services enabled to use this feature")
                            .setCancelable(false)
                            .setTitle("Location Service needed")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
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
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "My Complaints");
                if(internetServicesCheckUtil.isServiceAvailable(v.getContext())) {
                    if (userSession.isUserLoggedIn(v.getContext())) {
                        Intent i = new Intent(v.getContext(), ComplaintsActivity.class);
                        startActivity(i);
                    } else {
                        googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.ACCESS_DENIED, "My Complaints: Not Logged-in");
                        Intent i = new Intent(v.getContext(), LoginActivity.class);
                        i.putExtra("MODE", true);
                        startActivityForResult(i, REQUEST_MY_COMPLAINTS);
                    }
                }
                else {
                    googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.NO_SERVICE, "My Complaints: No Internet Service");
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), AlertDialog.THEME_HOLO_LIGHT);
                    builder.setMessage("You need internet services enabled to use this feature")
                            .setCancelable(false)
                            .setTitle("Internet Connection needed")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
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
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "My Leaders");
                if(internetServicesCheckUtil.isServiceAvailable(v.getContext())) {
                    if (userSession.isUserLoggedIn(v.getContext()) && userSession.isUserLocationKnown()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), AlertDialog.THEME_HOLO_LIGHT);
                        LayoutInflater inflater = getLayoutInflater();

                        View rootView = inflater.inflate(R.layout.dialog_select, null);
                        leaderGridView = (GridView) rootView.findViewById(R.id.sOptionList);
                        leaderProgressWheel = (ProgressWheel) rootView.findViewById(R.id.sProgressWheel);
                        sError = (LinearLayout) rootView.findViewById(R.id.sError);
                        sClose = (Button) rootView.findViewById(R.id.sClose);

                        leaderGridView.setVisibility(View.INVISIBLE);
                        leaderProgressWheel.setVisibility(View.VISIBLE);
                        sError.setVisibility(View.INVISIBLE);

                        builder.setView(rootView)
                                .setCancelable(true)
                                .setTitle("Select Leader");
                        leaderAlertDialog = builder.create();
                        leaderAlertDialog.show();
                        leaderAlertDialog.getWindow().setLayout(700, 400);

                        sClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                leaderAlertDialog.dismiss();
                            }
                        });

                        middlewareService.loadLeaders(v.getContext(), userSession, true);
                    } else if (userSession.isUserLoggedIn(v.getContext()) && !userSession.isUserLocationKnown()) {
                        googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.ACCESS_DENIED, "My Leaders: Location not marked");
                        Intent i = new Intent(v.getContext(), MarkLocationActivity.class);
                        i.putExtra("MODE", true);
                        startActivityForResult(i, REQUEST_MY_LEADERS);
                    } else {
                        googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.ACCESS_DENIED, "My Leaders: Not Logged-in");
                        Intent i = new Intent(v.getContext(), LoginActivity.class);
                        i.putExtra("MODE", true);
                        startActivityForResult(i, REQUEST_MY_LEADERS);
                    }
                }
                else {
                    googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.NO_SERVICE, "My Leaders: No Internet Service");
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), AlertDialog.THEME_HOLO_LIGHT);
                    builder.setMessage("You need internet services enabled to use this feature")
                            .setCancelable(false)
                            .setTitle("Internet Connection needed")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
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
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "My Constituency");
                if(internetServicesCheckUtil.isServiceAvailable(v.getContext())) {
                    if(userSession.isUserLoggedIn(v.getContext()) && userSession.isUserLocationKnown()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), AlertDialog.THEME_HOLO_LIGHT);
                        LayoutInflater inflater = getLayoutInflater();

                        View rootView = inflater.inflate(R.layout.dialog_select, null);
                        constituencyGridView = (GridView) rootView.findViewById(R.id.sOptionList);
                        constituencyProgressWheel = (ProgressWheel) rootView.findViewById(R.id.sProgressWheel);
                        sError = (LinearLayout) rootView.findViewById(R.id.sError);
                        sClose = (Button) rootView.findViewById(R.id.sClose);

                        constituencyGridView.setVisibility(View.VISIBLE);
                        constituencyProgressWheel.setVisibility(View.INVISIBLE);
                        sError.setVisibility(View.INVISIBLE);

                        sClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                constituencyAlertDialog.dismiss();
                            }
                        });

                        constituencyDialogItems.clear();

                        if(userSession.getUser().getPerson().getPersonAddress().getState() != null) {
                            DialogItem state = new DialogItem();
                            state.setName(userSession.getUser().getPerson().getPersonAddress().getState().getName());
                            state.setTitle("State");
                            state.setIcon(BitmapFactory.decodeResource(v.getResources(), R.drawable.constituency));
                            state.setId(userSession.getUser().getPerson().getPersonAddress().getState().getId());
                            state.setLocationDto(userSession.getUser().getPerson().getPersonAddress().getState());
                            state.setTarget(ConstituencyActivity.class);
                            constituencyDialogItems.add(state);
                        }

                        if(userSession.getUser().getPerson().getPersonAddress().getPc() != null) {
                            DialogItem pc = new DialogItem();
                            pc.setName(userSession.getUser().getPerson().getPersonAddress().getPc().getName());
                            pc.setTitle("Parliamentary Constituency");
                            pc.setIcon(BitmapFactory.decodeResource(v.getResources(), R.drawable.constituency));
                            pc.setId(userSession.getUser().getPerson().getPersonAddress().getPc().getId());
                            pc.setLocationDto(userSession.getUser().getPerson().getPersonAddress().getPc());
                            pc.setTarget(ConstituencyActivity.class);
                            constituencyDialogItems.add(pc);
                        }

                        if(userSession.getUser().getPerson().getPersonAddress().getAc() != null) {
                            DialogItem ac = new DialogItem();
                            ac.setName(userSession.getUser().getPerson().getPersonAddress().getAc().getName());
                            ac.setTitle("Assembly Constituency");
                            ac.setIcon(BitmapFactory.decodeResource(v.getResources(), R.drawable.constituency));
                            ac.setId(userSession.getUser().getPerson().getPersonAddress().getAc().getId());
                            ac.setLocationDto(userSession.getUser().getPerson().getPersonAddress().getAc());
                            ac.setTarget(ConstituencyActivity.class);
                            constituencyDialogItems.add(ac);
                        }

                        if(constituencyDialogItems.size() > 0) {
                            DialogAdapter adapter = new DialogAdapter(v.getContext(), R.layout.item_select_dialog, constituencyDialogItems);
                            constituencyGridView.setAdapter(adapter);
                            constituencyGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    constituencyAlertDialog.dismiss();
                                    Intent i = new Intent(view.getContext(), ((DialogItem) constituencyGridView.getAdapter().getItem(position)).getTarget());
                                    i.putExtra("LOCATION", (Serializable) ((DialogItem) constituencyGridView.getAdapter().getItem(position)).getLocationDto());
                                    startActivity(i);
                                }
                            });
                        }
                        else {
                            sError.setVisibility(View.VISIBLE);
                            constituencyGridView.setVisibility(View.INVISIBLE);
                        }

                        builder.setView(rootView)
                                .setCancelable(true)
                                .setTitle("Select Constituency");
                        constituencyAlertDialog = builder.create();
                        constituencyAlertDialog.show();
                        constituencyAlertDialog.getWindow().setLayout(700, 400);


                    }
                    else if(userSession.isUserLoggedIn(v.getContext()) && !userSession.isUserLocationKnown()) {
                        googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.ACCESS_DENIED, "My Constituency: Location not marked");
                        Intent i = new Intent(v.getContext(), MarkLocationActivity.class);
                        i.putExtra("MODE", true);
                        startActivityForResult(i, REQUEST_MY_CONSTITUENCY);
                    }
                    else {
                        googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.ACCESS_DENIED, "My Constituency: Not Logged-in");
                        Intent i = new Intent(v.getContext(), LoginActivity.class);
                        i.putExtra("MODE", true);
                        startActivityForResult(i, REQUEST_MY_CONSTITUENCY);
                    }
                }
                else {
                    googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.NO_SERVICE, "My Constituency: No Internet Service");
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), AlertDialog.THEME_HOLO_LIGHT);
                    builder.setMessage("You need internet services enabled to use this feature")
                            .setCancelable(false)
                            .setTitle("Internet Connection needed")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
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
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "My Profile");
                if(internetServicesCheckUtil.isServiceAvailable(v.getContext())) {
                    if(userSession.isUserLoggedIn(v.getContext())) {
                        Intent i = new Intent(v.getContext(), MyProfileActivity.class);
                        startActivity(i);
                    }
                    else {
                        googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.ACCESS_DENIED, "My Profile: Not Logged-in");
                        Intent i = new Intent(v.getContext(), LoginActivity.class);
                        i.putExtra("MODE", true);
                        startActivityForResult(i, REQUEST_MY_PROFILE);
                    }
                }
                else {
                    googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.NO_SERVICE, "My Profile: No Internet Service");
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), AlertDialog.THEME_HOLO_LIGHT);
                    builder.setMessage("You need internet services enabled to use this feature")
                            .setCancelable(false)
                            .setTitle("Internet Connection needed")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
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
            hRevGeocode.setActualText(event.getRevGeocodedLocation());
            userSession.setUserRevGeocodedLocation(event.getRevGeocodedFullData());
            retryRevGeocoding = false;
        }
        else {
            retryRevGeocoding = true;
        }
    }

    public void onEventMainThread(GetLeadersEvent event) {
        if(event.getSuccess()) {
            leaderDialogItems.clear();
            leaderTotal.set(event.getPoliticalBodyAdminDtos().size());
            leaderCount.set(0);
            for(PoliticalBodyAdminDto politicalBodyAdminDto : event.getPoliticalBodyAdminDtos()) {
                DialogItem dialogItem = new DialogItem();
                dialogItem.setId(politicalBodyAdminDto.getId());
                dialogItem.setName(politicalBodyAdminDto.getName());
                dialogItem.setTitle(politicalBodyAdminDto.getPoliticalAdminTypeDto().getShortName() + ", " + politicalBodyAdminDto.getLocation().getName());
                dialogItem.setPoliticalBodyAdminDto(politicalBodyAdminDto);
                dialogItem.setTarget(LeaderActivity.class);
                leaderDialogItems.add(dialogItem);
                middlewareService.loadProfileImage(this, politicalBodyAdminDto.getProfilePhoto(), politicalBodyAdminDto.getId(), true, event.getLoadProfilePhotos());
            }
            if(event.getPoliticalBodyAdminDtos() == null || event.getPoliticalBodyAdminDtos().size() == 0) {
                leaderProgressWheel.setVisibility(View.INVISIBLE);
                sError.setVisibility(View.VISIBLE);
            }
        }
    }

    public void onEventMainThread(GetProfileImageEvent event) {
        for(DialogItem dialogItem : leaderDialogItems) {
            if(dialogItem.getId().equals(event.getId())) {
                dialogItem.setIcon(event.getBitmap());
            }
        }
        if(leaderCount.incrementAndGet() == leaderTotal.get()) {
            DialogAdapter adapter = new DialogAdapter(this, R.layout.item_select_dialog, leaderDialogItems);
            leaderGridView.setNumColumns(leaderTotal.get());
            leaderGridView.setAdapter(adapter);
            leaderGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    leaderAlertDialog.dismiss();
                    Intent i = new Intent(view.getContext(), ((DialogItem) leaderGridView.getAdapter().getItem(position)).getTarget());
                    i.putExtra("LEADER", (Serializable) ((DialogItem) leaderGridView.getAdapter().getItem(position)).getPoliticalBodyAdminDto());
                    startActivity(i);
                }
            });
            leaderProgressWheel.setVisibility(View.INVISIBLE);
            leaderGridView.setVisibility(View.VISIBLE);
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
