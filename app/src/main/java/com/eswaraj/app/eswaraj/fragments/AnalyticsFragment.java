package com.eswaraj.app.eswaraj.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.adapters.AmenityListAdapter;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.GetLocationComplaintCountersEvent;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.ComplaintCounter;
import com.eswaraj.app.eswaraj.models.ComplaintDto;
import com.eswaraj.app.eswaraj.util.GlobalSessionUtil;
import com.eswaraj.app.eswaraj.widgets.PieChartView;
import com.eswaraj.web.dto.CategoryDto;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.web.dto.LocationDto;

import org.achartengine.GraphicalView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class AnalyticsFragment extends BaseFragment {

    @Inject
    GlobalSessionUtil globalSession;
    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;

    private FrameLayout mcChartContainer;
    private GridView mcAmenityList;

    public AnalyticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_analytics, container, false);
        mcChartContainer = (FrameLayout) rootView.findViewById(R.id.mcChartContainer);
        mcAmenityList = (GridView) rootView.findViewById(R.id.mcAmenityList);
        return rootView;
    }

    public void populateCountersAndCreateChart(List<ComplaintDto> complaintDtoList) {
        ArrayList<ComplaintCounter> complaintCounters = new ArrayList<ComplaintCounter>();
        for(CategoryWithChildCategoryDto categoryDto : globalSession.getCategoryDtoList()) {
            ComplaintCounter complaintCounter = new ComplaintCounter();
            complaintCounter.setId(categoryDto.getId());
            complaintCounter.setName(categoryDto.getName());
            complaintCounter.setCount(0L);
            complaintCounters.add(complaintCounter);
        }
        for(ComplaintDto complaintDto : complaintDtoList) {
            for(ComplaintCounter complaintCounter : complaintCounters) {
                for(CategoryDto categoryDto : complaintDto.getCategories()) {
                    if (categoryDto.getId().equals(complaintCounter.getId())) {
                        complaintCounter.setCount(complaintCounter.getCount() + 1);
                    }
                }
            }
        }
        createChart(complaintCounters);
    }

    public void createChart(List<ComplaintCounter> complaintCounters) {
        GraphicalView chartView = PieChartView.getNewInstance(getActivity(), complaintCounters, globalSession.getCategoryDtoList(), globalSession.getColorMap());
        mcChartContainer.removeAllViews();
        mcChartContainer.addView(chartView);
        AmenityListAdapter amenityListAdapter = new AmenityListAdapter(getActivity(), R.layout.item_amenity_list, globalSession.getCategoryDtoList(), complaintCounters, globalSession.getColorMap());
        mcAmenityList.setAdapter(amenityListAdapter);
    }

    public void loadCountersForLocation(LocationDto locationDto) {
        middlewareService.loadLocationComplaintCounters(getActivity(), locationDto);
    }

    public void onEventMainThread(GetLocationComplaintCountersEvent event) {
        if(event.getSuccess()) {
            List<ComplaintCounter> complaintCounters = event.getComplaintCounters();
            createChart(complaintCounters);
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch constituency complaint counters. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }
}
