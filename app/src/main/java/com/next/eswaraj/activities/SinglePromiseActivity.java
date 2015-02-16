package com.next.eswaraj.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.fragments.SinglePromiseFragment;

public class SinglePromiseActivity extends BaseActivity {

    private SinglePromiseFragment singlePromiseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_promise);

        if(savedInstanceState == null) {
            singlePromiseFragment = new SinglePromiseFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.spContainer, singlePromiseFragment).commit();
        }
    }

}
