package com.eswaraj.app.eswaraj.activities;

import android.content.Intent;
import android.os.Bundle;


import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.ComplaintClosedEvent;
import com.eswaraj.app.eswaraj.fragments.SingleComplaintFragment;

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
