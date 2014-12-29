package com.eswaraj.app.eswaraj.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.ComplaintPostedEvent;
import com.eswaraj.app.eswaraj.events.SavedComplaintEvent;
import com.eswaraj.app.eswaraj.fragments.AddDetailsFragment;
import com.eswaraj.app.eswaraj.util.LocationUtil;
import com.eswaraj.app.eswaraj.volley.ComplaintPostRequest;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class AddDetailsActivity extends BaseActivity {

    @Inject
    LocationUtil locationUtil;
    @Inject
    Context applicationContext;
    @Inject
    EventBus eventBus;

    private AddDetailsFragment addDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);

        addDetailsFragment = (AddDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.adFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.register(this);
        locationUtil.subscribe(applicationContext, false);
    }

    @Override
    protected void onStop() {
        locationUtil.unsubscribe();
        eventBus.unregister(this);
        super.onStop();
    }

    public void onEventMainThread(ComplaintPostedEvent event) {
        Intent i = new Intent(this, ComplaintSummaryActivity.class);
        i.putExtra("COMPLAINT", event.getComplaintDto());
        if(event.getImageFile() != null) {
            i.putExtra("IMAGE", event.getImageFile());
        }
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        addDetailsFragment.onActivityResult(requestCode, resultCode, data);
    }
}
