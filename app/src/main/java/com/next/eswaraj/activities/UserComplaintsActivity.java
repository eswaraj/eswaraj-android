package com.next.eswaraj.activities;

import android.content.Intent;
import android.os.Bundle;


import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.events.ComplaintSelectedEvent;
import com.next.eswaraj.events.MarkerClickEvent;
import com.next.eswaraj.fragments.UserComplaintsFragment;
import com.next.eswaraj.models.ComplaintFilter;


import java.io.Serializable;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class UserComplaintsActivity extends BaseActivity {

    @Inject
    EventBus eventBus;

    //private MyComplaintsFragment myComplaintsFragment;
    //private ComplaintsFragment complaintsFragment;
    private UserComplaintsFragment complaintsFragment;
    private final int OPEN_COMPLAINT_REQUEST = 99;
    private final int SHOW_FILTER_REQUEST = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showFilter = true;
        setContentView(R.layout.activity_my_complaints);

        //myComplaintsFragment = (MyComplaintsFragment) getSupportFragmentManager().findFragmentById(R.id.mcFragment);
        //complaintsFragment = (ComplaintsFragment) getSupportFragmentManager().findFragmentById(R.id.mcFragment);
        complaintsFragment = (UserComplaintsFragment) getSupportFragmentManager().findFragmentById(R.id.mcFragment);
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
        i.putExtra("DATA_PRESENT", true);
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
                //myComplaintsFragment.markComplaintClosed(data.getLongExtra("ID", -1));
                complaintsFragment.markComplaintClosed(data.getLongExtra("ID", -1));
            }
        }

        if(requestCode == SHOW_FILTER_REQUEST && resultCode == RESULT_OK) {
            complaintsFragment.setFilter((ComplaintFilter) data.getSerializableExtra("FILTER"));
            complaintFilter = (ComplaintFilter) data.getSerializableExtra("FILTER");
        }
    }
}
