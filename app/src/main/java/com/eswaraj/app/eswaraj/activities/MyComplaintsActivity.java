package com.eswaraj.app.eswaraj.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.adapters.ComplaintListAdapter;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.ComplaintSelectedEvent;
import com.eswaraj.app.eswaraj.events.GetUserComplaintsEvent;
import com.eswaraj.app.eswaraj.events.MarkerClickEvent;
import com.eswaraj.app.eswaraj.fragments.BottomMenuBarFragment;
import com.eswaraj.app.eswaraj.fragments.GoogleMapFragment;
import com.eswaraj.app.eswaraj.fragments.MyComplaintsFragment;
import com.eswaraj.app.eswaraj.helpers.WindowAnimationHelper;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.eswaraj.app.eswaraj.widgets.CustomProgressDialog;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.app.eswaraj.models.ComplaintDto;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class MyComplaintsActivity extends BaseActivity {

    @Inject
    EventBus eventBus;

    private MyComplaintsFragment myComplaintsFragment;
    private final int OPEN_COMPLAINT_REQUEST = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_complaints);

        myComplaintsFragment = (MyComplaintsFragment) getSupportFragmentManager().findFragmentById(R.id.mcFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.register(this);
    }

    @Override
    protected void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }

    public void onEventMainThread(ComplaintSelectedEvent event) {
        Intent i = new Intent(this, SingleComplaintActivity.class);
        i.putExtra("COMPLAINT", (Serializable) event.getComplaintDto());
        i.putExtra("DATA_PRESENT", true);
        startActivityForResult(i, OPEN_COMPLAINT_REQUEST);
    }

    public void onEventMainThread(MarkerClickEvent event) {
        Intent i = new Intent(this, SingleComplaintActivity.class);
        i.putExtra("COMPLAINT", (Serializable) event.getComplaintDto());
        i.putExtra("DATA_PRESENT", true);
        startActivityForResult(i, OPEN_COMPLAINT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OPEN_COMPLAINT_REQUEST && resultCode == RESULT_OK) {
            if(data != null) {
                myComplaintsFragment.markComplaintClosed(data.getLongExtra("ID", -1));
            }
        }
    }
}
