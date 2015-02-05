package com.next.eswaraj.activities;

import android.content.Intent;
import android.os.Bundle;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.events.ShowLocationEvent;
import com.next.eswaraj.fragments.LocationListFragment;

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
        setTitle("My constituencies");

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
