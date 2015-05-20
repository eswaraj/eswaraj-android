package com.next.eswaraj.activities;

import android.content.Intent;
import android.os.Bundle;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.events.ComplaintPostedEvent;
import com.next.eswaraj.events.ComplaintSavedEvent;
import com.next.eswaraj.fragments.AddDetailsFragment;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class AddDetailsActivity extends BaseActivity {

    @Inject
    EventBus eventBus;

    private AddDetailsFragment addDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);
        setTitle(getResources().getString(R.string.titleAddDetails));

        addDetailsFragment = (AddDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.adFragment);
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

    public void onEventMainThread(ComplaintPostedEvent event) {
        Intent i = new Intent(this, ComplaintSummaryActivity.class);
        i.putExtra("MODE", true);
        i.putExtra("COMPLAINT", event.getComplaintPostResponseDto());
        if(event.getImageFile() != null) {
            i.putExtra("IMAGE", event.getImageFile());
        }
        startActivity(i);
        finish();
    }

    public void onEventMainThread(ComplaintSavedEvent event) {
        Intent i = new Intent(this, ComplaintSummaryActivity.class);
        i.putExtra("MODE", false);
        i.putExtra("COMPLAINT", event.getComplaintSavedResponseDto());
        if(event.getImageFile() != null) {
            i.putExtra("IMAGE", event.getImageFile());
        }
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        addDetailsFragment.onActivityResult(requestCode, resultCode, data);
    }
}
