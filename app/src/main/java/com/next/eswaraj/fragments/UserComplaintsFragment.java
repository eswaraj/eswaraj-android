package com.next.eswaraj.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseFragment;
import com.next.eswaraj.events.GetUserComplaintsEvent;
import com.next.eswaraj.helpers.ComplaintFilterHelper;
import com.next.eswaraj.helpers.GoogleAnalyticsTracker;
import com.next.eswaraj.middleware.MiddlewareServiceImpl;
import com.next.eswaraj.models.ComplaintDto;
import com.next.eswaraj.models.ComplaintFilter;
import com.next.eswaraj.util.UserSessionUtil;
import com.next.eswaraj.widgets.CustomProgressDialog;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class UserComplaintsFragment extends BaseFragment {

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

    private CustomProgressDialog pDialog;
    private List<ComplaintDto> complaintDtoList;
    private List<ComplaintDto> currentComplaintDtoList;
    private ComplaintFilter complaintFilter;
    private ComplaintsPagerAdapter complaintsPagerAdapter;

    public UserComplaintsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pDialog = new CustomProgressDialog(getActivity(), false, true, "Fetching your complaints ...");
        pDialog.show();

        eventBus.register(this);

        middlewareService.loadUserComplaints(getActivity(), userSession.getUser(), true);
    }

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_complaints_pager, container, false);
        complaintListFragment = new ComplaintListFragment();
        analyticsFragment = new AnalyticsFragment();
        complaintsMapFragment = new ComplaintsMapFragment();

        ViewPager pager = (ViewPager) rootView.findViewById(R.id.viewPager);
        complaintsPagerAdapter = new ComplaintsPagerAdapter(getChildFragmentManager(), complaintListFragment, analyticsFragment, complaintsMapFragment);
        pager.setAdapter(complaintsPagerAdapter);
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
                complaintsPagerAdapter.setData(currentComplaintDtoList);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return rootView;
    }

    private void setComplaintData(List<ComplaintDto> complaintDtos) {
        currentComplaintDtoList = complaintDtos;
        complaintsPagerAdapter.setData(complaintDtos);
    }

    public void markComplaintClosed(Long id) {
        complaintListFragment.markComplaintClosed(id);
    }

    public void setFilter(ComplaintFilter filter) {
        complaintFilter = filter;
        setComplaintData(ComplaintFilterHelper.filter(complaintDtoList, filter));
    }

    public void onEventMainThread(GetUserComplaintsEvent event) {
        if(event.getSuccess()) {
            complaintDtoList = event.getComplaintDtoList();
            setFilter(complaintFilter);
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch user complaints. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
        pDialog.dismiss();
    }





    private class ComplaintsPagerAdapter extends FragmentPagerAdapter {

        private ComplaintListFragment complaintListFragment;
        private AnalyticsFragment analyticsFragment;
        private ComplaintsMapFragment complaintsMapFragment;

        private Boolean listAdded = false;
        private Boolean analyticsAdded = false;
        private Boolean mapAdded = false;

        public ComplaintsPagerAdapter(FragmentManager fm, ComplaintListFragment complaintListFragment, AnalyticsFragment analyticsFragment, ComplaintsMapFragment complaintsMapFragment) {
            super(fm);
            this.complaintListFragment = complaintListFragment;
            this.analyticsFragment = analyticsFragment;
            this.complaintsMapFragment = complaintsMapFragment;

        }

        @Override
        public Fragment getItem(int pos) {
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
                default: return complaintListFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return "List";
                case 1: return "Analytics";
                case 2: return "Map";
                default: return "";
            }
        }

        public void setData(List<ComplaintDto> complaintDtos) {
            if(listAdded) {
                complaintListFragment.setData(complaintDtos);
            }
            if(analyticsAdded) {
                analyticsFragment.populateCountersAndCreateChart(complaintDtos);
            }
            if(mapAdded) {
                complaintsMapFragment.setComplaintsData(complaintDtos);
            }
        }
    }
}
