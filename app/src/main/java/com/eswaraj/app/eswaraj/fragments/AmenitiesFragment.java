package com.eswaraj.app.eswaraj.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesImagesEvent;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class AmenitiesFragment extends Fragment {

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
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Launch next activity here and pass the selected amenity data in Extras
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
            //All needed data is available now. Set the adapter for gridview now using categoryList
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch categories images from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
            //Show retry button which will re-trigger the request.
        }
    }

}
