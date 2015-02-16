package com.next.eswaraj.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.events.PromiseSelectedEvent;
import com.next.eswaraj.fragments.PromisesListFragment;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class PromisesListActivity extends BaseActivity {

    @Inject
    EventBus eventBus;

    private PromisesListFragment promisesListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promises_list);

        eventBus.register(this);

        if(savedInstanceState == null) {
            promisesListFragment = new PromisesListFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.plContainer, promisesListFragment).commit();
        }
    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(PromiseSelectedEvent event) {
        Intent i = new Intent(this, SinglePromiseActivity.class);
        i.putExtra("PROMISE", event.getPromiseDto());
        startActivity(i);
    }
}
