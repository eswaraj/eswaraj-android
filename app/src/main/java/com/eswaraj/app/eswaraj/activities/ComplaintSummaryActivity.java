package com.eswaraj.app.eswaraj.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.fragments.BottomMenuBarFragment;
import com.eswaraj.app.eswaraj.fragments.ComplaintSummaryFragment;

public class ComplaintSummaryActivity extends BaseActivity {

    private BottomMenuBarFragment bottomMenuBarFragment;
    private ComplaintSummaryFragment complaintSummaryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_summary);

        bottomMenuBarFragment = new BottomMenuBarFragment();
        complaintSummaryFragment = new ComplaintSummaryFragment();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.csContainer, complaintSummaryFragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.csMenuBar, bottomMenuBarFragment).commit();
        }
    }

}
