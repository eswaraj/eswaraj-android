package com.next.eswaraj.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseFragment;
import com.next.eswaraj.events.GetLocationComplaintCountersEvent;
import com.next.eswaraj.events.GetLocationComplaintsEvent;
import com.next.eswaraj.events.GetLocationEvent;
import com.next.eswaraj.helpers.ComplaintFilterHelper;
import com.next.eswaraj.helpers.GoogleAnalyticsTracker;
import com.next.eswaraj.middleware.MiddlewareServiceImpl;
import com.next.eswaraj.models.ComplaintCounter;
import com.next.eswaraj.models.ComplaintDto;
import com.next.eswaraj.models.ComplaintFilter;
import com.next.eswaraj.widgets.CustomNetworkImageView;
import com.next.eswaraj.widgets.CustomProgressDialog;
import com.next.eswaraj.widgets.CustomScrollView;
import com.eswaraj.web.dto.LocationDto;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class ConstituencyFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    GoogleAnalyticsTracker googleAnalyticsTracker;

    private View headerView;
    private View footerView;
    private ImageView mapButton;
    private ImageView listButton;
    private ImageView analyticsButton;
    private ImageView infoButton;
    private ImageView mapButton2;
    private ImageView listButton2;
    private ImageView analyticsButton2;
    private ImageView infoButton2;
    private TextView mcName;
    private CustomNetworkImageView mcPhoto;
    private TextView mcName2;
    private CustomNetworkImageView mcPhoto2;
    private FrameLayout headerContainer;
    private FrameLayout fragmentContainer;
    private CustomScrollView mcScrollView;
    private TextView mcIssueCount;
    private Button mcShowMore;

    private ComplaintListFragment complaintListFragment;
    private AnalyticsFragment analyticsFragment;
    private ComplaintsMapFragment complaintsMapFragment;
    private ConstituencyInfoFragment constituencyInfoFragment;

    private View.OnClickListener listClickListener;
    private View.OnClickListener mapClickListener;
    private View.OnClickListener analyticsClickListener;
    private View.OnClickListener infoClickListener;

    private CustomProgressDialog pDialog;
    private List<ComplaintDto> currentComplaintDtoList;
    private ComplaintFilter complaintFilter;

    private Boolean locationDtoAvailable = true;
    private Boolean markersAdded = false;
    private Long totalComplaints = 0L;

    private LocationDto locationDto;
    private List<ComplaintDto> complaintDtoList;
    private List<ComplaintCounter> complaintCounters;

    public ConstituencyFragment() {
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

        if(savedInstanceState == null) {
            getChildFragmentManager().beginTransaction().add(R.id.cFragmentContainer, complaintListFragment).commit();
            getChildFragmentManager().beginTransaction().add(R.id.cFragmentContainer, analyticsFragment).commit();
            getChildFragmentManager().beginTransaction().add(R.id.cFragmentContainer, complaintsMapFragment).commit();
            getChildFragmentManager().beginTransaction().add(R.id.cFragmentContainer, constituencyInfoFragment).commit();
        }

        getChildFragmentManager().beginTransaction().hide(analyticsFragment).commit();
        getChildFragmentManager().beginTransaction().hide(complaintsMapFragment).commit();
        getChildFragmentManager().beginTransaction().hide(constituencyInfoFragment).commit();
        getChildFragmentManager().executePendingTransactions();

        pDialog = new CustomProgressDialog(getActivity(), false, true, "Fetching complaints ...");
        pDialog.show();

        if(locationDtoAvailable) {
            middlewareService.loadLocationComplaints(getActivity(), locationDto, 0, 20);
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
        View rootView = inflater.inflate(R.layout.fragment_constituency, container, false);
        headerContainer = (FrameLayout) rootView.findViewById(R.id.cHeaderContainer);
        fragmentContainer = (FrameLayout) rootView.findViewById(R.id.cFragmentContainer);
        //mcScrollView = (CustomScrollView) rootView.findViewById(R.id.cScrollView);
        headerView = getActivity().getLayoutInflater().inflate(R.layout.constituency_header, null);


        mapButton = (ImageView) headerView.findViewById(R.id.cShowMap);
        listButton = (ImageView) headerView.findViewById(R.id.cShowList);
        analyticsButton = (ImageView) headerView.findViewById(R.id.cShowAnalytics);
        infoButton = (ImageView) headerView.findViewById(R.id.cShowInfo);

        mapButton2 = (ImageView) rootView.findViewById(R.id.cShowMap);
        listButton2 = (ImageView) rootView.findViewById(R.id.cShowList);
        analyticsButton2 = (ImageView) rootView.findViewById(R.id.cShowAnalytics);
        infoButton2 = (ImageView) rootView.findViewById(R.id.cShowInfo);

        mcName = (TextView) headerView.findViewById(R.id.cName);
        mcPhoto = (CustomNetworkImageView) headerView.findViewById(R.id.cPhoto);

        mcName2 = (TextView) rootView.findViewById(R.id.cName);
        mcPhoto2 = (CustomNetworkImageView) rootView.findViewById(R.id.cPhoto);

        mcIssueCount = (TextView) rootView.findViewById(R.id.issue_issues);

        headerContainer.setVisibility(View.GONE);
        complaintListFragment.setHeader(headerView);

        mcShowMore = new Button(getActivity());
        mcShowMore.setText("Show more");
        mcShowMore.setBackgroundColor(Color.parseColor("#0099cc"));
        mcShowMore.setTextColor(Color.parseColor("#FFFFFF"));
        complaintListFragment.setFooter(mcShowMore);

        if(locationDto != null) {
            mcName.setText(locationDto.getName());
            mcPhoto.loadHeaderImage(locationDto.getMobileHeaderImageUrl(), locationDto.getId());
            mcName2.setText(locationDto.getName());
            mcPhoto2.loadHeaderImage(locationDto.getMobileHeaderImageUrl(), locationDto.getId());
            constituencyInfoFragment.setInfoData(locationDto);
        }

        listClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Constituency: Show List");
                headerContainer.setVisibility(View.GONE);
                //setFragmentContainerSize(600);
                complaintsMapFragment.setMapDisplayed(false);
                getChildFragmentManager().beginTransaction().show(complaintListFragment).commit();
                getChildFragmentManager().beginTransaction().hide(analyticsFragment).commit();
                getChildFragmentManager().beginTransaction().hide(complaintsMapFragment).commit();
                getChildFragmentManager().beginTransaction().hide(constituencyInfoFragment).commit();
                getChildFragmentManager().executePendingTransactions();
                //mcScrollView.removeInterceptScrollView(googleMapFragment.getView());
                //mcScrollView.addInterceptScrollView(complaintListFragment.getView());
                //mapDisplayed = false;
                //mcScrollView.smoothScrollTo(0, 0);

            }
        };

        mapClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Constituency: Show Map");
                headerContainer.setVisibility(View.VISIBLE);
                complaintsMapFragment.setMapDisplayed(true);
                //setFragmentContainerSize(350);
                getChildFragmentManager().beginTransaction().hide(complaintListFragment).commit();
                getChildFragmentManager().beginTransaction().hide(analyticsFragment).commit();
                getChildFragmentManager().beginTransaction().show(complaintsMapFragment).commit();
                getChildFragmentManager().beginTransaction().hide(constituencyInfoFragment).commit();
                getChildFragmentManager().executePendingTransactions();
                //mcScrollView.removeInterceptScrollView(complaintListFragment.getView());
                //mcScrollView.addInterceptScrollView(googleMapFragment.getView());
                //mapDisplayed = true;

                //Post it on UI thread so that it gets en-queued behind fragment transactions and gets executed only after layout has happened for map
                fragmentContainer.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!markersAdded) {
                            markersAdded = complaintsMapFragment.setComplaintsData(currentComplaintDtoList);
                        }
                    }
                });
                //mcScrollView.smoothScrollTo(0, 0);
            }

        };

        analyticsClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Constituency: Show Analytics");
                headerContainer.setVisibility(View.VISIBLE);
                //setFragmentContainerSize(null);
                complaintsMapFragment.setMapDisplayed(false);
                getChildFragmentManager().beginTransaction().hide(complaintListFragment).commit();
                getChildFragmentManager().beginTransaction().show(analyticsFragment).commit();
                getChildFragmentManager().beginTransaction().hide(complaintsMapFragment).commit();
                getChildFragmentManager().beginTransaction().hide(constituencyInfoFragment).commit();
                getChildFragmentManager().executePendingTransactions();
                //mcScrollView.removeInterceptScrollView(googleMapFragment.getView());
                //mcScrollView.removeInterceptScrollView(complaintListFragment.getView());
                //mapDisplayed = false;
                //mcScrollView.smoothScrollTo(0, 0);
            }
        };

        infoClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Constituency: Show Info");
                headerContainer.setVisibility(View.VISIBLE);
                //setFragmentContainerSize(null);
                complaintsMapFragment.setMapDisplayed(false);
                getChildFragmentManager().beginTransaction().hide(complaintListFragment).commit();
                getChildFragmentManager().beginTransaction().hide(analyticsFragment).commit();
                getChildFragmentManager().beginTransaction().hide(complaintsMapFragment).commit();
                getChildFragmentManager().beginTransaction().show(constituencyInfoFragment).commit();
                getChildFragmentManager().executePendingTransactions();
                //mcScrollView.removeInterceptScrollView(googleMapFragment.getView());
                //mcScrollView.removeInterceptScrollView(complaintListFragment.getView());
                //mapDisplayed = false;
                //mcScrollView.smoothScrollTo(0, 0);
            }
        };

        mcShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Constituency: Show More");
                middlewareService.loadLocationComplaints(getActivity(), locationDto, complaintDtoList.size(), 20);
                pDialog = new CustomProgressDialog(getActivity(), false, true, "Fetching more complaints ...");
                pDialog.show();
            }
        });

        listButton.setOnClickListener(listClickListener);
        mapButton.setOnClickListener(mapClickListener);
        analyticsButton.setOnClickListener(analyticsClickListener);
        infoButton.setOnClickListener(infoClickListener);

        listButton2.setOnClickListener(listClickListener);
        mapButton2.setOnClickListener(mapClickListener);
        analyticsButton2.setOnClickListener(analyticsClickListener);
        infoButton2.setOnClickListener(infoClickListener);

        return rootView;
    }

    private void setComplaintData(List<ComplaintDto> complaintDtoList) {
        currentComplaintDtoList = complaintDtoList;
        complaintListFragment.setData(currentComplaintDtoList);
        analyticsFragment.populateCountersAndCreateChart(currentComplaintDtoList);
        markersAdded = complaintsMapFragment.setComplaintsData(currentComplaintDtoList);
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
            middlewareService.loadLocationComplaints(getActivity(), locationDto, 0, 20);
            middlewareService.loadLocationComplaintCounters(getActivity(), locationDto);

            mcName.setText(locationDto.getName());
            mcPhoto.loadHeaderImage(locationDto.getMobileHeaderImageUrl(), locationDto.getId());
            mcName2.setText(locationDto.getName());
            mcPhoto2.loadHeaderImage(locationDto.getMobileHeaderImageUrl(), locationDto.getId());
            constituencyInfoFragment.setInfoData(locationDto);
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch constituency details. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

    public void onEventMainThread(GetLocationComplaintsEvent event) {
        if(event.getSuccess()) {
            if(complaintDtoList == null) {
                complaintDtoList = event.getComplaintDtoList();
            }
            else {
                for(ComplaintDto complaintDto : event.getComplaintDtoList()) {
                    complaintDtoList.add(complaintDto);
                }
            }
            //setComplaintData(complaintDtoList);
            setFilter(complaintFilter);
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch constituency complaints. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
        pDialog.dismiss();
    }

    public void onEventMainThread(GetLocationComplaintCountersEvent event) {
        if(event.getSuccess()) {
            complaintCounters = event.getComplaintCounters();
            totalComplaints = 0L;
            for(ComplaintCounter complaintCounter : complaintCounters) {
                totalComplaints += complaintCounter.getCount();
            }
            mcIssueCount.setText(totalComplaints + " Issues");
            analyticsFragment.createChart(complaintCounters);
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch constituency complaint counters. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

}
