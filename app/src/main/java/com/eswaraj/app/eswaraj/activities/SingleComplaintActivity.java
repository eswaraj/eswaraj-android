package com.eswaraj.app.eswaraj.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.fragments.BottomMenuBarFragment;
import com.eswaraj.app.eswaraj.fragments.MyComplaintsFragment;
import com.eswaraj.app.eswaraj.fragments.SingleComplaintFragment;

public class SingleComplaintActivity extends BaseActivity {

    private SingleComplaintFragment singleComplaintFragment;
    private BottomMenuBarFragment bottomMenuBarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_complaint);

        singleComplaintFragment = new SingleComplaintFragment();
        bottomMenuBarFragment = new BottomMenuBarFragment();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.scContainer, singleComplaintFragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.scMenuBar, bottomMenuBarFragment).commit();
        }
    }

}
