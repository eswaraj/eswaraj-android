package com.eswaraj.app.eswaraj.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.UserContinueEvent;
import com.eswaraj.app.eswaraj.fragments.ComplaintSummaryFragment;
import com.eswaraj.app.eswaraj.fragments.ComplaintSummaryOfflineFragment;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class ComplaintSummaryActivity extends BaseActivity {

    @Inject
    EventBus eventBus;

    private ComplaintSummaryFragment complaintSummaryFragment;
    private ComplaintSummaryOfflineFragment complaintSummaryOfflineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_summary);

        Boolean mode = getIntent().getBooleanExtra("MODE", false);
        if(mode) {
            complaintSummaryFragment = new ComplaintSummaryFragment();
            if(savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().add(R.id.csFragment, complaintSummaryFragment).commit();
            }
        }
        else {
            complaintSummaryOfflineFragment = new ComplaintSummaryOfflineFragment();
            if(savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().add(R.id.csFragment, complaintSummaryOfflineFragment).commit();
            }
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

    public void onEventMainThread(UserContinueEvent event) {
        if(event.getAnother()) {
            Intent i = new Intent(this, SelectAmenityActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        else {
            Intent i = new Intent(this, HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}
