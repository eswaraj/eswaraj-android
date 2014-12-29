package com.eswaraj.app.eswaraj.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.activities.AddDetailsActivity;
import com.eswaraj.app.eswaraj.activities.SelectTemplateActivity;
import com.eswaraj.app.eswaraj.adapters.TemplateListAdapter;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.TemplateSelectEvent;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class SelectTemplateFragment extends BaseFragment {

    @Inject
    EventBus eventBus;

    private CategoryWithChildCategoryDto amenity;
    private AmenityBannerFragment amenityBannerFragment;
    private ListView listView;

    public SelectTemplateFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        amenityBannerFragment = new AmenityBannerFragment();
        amenityBannerFragment.setAmenity(amenity);
        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction().add(R.id.stBanner, amenityBannerFragment).commit();
        }
    }

    public void setAmenity(CategoryWithChildCategoryDto amenity) {
        this.amenity = amenity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_templates, container, false);
        listView = (ListView) rootView.findViewById(R.id.stList);

        TemplateListAdapter templateListAdapter = new TemplateListAdapter(getActivity(), R.layout.item_subcategory_list, amenity.getChildCategories());
        listView.setAdapter(templateListAdapter);
        listView.setDividerHeight(0);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                TemplateSelectEvent event = new TemplateSelectEvent();
                event.setSuccess(true);
                event.setCategoryWithChildCategoryDto((CategoryWithChildCategoryDto) listView.getAdapter().getItem(pos));
                eventBus.post(event);
            }
        });
        return rootView;
    }


}
