package com.eswaraj.app.eswaraj.activities;

import android.os.Bundle;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.fragments.AddDetailsFragment;

public class AddDetailsActivity extends BaseActivity {

    private AddDetailsFragment addDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);

        addDetailsFragment = new AddDetailsFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.adContainer, addDetailsFragment).commit();
        }
    }

}
