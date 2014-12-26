package com.eswaraj.app.eswaraj.activities;

import android.content.Context;
import android.os.Bundle;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.fragments.AddDetailsFragment;
import com.eswaraj.app.eswaraj.util.LocationUtil;

import javax.inject.Inject;

public class AddDetailsActivity extends BaseActivity {

    @Inject
    LocationUtil locationUtil;
    @Inject
    Context applicationContext;

    private AddDetailsFragment addDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);

        addDetailsFragment = (AddDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.adFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationUtil.subscribe(applicationContext, false);
    }

    @Override
    protected void onStop() {
        locationUtil.unsubscribe();
        super.onStop();
    }

}
