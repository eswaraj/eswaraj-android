package com.next.eswaraj.activities;

import android.content.Intent;
import android.os.Bundle;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.events.LoginStatusEvent;
import com.next.eswaraj.events.StartAnotherActivityEvent;
import com.next.eswaraj.events.UserContinueEvent;
import com.next.eswaraj.fragments.MyProfileFragment;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class MyProfileActivity extends BaseActivity {

    @Inject
    EventBus eventBus;

    private MyProfileFragment myProfileFragment;
    private final int UPDATE_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        setTitle("Profile");

        myProfileFragment = (MyProfileFragment) getSupportFragmentManager().findFragmentById(R.id.mpFragment);
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

    public void onEventMainThread(StartAnotherActivityEvent event) {
        Intent i = new Intent(this, MarkHomeActivity.class);
        i.putExtra("MODE", true);
        i.putExtra("ALWAYS", true);
        startActivityForResult(i, UPDATE_LOCATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UPDATE_LOCATION) {
            myProfileFragment.updateUserLocationDisplay();
        }
    }

    public void onEventMainThread(UserContinueEvent event) {
        finish();
    }

    public void onEventMainThread(LoginStatusEvent event) {
        if(!event.getLoggedIn()) {
            finish();
        }
    }
}
