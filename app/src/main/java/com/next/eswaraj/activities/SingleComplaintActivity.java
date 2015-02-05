package com.next.eswaraj.activities;

import android.content.Intent;
import android.os.Bundle;


import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.events.ComplaintClosedEvent;
import com.next.eswaraj.fragments.SingleComplaintFragment;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class SingleComplaintActivity extends BaseActivity {

    @Inject
    EventBus eventBus;

    private SingleComplaintFragment singleComplaintFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_complaint);
        setTitle("Complaint");

        singleComplaintFragment = (SingleComplaintFragment) getSupportFragmentManager().findFragmentById(R.id.scFragment);
        eventBus.register(this);
    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(ComplaintClosedEvent event) {
        if(event.getSuccess()) {
            Intent i = new Intent();
            i.putExtra("ID", event.getComplaintDto().getId());
            setResult(RESULT_OK, i);
        }
    }
}
