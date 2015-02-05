package com.next.eswaraj.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.next.eswaraj.R;
import com.next.eswaraj.adapters.TemplateListAdapter;
import com.next.eswaraj.base.BaseFragment;
import com.next.eswaraj.events.TemplateSelectEvent;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

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

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                TemplateSelectEvent event = new TemplateSelectEvent();
                event.setSuccess(true);
                event.setTemplate((CategoryWithChildCategoryDto) listView.getAdapter().getItem(pos));
                event.setAmenity(amenity);
                eventBus.post(event);
            }
        });
        return rootView;
    }


}
