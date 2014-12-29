package com.eswaraj.app.eswaraj.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.AmenitySelectEvent;
import com.eswaraj.app.eswaraj.events.RevGeocodeEvent;
import com.eswaraj.app.eswaraj.fragments.SelectAmenityFragment;
import com.eswaraj.app.eswaraj.fragments.GoogleMapFragment;
import com.eswaraj.app.eswaraj.helpers.ReverseGeocodingTask;
import com.eswaraj.app.eswaraj.util.GenericUtil;
import com.eswaraj.app.eswaraj.util.LocationUtil;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.Serializable;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class SelectAmenityActivity extends BaseActivity {

    @Inject
    EventBus eventBus;

    private SelectAmenityFragment selectAmenityFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_amenity);

        selectAmenityFragment = SelectAmenityFragment.newInstance();

        //Add all fragments
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.asAmenities, selectAmenityFragment).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.registerSticky(this);
    }

    @Override
    protected void onStop() {
        eventBus.unregister(this);
        super.onStop();
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

}
