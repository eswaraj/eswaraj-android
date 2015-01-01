package com.eswaraj.app.eswaraj.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.ComplaintSelectedEvent;
import com.eswaraj.app.eswaraj.events.MarkerClickEvent;
import com.eswaraj.app.eswaraj.fragments.ConstituencyFragment;

import java.io.Serializable;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class ConstituencyActivity extends BaseActivity {

    @Inject
    EventBus eventBus;

    private ConstituencyFragment constituencyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constituency);

        constituencyFragment = (ConstituencyFragment) getSupportFragmentManager().findFragmentById(R.id.constituencyFragment);
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
