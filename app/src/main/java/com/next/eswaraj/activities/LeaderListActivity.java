package com.next.eswaraj.activities;

import android.content.Intent;
import android.os.Bundle;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.events.ShowLeaderEvent;
import com.next.eswaraj.fragments.LeaderListFragment;

import java.io.Serializable;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class LeaderListActivity extends BaseActivity {

    @Inject
    EventBus eventBus;

    private LeaderListFragment leaderListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_list);
        setTitle("My leaders");

        eventBus.register(this);

        leaderListFragment = new LeaderListFragment();
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.llFragment, leaderListFragment).commit();
        }
    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(ShowLeaderEvent event) {
        Intent i = new Intent(this, LeaderActivity.class);
        i.putExtra("LEADER", (Serializable) event.getPoliticalBodyAdminDto());
        startActivity(i);
    }
}
