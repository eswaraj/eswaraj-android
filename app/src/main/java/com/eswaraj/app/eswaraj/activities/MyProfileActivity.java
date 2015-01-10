package com.eswaraj.app.eswaraj.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.GetProfileEvent;
import com.eswaraj.app.eswaraj.events.LoginStatusEvent;
import com.eswaraj.app.eswaraj.events.StartAnotherActivityEvent;
import com.eswaraj.app.eswaraj.events.UserContinueEvent;
import com.eswaraj.app.eswaraj.fragments.MyProfileFragment;

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
        Intent i = new Intent(this, MarkLocationActivity.class);
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
