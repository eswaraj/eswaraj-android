package com.eswaraj.app.eswaraj.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.adapters.DialogAdapter;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.RevGeocodeEvent;
import com.eswaraj.app.eswaraj.fragments.GoogleMapFragment;
import com.eswaraj.app.eswaraj.helpers.ReverseGeocodingTask;
import com.eswaraj.app.eswaraj.models.DialogItem;
import com.eswaraj.app.eswaraj.util.GenericUtil;
import com.eswaraj.app.eswaraj.util.InternetServicesCheckUtil;
import com.eswaraj.app.eswaraj.util.LocationServicesCheckUtil;
import com.eswaraj.app.eswaraj.util.LocationUtil;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.Serializable;
import java.util.ArrayList;

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
    private LinearLayout sError;
    private Button sClose;

    private Boolean retryRevGeocoding = false;
    private ArrayList<DialogItem> constituencyDialogItems = new ArrayList<DialogItem>();
    private GridView constituencyGridView;
    private ProgressWheel constituencyProgressWheel;
    private AlertDialog constituencyAlertDialog;

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
                if(locationServicesCheckUtil.isServiceAvailable(v.getContext())) {
                    Intent i = new Intent(v.getContext(), SelectAmenityActivity.class);
                    startActivity(i);
                }
                else {
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
                if(internetServicesCheckUtil.isServiceAvailable(v.getContext())) {
                    if (userSession.isUserLoggedIn(v.getContext())) {
                        Intent i = new Intent(v.getContext(), MyComplaintsActivity.class);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(v.getContext(), LoginActivity.class);
                        i.putExtra("MODE", true);
                        startActivityForResult(i, REQUEST_MY_COMPLAINTS);
                    }
                }
                else {
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
        //TODO:Fix the activity targets
        leaders.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetServicesCheckUtil.isServiceAvailable(v.getContext())) {
                    if (userSession.isUserLoggedIn(v.getContext()) && userSession.isUserLocationKnown()) {
                        Intent i = new Intent(v.getContext(), MyComplaintsActivity.class);
                        startActivity(i);
                    } else if (userSession.isUserLoggedIn(v.getContext()) && !userSession.isUserLocationKnown()) {
                        Intent i = new Intent(v.getContext(), MarkLocationActivity.class);
                        i.putExtra("MODE", true);
                        startActivityForResult(i, REQUEST_MY_LEADERS);
                    } else {
                        Intent i = new Intent(v.getContext(), LoginActivity.class);
                        i.putExtra("MODE", true);
                        startActivityForResult(i, REQUEST_MY_LEADERS);
                    }
                }
                else {
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
                        Intent i = new Intent(v.getContext(), MarkLocationActivity.class);
                        i.putExtra("MODE", true);
                        startActivityForResult(i, REQUEST_MY_CONSTITUENCY);
                    }
                    else {
                        Intent i = new Intent(v.getContext(), LoginActivity.class);
                        i.putExtra("MODE", true);
                        startActivityForResult(i, REQUEST_MY_CONSTITUENCY);
                    }
                }
                else {
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
                if(internetServicesCheckUtil.isServiceAvailable(v.getContext())) {
                    if(userSession.isUserLoggedIn(v.getContext())) {
                        Intent i = new Intent(v.getContext(), MyProfileActivity.class);
                        startActivity(i);
                    }
                    else {
                        Intent i = new Intent(v.getContext(), LoginActivity.class);
                        i.putExtra("MODE", true);
                        startActivityForResult(i, REQUEST_MY_PROFILE);
                    }
                }
                else {
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
            hRevGeocode.setText(event.getRevGeocodedLocation());
            hRevGeocode.setTextColor(Color.parseColor("#929292"));
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
