package com.eswaraj.app.eswaraj.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.GetLocationComplaintCountersEvent;
import com.eswaraj.app.eswaraj.events.GetLocationComplaintsEvent;
import com.eswaraj.app.eswaraj.events.GetLocationEvent;
import com.eswaraj.app.eswaraj.helpers.ComplaintFilterHelper;
import com.eswaraj.app.eswaraj.helpers.GoogleAnalyticsTracker;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.ComplaintCounter;
import com.eswaraj.app.eswaraj.models.ComplaintDto;
import com.eswaraj.app.eswaraj.models.ComplaintFilter;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.eswaraj.app.eswaraj.widgets.CustomProgressDialog;
import com.eswaraj.web.dto.LocationDto;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class ConstituencyComplaintsFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    UserSessionUtil userSession;
    @Inject
    GoogleAnalyticsTracker googleAnalyticsTracker;

    private ComplaintListFragment complaintListFragment;
    private AnalyticsFragment analyticsFragment;
    private ComplaintsMapFragment complaintsMapFragment;
    private ConstituencyInfoFragment constituencyInfoFragment;

    private CustomProgressDialog pDialog;
    private List<ComplaintDto> complaintDtoList;
    private List<ComplaintDto> currentComplaintDtoList;
    private List<ComplaintCounter> complaintCounters;
    private ComplaintFilter complaintFilter;
    private LocationDto locationDto;
    private Button mcShowMore;
    private ConstituencyPagerAdapter constituencyPagerAdapter;

    private Boolean locationDtoAvailable = true;
    private Integer requestCount = 20;

    private int visibleThreshold = 5;
    private int previousTotal = 0;
    private boolean loading = true;
    private ListView.OnScrollListener infiniteScrollListener;

    public ConstituencyComplaintsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);

        locationDto = (LocationDto) getActivity().getIntent().getSerializableExtra("LOCATION");
        if(locationDto == null) {
            locationDtoAvailable = false;
            middlewareService.loadLocation(getActivity(), getActivity().getIntent().getLongExtra("ID", -1));
        }

        complaintListFragment = new ComplaintListFragment();
        analyticsFragment = new AnalyticsFragment();
        complaintsMapFragment = new ComplaintsMapFragment();
        constituencyInfoFragment = new ConstituencyInfoFragment();

        pDialog = new CustomProgressDialog(getActivity(), false, true, "Fetching complaints ...");
        pDialog.show();

        if(locationDtoAvailable) {
            middlewareService.loadLocationComplaints(getActivity(), locationDto, 0, requestCount);
            middlewareService.loadLocationComplaintCounters(getActivity(), locationDto);
        }
    }

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_constituency_pager, container, false);

        complaintListFragment = new ComplaintListFragment();
        analyticsFragment = new AnalyticsFragment();
        complaintsMapFragment = new ComplaintsMapFragment();
        constituencyInfoFragment = new ConstituencyInfoFragment();

        mcShowMore = new Button(getActivity());
        mcShowMore.setText("Show more");
        complaintListFragment.setFooter(mcShowMore);
        mcShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Constituency: Show More");
                middlewareService.loadLocationComplaints(getActivity(), locationDto, complaintDtoList.size(), requestCount);
                pDialog = new CustomProgressDialog(getActivity(), false, true, "Fetching more complaints ...");
                pDialog.show();
            }
        });
        infiniteScrollListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Constituency: Show More");
                    middlewareService.loadLocationComplaints(getActivity(), locationDto, complaintDtoList.size(), requestCount);
                    loading = true;
                }
            }
        };
        //complaintListFragment.setOnScrollListener(infiniteScrollListener);

        ViewPager pager = (ViewPager) rootView.findViewById(R.id.viewPager);
        constituencyPagerAdapter = new ConstituencyPagerAdapter(getChildFragmentManager(), complaintListFragment, analyticsFragment, complaintsMapFragment, constituencyInfoFragment);
        pager.setAdapter(constituencyPagerAdapter);
        pager.setOffscreenPageLimit(3);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
        tabs.setShouldExpand(true);
        //tabs.setTabPaddingLeftRight(100);
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position != 2) {
                    complaintsMapFragment.setMapDisplayed(false);
                }
                else {
                    complaintsMapFragment.setMapDisplayed(true);
                }
                constituencyPagerAdapter.setData(locationDto, currentComplaintDtoList, complaintCounters);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return rootView;
    }

    private void setComplaintData(List<ComplaintDto> complaintDtos) {
        currentComplaintDtoList = complaintDtos;
        constituencyPagerAdapter.setData(locationDto, currentComplaintDtoList, complaintCounters);
    }

    public void markComplaintClosed(Long id) {
        complaintListFragment.markComplaintClosed(id);
    }

    public void setFilter(ComplaintFilter filter) {
        complaintFilter = filter;
        setComplaintData(ComplaintFilterHelper.filter(complaintDtoList, filter));
    }

    public void onEventMainThread(GetLocationEvent event) {
        if(event.getSuccess()) {
            locationDto = event.getLocationDto();
            middlewareService.loadLocationComplaints(getActivity(), locationDto, 0, requestCount);
            middlewareService.loadLocationComplaintCounters(getActivity(), locationDto);
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch constituency details. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

    public void onEventMainThread(GetLocationComplaintsEvent event) {
        if(event.getSuccess()) {
            int old = 0;
            if(currentComplaintDtoList != null) {
                old = currentComplaintDtoList.size();
            }
            if(complaintDtoList == null) {
                complaintDtoList = event.getComplaintDtoList();
            }
            else {
                for(ComplaintDto complaintDto : event.getComplaintDtoList()) {
                    complaintDtoList.add(complaintDto);
                }
            }
            setComplaintData(ComplaintFilterHelper.filter(complaintDtoList, complaintFilter));
            if(old != 0) {
                complaintListFragment.scrollTo(old - 1);
            }
            if(complaintDtoList.size() < requestCount) {
                mcShowMore.setVisibility(View.GONE);
            }
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch constituency complaints. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
        pDialog.dismiss();
    }

    public void onEventMainThread(GetLocationComplaintCountersEvent event) {
        if(event.getSuccess()) {
            complaintCounters = event.getComplaintCounters();
            constituencyPagerAdapter.setData(locationDto, currentComplaintDtoList, complaintCounters);
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch constituency complaint counters. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }




    private class ConstituencyPagerAdapter extends FragmentPagerAdapter {

        private ComplaintListFragment complaintListFragment;
        private AnalyticsFragment analyticsFragment;
        private ComplaintsMapFragment complaintsMapFragment;
        private ConstituencyInfoFragment constituencyInfoFragment;

        private Boolean listAdded = false;
        private Boolean analyticsAdded = false;
        private Boolean mapAdded = false;
        private Boolean infoAdded = false;

        public ConstituencyPagerAdapter(FragmentManager fm, ComplaintListFragment complaintListFragment, AnalyticsFragment analyticsFragment, ComplaintsMapFragment complaintsMapFragment, ConstituencyInfoFragment constituencyInfoFragment) {
            super(fm);
            this.complaintListFragment = complaintListFragment;
            this.analyticsFragment = analyticsFragment;
            this.complaintsMapFragment = complaintsMapFragment;
            this.constituencyInfoFragment = constituencyInfoFragment;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int pos) {
            switch(pos) {
                case 0: {
                    listAdded = true;
                    return complaintListFragment;
                }
                case 1: {
                    analyticsAdded = true;
                    return analyticsFragment;
                }
                case 2: {
                    mapAdded = true;
                    return complaintsMapFragment;
                }
                case 3: {
                    infoAdded = true;
                    return constituencyInfoFragment;
                }
                default: return complaintListFragment;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return "List";
                case 1: return "Analytics";
                case 2: return "Map";
                case 3: return "Info";
                default: return "";
            }
        }

        public void setData(LocationDto locationDto, List<ComplaintDto> complaintDtos, List<ComplaintCounter> complaintCounterList) {
            if(listAdded && complaintDtos != null) {
                complaintListFragment.setData(complaintDtos);
            }
            if(analyticsAdded && complaintCounterList != null) {
                analyticsFragment.createChart(complaintCounterList);
            }
            if(mapAdded && complaintDtos != null) {
                complaintsMapFragment.setComplaintsData(complaintDtos);
            }
            if(infoAdded && complaintDtos != null) {
                constituencyInfoFragment.setInfoData(locationDto);
            }
        }
    }
}
