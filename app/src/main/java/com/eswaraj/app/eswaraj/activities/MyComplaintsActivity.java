package com.eswaraj.app.eswaraj.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.fragments.BottomMenuBarFragment;
import com.eswaraj.app.eswaraj.fragments.MyComplaintsFragment;

public class MyComplaintsActivity extends BaseActivity {

    private MyComplaintsFragment myComplaintsFragment;
    private BottomMenuBarFragment bottomMenuBarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_complaints);

        myComplaintsFragment = new MyComplaintsFragment();
        bottomMenuBarFragment = new BottomMenuBarFragment();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.mcContainer, myComplaintsFragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.mcMenuBar, bottomMenuBarFragment).commit();
        }
    }

}
