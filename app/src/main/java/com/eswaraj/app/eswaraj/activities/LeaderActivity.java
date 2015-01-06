package com.eswaraj.app.eswaraj.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.fragments.LeaderFragment;

public class LeaderActivity extends BaseActivity {

    private LeaderFragment leaderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader);

        leaderFragment = (LeaderFragment) getSupportFragmentManager().findFragmentById(R.id.leaderFragment);
    }

}
