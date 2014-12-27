package com.eswaraj.app.eswaraj.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.fragments.BottomMenuBarFragment;
import com.eswaraj.app.eswaraj.fragments.ComplaintSummaryFragment;
import com.eswaraj.app.eswaraj.helpers.WindowAnimationHelper;

public class ComplaintSummaryActivity extends BaseActivity {

    private ComplaintSummaryFragment complaintSummaryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_summary);

        complaintSummaryFragment = (ComplaintSummaryFragment) getSupportFragmentManager().findFragmentById(R.id.csFragment);
    }
}
