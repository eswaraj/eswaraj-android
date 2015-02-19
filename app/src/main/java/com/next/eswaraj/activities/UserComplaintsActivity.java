package com.next.eswaraj.activities;

import android.content.Intent;
import android.os.Bundle;


import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.events.ComplaintSelectedEvent;
import com.next.eswaraj.events.MarkerClickEvent;
import com.next.eswaraj.events.ShowSelectAmenityEvent;
import com.next.eswaraj.fragments.UserComplaintsFragment;
import com.next.eswaraj.models.ComplaintFilter;


import java.io.Serializable;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class UserComplaintsActivity extends BaseActivity {

    @Inject
    EventBus eventBus;

    private UserComplaintsFragment complaintsFragment;
    private final int OPEN_COMPLAINT_REQUEST = 99;
    private final int SHOW_FILTER_REQUEST = 9999;
    private Boolean isStopped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showFilter = true;
        eventBus.register(this);
        setContentView(R.layout.activity_my_complaints);

        complaintsFragment = (UserComplaintsFragment) getSupportFragmentManager().findFragmentById(R.id.mcFragment);
    }

    @Override
    protected void onPause() {
        isStopped = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isStopped = false;
    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(ComplaintSelectedEvent event) {
        if(!isStopped) {
            Intent i = new Intent(this, SingleComplaintActivity.class);
            i.putExtra("COMPLAINT", (Serializable) event.getComplaintDto());
            i.putExtra("DATA_PRESENT", true);
            startActivityForResult(i, OPEN_COMPLAINT_REQUEST);
        }
    }

    public void onEventMainThread(MarkerClickEvent event) {
        if(!isStopped) {
            Intent i = new Intent(this, SingleComplaintActivity.class);
            i.putExtra("COMPLAINT", (Serializable) event.getComplaintDto());
            i.putExtra("DATA_PRESENT", true);
            startActivityForResult(i, OPEN_COMPLAINT_REQUEST);
        }
    }

    public void onEventMainThread(ShowSelectAmenityEvent event) {
        if(!isStopped) {
            Intent i = new Intent(this, SelectAmenityActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OPEN_COMPLAINT_REQUEST && resultCode == RESULT_OK) {
            if(data != null) {
                complaintsFragment.markComplaintClosed(data.getLongExtra("ID", -1));
            }
        }

        if(requestCode == SHOW_FILTER_REQUEST && resultCode == RESULT_OK) {
            complaintsFragment.setFilter((ComplaintFilter) data.getSerializableExtra("FILTER"));
            complaintFilter = (ComplaintFilter) data.getSerializableExtra("FILTER");
        }
    }
}
