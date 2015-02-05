package com.next.eswaraj.activities;

import android.content.Intent;
import android.os.Bundle;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.events.ComplaintSelectedEvent;
import com.next.eswaraj.events.MarkerClickEvent;
import com.next.eswaraj.fragments.ConstituencyComplaintsFragment;
import com.next.eswaraj.models.ComplaintFilter;

import java.io.Serializable;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class ConstituencyComplaintsActivity extends BaseActivity {

    @Inject
    EventBus eventBus;

    private ConstituencyComplaintsFragment myConstituencyFragment;
    private final int OPEN_COMPLAINT_REQUEST = 99;
    private final int SHOW_FILTER_REQUEST = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);
        showFilter = true;
        setContentView(R.layout.activity_constituency);

        myConstituencyFragment = (ConstituencyComplaintsFragment) getSupportFragmentManager().findFragmentById(R.id.constituencyFragment);
    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onStop();
    }

    public void onEventMainThread(ComplaintSelectedEvent event) {
        Intent i = new Intent(this, SingleComplaintActivity.class);
        i.putExtra("COMPLAINT", (Serializable) event.getComplaintDto());
        i.putExtra("DATA_PRESENT", true);
        //i.putExtra("COMPLAINT_ID", event.getComplaintDto().getId());
        //i.putExtra("DATA_PRESENT", false);
        startActivityForResult(i, OPEN_COMPLAINT_REQUEST);
    }

    public void onEventMainThread(MarkerClickEvent event) {
        Intent i = new Intent(this, SingleComplaintActivity.class);
        i.putExtra("COMPLAINT", (Serializable) event.getComplaintDto());
        i.putExtra("DATA_PRESENT", true);
        startActivityForResult(i, OPEN_COMPLAINT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OPEN_COMPLAINT_REQUEST && resultCode == RESULT_OK) {
            if(data != null) {
                myConstituencyFragment.markComplaintClosed(data.getLongExtra("ID", -1));
            }
        }
        if(requestCode == SHOW_FILTER_REQUEST && resultCode == RESULT_OK) {
            myConstituencyFragment.setFilter((ComplaintFilter) data.getSerializableExtra("FILTER"));
            complaintFilter = (ComplaintFilter) data.getSerializableExtra("FILTER");
        }
    }
}
