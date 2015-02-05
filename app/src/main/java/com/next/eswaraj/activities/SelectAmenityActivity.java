package com.next.eswaraj.activities;

import android.content.Intent;
import android.os.Bundle;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.events.AmenitySelectEvent;
import com.next.eswaraj.fragments.SelectAmenityFragment;

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
        setTitle("Select Amenity");

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
