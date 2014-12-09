package com.eswaraj.app.eswaraj.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.activities.SplashActivity;
import com.eswaraj.app.eswaraj.adapters.AmenityListAdapter;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesImagesEvent;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class AmenitiesFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;

    private GridView gvAmenityList;
    private List<CategoryWithChildCategoryDto> categoryList;

    public static AmenitiesFragment newInstance() {
        AmenitiesFragment fragment = new AmenitiesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AmenitiesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        eventBus.register(this);
        middlewareService.loadCategoriesData(getActivity());
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_amenities, container, false);
        gvAmenityList = (GridView) rootView.findViewById(R.id.gvAmenityList);
        gvAmenityList.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                //TODO: Fix the activity name below. It should be the SelectTemplateActivity.class
                Intent i = new Intent(getActivity(), SplashActivity.class);
                i.putExtra("AMENITY", (Serializable) gvAmenityList.getAdapter().getItem(pos));
                startActivity(i);
            }
        });
        return rootView;
    }

    public void onEventMainThread(GetCategoriesDataEvent event) {
        if(event.getSuccess()) {
            //Launch image download now. Always launch with dontGetFromCache=true
            categoryList = event.getCategoryList();
            middlewareService.loadCategoriesImages(getActivity(), event.getCategoryList(), true);
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch categories from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
            //Show retry button which will re-trigger the request.
        }
    }

    public void onEventMainThread(GetCategoriesImagesEvent event) {
        if(event.getSuccess()) {
            //All needed data is available now. Set the adapter for gridview
            AmenityListAdapter amenityListAdapter = new AmenityListAdapter(getActivity(), R.layout.item_amenity_list, categoryList);
            gvAmenityList.setAdapter(amenityListAdapter);
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch categories images from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
            //Show retry button which will re-trigger the request.
        }
    }

}
