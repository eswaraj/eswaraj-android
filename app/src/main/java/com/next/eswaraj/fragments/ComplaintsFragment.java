package com.next.eswaraj.fragments;


import android.os.Bundle;
import android.util.TypedValue;
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
import com.next.eswaraj.events.GetUserComplaintsEvent;
import com.next.eswaraj.helpers.ComplaintFilterHelper;
import com.next.eswaraj.helpers.GoogleAnalyticsTracker;
import com.next.eswaraj.middleware.MiddlewareServiceImpl;
import com.next.eswaraj.models.ComplaintDto;
import com.next.eswaraj.models.ComplaintFilter;
import com.next.eswaraj.util.GenericUtil;
import com.next.eswaraj.util.GlobalSessionUtil;
import com.next.eswaraj.util.UserSessionUtil;
import com.next.eswaraj.widgets.CustomNetworkImageView;
import com.next.eswaraj.widgets.CustomProgressDialog;
import com.next.eswaraj.widgets.CustomScrollView;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class ComplaintsFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    UserSessionUtil userSession;
    @Inject
    GlobalSessionUtil globalSession;
    @Inject
    GoogleAnalyticsTracker googleAnalyticsTracker;

    private View headerView;
    private View footerView;
    private ImageView mapButton;
    private ImageView listButton;
    private ImageView analyticsButton;
    private ImageView mapButton2;
    private ImageView listButton2;
    private ImageView analyticsButton2;
    private TextView mcName;
    private CustomNetworkImageView mcPhoto;
    private TextView mcUserDetails;
    private TextView mcName2;
    private CustomNetworkImageView mcPhoto2;
    private TextView mcUserDetails2;
    private FrameLayout headerContainer;
    private FrameLayout fragmentContainer;
    private CustomScrollView mcScrollView;

    private ComplaintListFragment complaintListFragment;
    private AnalyticsFragment analyticsFragment;
    private ComplaintsMapFragment complaintsMapFragment;

    private View.OnClickListener listClickListener;
    private View.OnClickListener mapClickListener;
    private View.OnClickListener analyticsClickListener;
    private View.OnClickListener filterClickListener;

    private CustomProgressDialog pDialog;
    private List<ComplaintDto> complaintDtoList;
    private List<ComplaintDto> currentComplaintDtoList;
    private ComplaintFilter complaintFilter;

    private Boolean mapReady = false;
    private Boolean mapDisplayed = false;
    private Boolean markersAdded = false;

    public ComplaintsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        complaintListFragment = new ComplaintListFragment();
        analyticsFragment = new AnalyticsFragment();
        complaintsMapFragment = new ComplaintsMapFragment();

        if(savedInstanceState == null) {
            getChildFragmentManager().beginTransaction().add(R.id.mcFragmentContainer, complaintListFragment).commit();
            getChildFragmentManager().beginTransaction().add(R.id.mcFragmentContainer, analyticsFragment).commit();
            getChildFragmentManager().beginTransaction().add(R.id.mcFragmentContainer, complaintsMapFragment).commit();
        }

        getChildFragmentManager().beginTransaction().hide(analyticsFragment).commit();
        getChildFragmentManager().beginTransaction().hide(complaintsMapFragment).commit();
        getChildFragmentManager().executePendingTransactions();

        mapDisplayed = false;

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
        View rootView = inflater.inflate(R.layout.fragment_complaints, container, false);
        headerContainer = (FrameLayout) rootView.findViewById(R.id.mcHeaderContainer);
        fragmentContainer = (FrameLayout) rootView.findViewById(R.id.mcFragmentContainer);
        //mcScrollView = (CustomScrollView) rootView.findViewById(R.id.mcScrollView);
        headerView = getActivity().getLayoutInflater().inflate(R.layout.my_complaints_header, null);


        mapButton = (ImageView) headerView.findViewById(R.id.mcShowMap);
        listButton = (ImageView) headerView.findViewById(R.id.mcShowList);
        analyticsButton = (ImageView) headerView.findViewById(R.id.mcShowAnalytics);

        mapButton2 = (ImageView) rootView.findViewById(R.id.mcShowMap);
        listButton2 = (ImageView) rootView.findViewById(R.id.mcShowList);
        analyticsButton2 = (ImageView) rootView.findViewById(R.id.mcShowAnalytics);

        mcName = (TextView) headerView.findViewById(R.id.mcName);
        mcPhoto = (CustomNetworkImageView) headerView.findViewById(R.id.mcPhoto);
        mcUserDetails = (TextView) headerView.findViewById(R.id.mcUserDetails);

        mcName2 = (TextView) rootView.findViewById(R.id.mcName);
        mcPhoto2 = (CustomNetworkImageView) rootView.findViewById(R.id.mcPhoto);
        mcUserDetails2 = (TextView) rootView.findViewById(R.id.mcUserDetails);

        headerContainer.setVisibility(View.GONE);
        complaintListFragment.setHeader(headerView);
        //setFragmentContainerSize(null);

        if(userSession.getUser().getPerson().getDob() != null) {
            mcUserDetails.setText(GenericUtil.getAge(userSession.getUser().getPerson().getDob()) + " Years, " + userSession.getUser().getPerson().getGender());
        }
        mcName.setText(userSession.getUser().getPerson().getName());
        mcPhoto.loadProfileImage(userSession.getProfilePhoto(), userSession.getUser().getPerson().getId());

        if(userSession.getUser().getPerson().getDob() != null) {
            mcUserDetails2.setText(GenericUtil.getAge(userSession.getUser().getPerson().getDob()) + " Years, " + userSession.getUser().getPerson().getGender());
        }
        mcName2.setText(userSession.getUser().getPerson().getName());
        mcPhoto2.loadProfileImage(userSession.getProfilePhoto(), userSession.getUser().getPerson().getId());

        listClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "My Complaints: Show List");
                headerContainer.setVisibility(View.GONE);
                //setFragmentContainerSize(600);
                complaintsMapFragment.setMapDisplayed(false);
                getChildFragmentManager().beginTransaction().show(complaintListFragment).commit();
                getChildFragmentManager().beginTransaction().hide(analyticsFragment).commit();
                getChildFragmentManager().beginTransaction().hide(complaintsMapFragment).commit();
                getChildFragmentManager().executePendingTransactions();
                //mcScrollView.removeInterceptScrollView(googleMapFragment.getView());
                //mcScrollView.addInterceptScrollView(complaintListFragment.getView());
                mapDisplayed = false;
                //mcScrollView.smoothScrollTo(0, 0);

            }
        };

        mapClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "My Complaints: Show Map");
                headerContainer.setVisibility(View.VISIBLE);
                //setFragmentContainerSize(350);
                complaintsMapFragment.setMapDisplayed(true);
                getChildFragmentManager().beginTransaction().hide(complaintListFragment).commit();
                getChildFragmentManager().beginTransaction().hide(analyticsFragment).commit();
                getChildFragmentManager().beginTransaction().show(complaintsMapFragment).commit();
                getChildFragmentManager().executePendingTransactions();
                //mcScrollView.removeInterceptScrollView(complaintListFragment.getView());
                //mcScrollView.addInterceptScrollView(googleMapFragment.getView());
                mapDisplayed = true;

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
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "My Complaints: Show Analytics");
                headerContainer.setVisibility(View.VISIBLE);
                //setFragmentContainerSize(null);
                complaintsMapFragment.setMapDisplayed(false);
                getChildFragmentManager().beginTransaction().hide(complaintListFragment).commit();
                getChildFragmentManager().beginTransaction().show(analyticsFragment).commit();
                getChildFragmentManager().beginTransaction().hide(complaintsMapFragment).commit();
                getChildFragmentManager().executePendingTransactions();
                //mcScrollView.removeInterceptScrollView(googleMapFragment.getView());
                //mcScrollView.removeInterceptScrollView(complaintListFragment.getView());
                mapDisplayed = false;
                //mcScrollView.smoothScrollTo(0, 0);
            }
        };

        listButton.setOnClickListener(listClickListener);
        mapButton.setOnClickListener(mapClickListener);
        analyticsButton.setOnClickListener(analyticsClickListener);

        listButton2.setOnClickListener(listClickListener);
        mapButton2.setOnClickListener(mapClickListener);
        analyticsButton2.setOnClickListener(analyticsClickListener);

        return rootView;
    }

    private void setComplaintData(List<ComplaintDto> complaintDtos) {
        currentComplaintDtoList = complaintDtos;
        complaintListFragment.setData(currentComplaintDtoList);
        analyticsFragment.populateCountersAndCreateChart(currentComplaintDtoList);
        markersAdded = complaintsMapFragment.setComplaintsData(currentComplaintDtoList);
        //mcScrollView.smoothScrollTo(0, 0);
    }

    private void setFragmentContainerSize(Integer height) {
        ViewGroup.LayoutParams params = fragmentContainer.getLayoutParams();
        if(height == null) {
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            return;
        }
        int heightPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics());
        params.height = heightPixels;
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
            //setComplaintData(complaintDtoList);
            setFilter(complaintFilter);
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch user complaints. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
        pDialog.dismiss();
    }
}
