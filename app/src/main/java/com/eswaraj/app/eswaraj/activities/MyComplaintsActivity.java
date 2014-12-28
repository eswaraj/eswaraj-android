package com.eswaraj.app.eswaraj.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.adapters.ComplaintListAdapter;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.ComplaintSelectedEvent;
import com.eswaraj.app.eswaraj.events.GetUserComplaintsEvent;
import com.eswaraj.app.eswaraj.events.MarkerClickEvent;
import com.eswaraj.app.eswaraj.fragments.BottomMenuBarFragment;
import com.eswaraj.app.eswaraj.fragments.GoogleMapFragment;
import com.eswaraj.app.eswaraj.fragments.MyComplaintsFragment;
import com.eswaraj.app.eswaraj.helpers.WindowAnimationHelper;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.web.dto.ComplaintDto;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class MyComplaintsActivity extends BaseActivity implements OnMapReadyCallback {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    UserSessionUtil userSession;

    private List<ComplaintDto> complaintDtoList;
    private MyComplaintsFragment myComplaintsFragment;
    private GoogleMapFragment googleMapFragment;

    private Button mapButton;
    private Button listButton;
    private FrameLayout mcContainer;

    private Boolean mapDisplayed = false;
    private Boolean mapReady = false;
    private Boolean markersAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_complaints);

        myComplaintsFragment = new MyComplaintsFragment();
        googleMapFragment = new GoogleMapFragment();

        googleMapFragment.setContext(this);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.mcContainer, myComplaintsFragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.mcContainer, googleMapFragment).commit();
        }
        getSupportFragmentManager().beginTransaction().hide(googleMapFragment).commit();
        getSupportFragmentManager().executePendingTransactions();
        mapDisplayed = false;

        mapButton = (Button) findViewById(R.id.mcShowMap);
        listButton = (Button) findViewById(R.id.mcShowList);
        mcContainer = (FrameLayout) findViewById(R.id.mcContainer);

        listButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapDisplayed) {
                    mapDisplayed = false;
                    getSupportFragmentManager().beginTransaction().hide(googleMapFragment).commit();
                    getSupportFragmentManager().beginTransaction().show(myComplaintsFragment).commit();
                    getSupportFragmentManager().executePendingTransactions();
                }
            }
        });
        mapButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mapDisplayed) {
                    mapDisplayed = true;
                    getSupportFragmentManager().beginTransaction().hide(myComplaintsFragment).commit();
                    getSupportFragmentManager().beginTransaction().show(googleMapFragment).commit();
                    getSupportFragmentManager().executePendingTransactions();
                    //Post it on UI thread so that it gets en-queued behind fragment transactions and gets executed only after layout has happened for map
                    mcContainer.post(new Runnable() {
                        @Override
                        public void run() {
                            if(!markersAdded) {
                                googleMapFragment.addMarkers(complaintDtoList);
                                markersAdded = true;
                            }
                        }
                    });
                }
            }
        });

        middlewareService.loadUserComplaints(this, userSession.getUser(), true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        if(complaintDtoList != null) {
            googleMapFragment.addMarkers(complaintDtoList);
            markersAdded = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.register(this);
    }

    @Override
    protected void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }

    public void onEventMainThread(GetUserComplaintsEvent event) {
        if(event.getSuccess()) {
            complaintDtoList = event.getComplaintDtoList();
            myComplaintsFragment.setComplaintData(complaintDtoList);
            if(mapReady && mapDisplayed) {
                googleMapFragment.addMarkers(complaintDtoList);
                markersAdded = true;
            }
        }
        else {
            Toast.makeText(this, "Could not fetch user complaints. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

    public void onEventMainThread(ComplaintSelectedEvent event) {
        Intent i = new Intent(this, SingleComplaintActivity.class);
        i.putExtra("COMPLAINT", (Serializable) event.getComplaintDto());
        startActivity(i);
    }

    public void onEventMainThread(MarkerClickEvent event) {
        Intent i = new Intent(this, SingleComplaintActivity.class);
        i.putExtra("COMPLAINT", (Serializable) event.getComplaintDto());
        startActivity(i);
    }
}
