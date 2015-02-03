package com.eswaraj.app.eswaraj.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.adapters.GooglePlacesListAdapter;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.GooglePlaceDetailsEvent;
import com.eswaraj.app.eswaraj.events.GooglePlacesListEvent;
import com.eswaraj.app.eswaraj.fragments.GoogleMapFragment;
import com.eswaraj.app.eswaraj.helpers.GoogleAnalyticsTracker;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.GooglePlace;
import com.eswaraj.app.eswaraj.util.GooglePlacesUtil;
import com.eswaraj.app.eswaraj.util.LocationUtil;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class PlaceSearchActivity extends BaseActivity {

    @Inject
    EventBus eventBus;
    @Inject
    GooglePlacesUtil googlePlacesUtil;
    @Inject
    GoogleAnalyticsTracker googleAnalyticsTracker;

    private EditText psInput;
    private ListView psList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_search);
        setTitle("Search location");
        eventBus.register(this);

        psInput = (EditText) findViewById(R.id.psInput);
        psList = (ListView) findViewById(R.id.psList);

        psInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                googlePlacesUtil.getPlacesList(psInput.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        psList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                googlePlacesUtil.getPlaceDetails((GooglePlace) psList.getAdapter().getItem(position));
            }
        });

    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(GooglePlacesListEvent event) {
        if(event.getSuccess()) {
            psList.setAdapter(new GooglePlacesListAdapter(this, R.layout.item_google_places_list, event.getArrayList()));
        }
        else {
            Toast.makeText(this, "Could not fetch list of places. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

    public void onEventMainThread(GooglePlaceDetailsEvent event) {
        if(event.getSuccess()) {
            GooglePlace googlePlace = event.getGooglePlace();
            Intent i = new Intent();
            i.putExtra("LAT", googlePlace.getLatitude());
            i.putExtra("LNG", googlePlace.getLongitude());
            if (getParent() == null) {
                setResult(Activity.RESULT_OK, i);
            } else {
                getParent().setResult(Activity.RESULT_OK, i);
            }
            finish();
        }
        else {
            Toast.makeText(this, "Could not fetch details of selected place. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

}
