package com.eswaraj.app.eswaraj.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.ShowLocationEvent;
import com.eswaraj.app.eswaraj.fragments.LocationListFragment;
import com.eswaraj.app.eswaraj.models.DialogItem;

import java.io.Serializable;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class LocationListActivity extends BaseActivity {

    @Inject
    EventBus eventBus;

    private LocationListFragment locationListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);
        eventBus.register(this);
        locationListFragment = new LocationListFragment();

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.llFragment, locationListFragment).commit();
        }
    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(ShowLocationEvent event) {
        Intent i = new Intent(this, ConstituencySnapshotActivity.class);
        i.putExtra("LOCATION", (Serializable) event.getLocationDto());
        startActivity(i);
    }
}
