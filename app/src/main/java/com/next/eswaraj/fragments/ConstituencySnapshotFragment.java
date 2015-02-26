package com.next.eswaraj.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseFragment;
import com.next.eswaraj.events.GetLocationComplaintCountersEvent;
import com.next.eswaraj.events.GetLocationComplaintsEvent;
import com.next.eswaraj.events.GetLocationEvent;
import com.next.eswaraj.events.ShowConstituencyComplaintsEvent;
import com.next.eswaraj.events.ShowConstituencyTimelineEvent;
import com.next.eswaraj.events.ShowLeadersForLocationEvent;
import com.next.eswaraj.helpers.ComplaintFilterHelper;
import com.next.eswaraj.helpers.GoogleAnalyticsTracker;
import com.next.eswaraj.middleware.MiddlewareServiceImpl;
import com.next.eswaraj.models.ComplaintCounter;
import com.next.eswaraj.models.ComplaintDto;
import com.next.eswaraj.models.ComplaintFilter;
import com.next.eswaraj.util.UserSessionUtil;
import com.next.eswaraj.widgets.CustomNetworkImageView;
import com.next.eswaraj.widgets.CustomProgressDialog;
import com.eswaraj.web.dto.LocationDto;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class ConstituencySnapshotFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    UserSessionUtil userSession;
    @Inject
    GoogleAnalyticsTracker googleAnalyticsTracker;

    private ComplaintListFragment complaintListFragment;
    private CustomProgressDialog pDialog;
    private List<ComplaintDto> complaintDtoList;
    private List<ComplaintDto> currentComplaintDtoList;
    private Button mcShowMore;
    private Boolean locationDtoAvailable = true;
    private LocationDto locationDto;
    private ComplaintFilter complaintFilter;
    private List<ComplaintCounter> complaintCounters;
    private View headerView;
    private TextView count;
    private CustomNetworkImageView header;
    private TextView name;
    private Long complaintCount = 0L;
    private Button leader;
    private Button smart;
    private Button timeline;

    private Integer requestCount = 20;
    private Boolean isStopped = false;

    public ConstituencySnapshotFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);
        complaintListFragment = new ComplaintListFragment();
        if(savedInstanceState == null) {
            getChildFragmentManager().beginTransaction().add(R.id.csListFragment, complaintListFragment).commit();
        }
        locationDto = (LocationDto) getActivity().getIntent().getSerializableExtra("LOCATION");
        if(locationDto == null) {
            locationDtoAvailable = false;
            middlewareService.loadLocation(getActivity(), getActivity().getIntent().getLongExtra("ID", -1));
        }
        pDialog = new CustomProgressDialog(getActivity(), false, true, "Fetching complaints ...");
        pDialog.show();

        if(locationDtoAvailable) {
            getActivity().setTitle(locationDto.getName());
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
    public void onResume() {
        super.onResume();
        isStopped = false;
    }

    @Override
    public void onPause() {
        isStopped = true;
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_constituency_snapshot, container, false);

        mcShowMore = new Button(getActivity());
        mcShowMore.setText("Show More");
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mcShowMore.setBackgroundColor(Color.parseColor("#0099cc"));
        } else {
            mcShowMore.setBackgroundResource( R.drawable.blue_darkblue_highlight );
        }
        mcShowMore.setTextColor(Color.parseColor("#FFFFFF"));
        headerView = getActivity().getLayoutInflater().inflate(R.layout.header_constituency_header, null);
        complaintListFragment.setFooter(mcShowMore);
        complaintListFragment.setHeader(headerView);

        name = (TextView) headerView.findViewById(R.id.cName);
        count = (TextView) headerView.findViewById(R.id.cCount);
        header = (CustomNetworkImageView) headerView.findViewById(R.id.cHeader);
        leader = (Button) headerView.findViewById(R.id.cLeader);
        smart = (Button) headerView.findViewById(R.id.cSmart);
        timeline = (Button) headerView.findViewById(R.id.cTimeline);

        if(locationDto != null) {
            name.setText(locationDto.getName());
            if(locationDto.getMobileHeaderImageUrl() != null && !locationDto.getMobileHeaderImageUrl().equals("")) {
                Picasso.with(getActivity()).load(locationDto.getMobileHeaderImageUrl()).into(header);
            }
            else {
                header.setImageDrawable(getResources().getDrawable(R.drawable.constituency_default_header));
            }
        }
        count.setText(complaintCount.toString() + " issues");

        mcShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Constituency: Show More");
                middlewareService.loadLocationComplaints(getActivity(), locationDto, complaintDtoList.size(), requestCount);
                pDialog = new CustomProgressDialog(getActivity(), false, true, "Fetching more complaints ...");
                pDialog.show();
            }
        });

        leader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowLeadersForLocationEvent event = new ShowLeadersForLocationEvent();
                event.setLocationDto(locationDto);
                eventBus.post(event);
            }
        });
        smart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowConstituencyComplaintsEvent event = new ShowConstituencyComplaintsEvent();
                event.setSuccess(true);
                event.setLocationDto(locationDto);
                eventBus.post(event);
            }
        });
        timeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowConstituencyTimelineEvent event = new ShowConstituencyTimelineEvent();
                event.setSuccess(true);
                event.setLocationDto(locationDto);
                eventBus.post(event);
            }
        });
        return rootView;
    }

    private void setComplaintData(List<ComplaintDto> complaintDtos) {
        currentComplaintDtoList = complaintDtos;
        complaintListFragment.setData(currentComplaintDtoList);
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
            name.setText(locationDto.getName());
            if(locationDto.getMobileHeaderImageUrl() != null && !locationDto.getMobileHeaderImageUrl().equals("")) {
                Picasso.with(getActivity()).load(locationDto.getMobileHeaderImageUrl()).into(header);
            }
            else {
                header.setImageDrawable(getResources().getDrawable(R.drawable.constituency_default_header));
            }
            getActivity().setTitle(locationDto.getName());
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch constituency details. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

    public void onEventMainThread(GetLocationComplaintsEvent event) {
        if(isStopped) {
            return;
        }
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
        if(isStopped) {
            return;
        }
        if(event.getSuccess()) {
            complaintCounters = event.getComplaintCounters();
            complaintCount = 0L;
            for(ComplaintCounter complaintCounter : complaintCounters) {
                complaintCount += complaintCounter.getCount();
            }
            count.setText(complaintCount.toString() + " issues");
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch constituency complaint counters. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }
}
