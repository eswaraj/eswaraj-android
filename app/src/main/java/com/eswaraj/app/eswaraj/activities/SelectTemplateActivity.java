package com.eswaraj.app.eswaraj.activities;

import android.content.Intent;
import android.os.Bundle;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.BannerClickEvent;
import com.eswaraj.app.eswaraj.events.TemplateSelectEvent;
import com.eswaraj.app.eswaraj.fragments.SelectTemplateFragment;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class SelectTemplateActivity extends BaseActivity {

    @Inject
    EventBus eventBus;

    private SelectTemplateFragment selectTemplateFragment;
    private CategoryWithChildCategoryDto amenity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_template);

        //Retrieve data from Intent
        Intent i = getIntent();
        amenity = (CategoryWithChildCategoryDto) i.getSerializableExtra("AMENITY");

        //Create fragment
        selectTemplateFragment = new SelectTemplateFragment();
        selectTemplateFragment.setAmenity(amenity);

        //Add all fragments
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.stContainer, selectTemplateFragment).commit();
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
        i.putExtra("TEMPLATE", event.getTemplate());
        i.putExtra("AMENITY", event.getAmenity());
        startActivity(i);
    }

    public void onEventMainThread(BannerClickEvent event) {
        Intent i = new Intent(this, YoutubeActivity.class);
        i.putExtra("VIDEO_ID", event.getVideo());
        startActivity(i);
    }

}
