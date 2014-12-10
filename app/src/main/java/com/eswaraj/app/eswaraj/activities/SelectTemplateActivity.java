package com.eswaraj.app.eswaraj.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.fragments.AmenityBannerFragment;
import com.eswaraj.app.eswaraj.fragments.BottomMenuBarFragment;
import com.eswaraj.app.eswaraj.fragments.TemplatesFragment;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

public class SelectTemplateActivity extends BaseActivity {

    private BottomMenuBarFragment bottomMenuBarFragment;
    private AmenityBannerFragment amenityBannerFragment;
    private TemplatesFragment templatesFragment;
    private CategoryWithChildCategoryDto amenity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_template);

        //Retrieve data from Intent
        Intent i = getIntent();
        amenity = (CategoryWithChildCategoryDto) i.getSerializableExtra("AMENITY");

        //Create fragments
        bottomMenuBarFragment = new BottomMenuBarFragment();
        amenityBannerFragment = new AmenityBannerFragment();
        templatesFragment = new TemplatesFragment();

        amenityBannerFragment.setAmenity(amenity);
        templatesFragment.setTemplate(amenity.getChildCategories());

        //Add all fragments
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.stBanner, amenityBannerFragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.stList, templatesFragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.stMenuBar, bottomMenuBarFragment).commit();
        }
    }

}
