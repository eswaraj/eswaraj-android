package com.eswaraj.app.eswaraj.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.ComplaintSelectedEvent;
import com.eswaraj.app.eswaraj.events.ShowConstituencyComplaintsEvent;
import com.eswaraj.app.eswaraj.events.ShowProfileEvent;
import com.eswaraj.app.eswaraj.events.ShowSelectAmenityEvent;
import com.eswaraj.app.eswaraj.events.ShowUserComplaintsEvent;
import com.eswaraj.app.eswaraj.fragments.UserSnapshotFragment;
import com.eswaraj.app.eswaraj.models.ComplaintFilter;

import java.io.Serializable;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class UserSnapshotActivity extends BaseActivity {

    private UserSnapshotFragment userSnapshotFragment;

    private Boolean isStopped;

    @Inject
    EventBus eventBus;

    private final int OPEN_COMPLAINT_REQUEST = 99;
    private final int SHOW_FILTER_REQUEST = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showFilter = true;
        eventBus.register(this);
        setContentView(R.layout.activity_user_snapshot);
        userSnapshotFragment = (UserSnapshotFragment) getSupportFragmentManager().findFragmentById(R.id.usFragment);
    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isStopped = false;
    }

    @Override
    protected void onStop() {
        isStopped = true;
        super.onStop();
    }

    public void onEventMainThread(ComplaintSelectedEvent event) {
        if(!isStopped) {
            Intent i = new Intent(this, SingleComplaintActivity.class);
            i.putExtra("COMPLAINT", (Serializable) event.getComplaintDto());
            i.putExtra("DATA_PRESENT", true);
            startActivityForResult(i, OPEN_COMPLAINT_REQUEST);
        }
    }

    public void onEventMainThread(ShowUserComplaintsEvent event) {
        Intent i = new Intent(this, UserComplaintsActivity.class);
        startActivity(i);
    }

    public void onEventMainThread(ShowProfileEvent event) {
        Intent i = new Intent(this, MyProfileActivity.class);
        startActivity(i);
    }

    public void onEventMainThread(ShowSelectAmenityEvent event) {
        Intent i = new Intent(this, SelectAmenityActivity.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OPEN_COMPLAINT_REQUEST && resultCode == RESULT_OK) {
            if(data != null) {
                userSnapshotFragment.markComplaintClosed(data.getLongExtra("ID", -1));
            }
        }
        if(requestCode == SHOW_FILTER_REQUEST && resultCode == RESULT_OK) {
            userSnapshotFragment.setFilter((ComplaintFilter) data.getSerializableExtra("FILTER"));
        }
    }

}
