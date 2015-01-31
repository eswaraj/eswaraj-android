package com.eswaraj.app.eswaraj.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.GetLeaderEvent;
import com.eswaraj.app.eswaraj.events.ShowLeaderEvent;
import com.eswaraj.app.eswaraj.fragments.LeaderListFragment;
import com.eswaraj.app.eswaraj.models.DialogItem;

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
