package com.eswaraj.app.eswaraj.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.eswaraj.app.eswaraj.R;

public class LaunchActivity extends FragmentActivity {

    private Button lMyComplaints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        lMyComplaints = (Button) findViewById(R.id.lMyComplaints);
        lMyComplaints.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), MyComplaintsActivity.class);
                startActivity(i);
            }
        });
    }

}
