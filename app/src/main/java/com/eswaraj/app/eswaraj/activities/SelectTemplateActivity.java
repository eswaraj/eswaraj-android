package com.eswaraj.app.eswaraj.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.BannerClickEvent;
import com.eswaraj.app.eswaraj.events.TemplateSelectEvent;
import com.eswaraj.app.eswaraj.fragments.AmenityBannerFragment;
import com.eswaraj.app.eswaraj.fragments.BottomMenuBarFragment;
import com.eswaraj.app.eswaraj.fragments.TemplatesFragment;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import java.io.Serializable;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class SelectTemplateActivity extends BaseActivity {

    @Inject
    EventBus eventBus;

    private TemplatesFragment templatesFragment;
    private CategoryWithChildCategoryDto amenity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_template);

        //Retrieve data from Intent
        Intent i = getIntent();
        amenity = (CategoryWithChildCategoryDto) i.getSerializableExtra("AMENITY");

        //Create fragment
        templatesFragment = new TemplatesFragment();
        templatesFragment.setAmenity(amenity);

        //Add all fragments
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.stContainer, templatesFragment).commit();
        }
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

    public void onEventMainThread(TemplateSelectEvent event) {
        Intent i = new Intent(this, AddDetailsActivity.class);
        i.putExtra("TEMPLATE", event.getCategoryWithChildCategoryDto());
        startActivity(i);
    }

    public void onEventMainThread(BannerClickEvent event) {
        Intent i = new Intent(this, YoutubeActivity.class);
        i.putExtra("VIDEO_ID", event.getVideo());
        startActivity(i);
    }

}
