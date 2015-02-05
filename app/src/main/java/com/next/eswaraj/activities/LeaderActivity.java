package com.next.eswaraj.activities;

import android.content.Intent;
import android.os.Bundle;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.events.StartAnotherActivityEvent;
import com.next.eswaraj.fragments.LeaderFragment;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class LeaderActivity extends BaseActivity {

    @Inject
    EventBus eventBus;

    private LeaderFragment leaderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader);

        leaderFragment = (LeaderFragment) getSupportFragmentManager().findFragmentById(R.id.leaderFragment);
        eventBus.register(this);
    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(StartAnotherActivityEvent event) {
        Intent i = new Intent(this, ConstituencySnapshotActivity.class);
        i.putExtra("ID", event.getId());
        startActivity(i);
    }
}
