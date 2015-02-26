package com.next.eswaraj.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.fragments.ConstituencyTimelineFragment;

public class ConstituencyTimelineActivity extends BaseActivity {

    private ConstituencyTimelineFragment constituencyTimelineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constituency_timeline);

        if(savedInstanceState == null) {
            constituencyTimelineFragment = new ConstituencyTimelineFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.ctContainer, constituencyTimelineFragment).commit();
        }
    }

}
