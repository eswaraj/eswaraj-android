package com.next.eswaraj.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

import com.next.eswaraj.R;
import com.next.eswaraj.adapters.AmenityListAdapter;
import com.next.eswaraj.base.BaseFragment;
import com.next.eswaraj.events.GetLocationComplaintCountersEvent;
import com.next.eswaraj.middleware.MiddlewareServiceImpl;
import com.next.eswaraj.models.ComplaintCounter;
import com.next.eswaraj.models.ComplaintDto;
import com.next.eswaraj.util.GlobalSessionUtil;
import com.next.eswaraj.widgets.CustomScrollView;
import com.next.eswaraj.widgets.PieChartView;
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
    private CustomScrollView mcScrollView;

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
        mcScrollView = (CustomScrollView) rootView.findViewById(R.id.mcScroll);

        mcScrollView.scrollTo(0,0);
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
            mcScrollView.scrollTo(0,0);
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch constituency complaint counters. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }
}
