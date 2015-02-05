package com.next.eswaraj.activities;

import android.content.Intent;
import android.os.Bundle;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.events.ComplaintSelectedEvent;
import com.next.eswaraj.events.ShowConstituencyComplaintsEvent;
import com.next.eswaraj.events.ShowLeadersForLocationEvent;
import com.next.eswaraj.fragments.ConstituencySnapshotFragment;
import com.next.eswaraj.models.ComplaintFilter;

import java.io.Serializable;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class ConstituencySnapshotActivity extends BaseActivity {

    @Inject
    EventBus eventBus;

    private final int OPEN_COMPLAINT_REQUEST = 99;
    private final int SHOW_FILTER_REQUEST = 9999;

    ConstituencySnapshotFragment constituencySnapshotFragment;

    private Boolean isStopped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);
        showFilter = true;
        setContentView(R.layout.activity_constituency_snapshot);
        constituencySnapshotFragment = (ConstituencySnapshotFragment) getSupportFragmentManager().findFragmentById(R.id.csFragment);
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

    public void onEventMainThread(ShowConstituencyComplaintsEvent event) {
        Intent i = new Intent(this, ConstituencyComplaintsActivity.class);
        i.putExtra("LOCATION", (Serializable) event.getLocationDto());
        i.putExtra("DATA_PRESENT", true);
        startActivity(i);
    }

    public void onEventMainThread(ShowLeadersForLocationEvent event) {
        Intent i = new Intent(this, LeaderListActivity.class);
        i.putExtra("LOCATION", (Serializable) event.getLocationDto());
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OPEN_COMPLAINT_REQUEST && resultCode == RESULT_OK) {
            if(data != null) {
                constituencySnapshotFragment.markComplaintClosed(data.getLongExtra("ID", -1));
            }
        }
        if(requestCode == SHOW_FILTER_REQUEST && resultCode == RESULT_OK) {
            constituencySnapshotFragment.setFilter((ComplaintFilter) data.getSerializableExtra("FILTER"));
            complaintFilter = (ComplaintFilter) data.getSerializableExtra("FILTER");
        }
    }

}
