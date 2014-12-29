package com.eswaraj.app.eswaraj.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.activities.SingleComplaintActivity;
import com.eswaraj.app.eswaraj.adapters.ComplaintListAdapter;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.ComplaintSelectedEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.GetUserComplaintsEvent;
import com.eswaraj.app.eswaraj.events.GetUserEvent;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.eswaraj.app.eswaraj.widgets.CustomProgressDialog;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.web.dto.ComplaintDto;
import com.eswaraj.web.dto.UserDto;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class MyComplaintsFragment extends BaseFragment implements OnMapReadyCallback {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    UserSessionUtil userSession;

    private GoogleMapFragment googleMapFragment;
    private List<ComplaintDto> complaintDtoList;

    private ListView mcListOpen;
    private ListView mcListClosed;
    private Button mapButton;
    private Button listButton;
    private CustomProgressDialog pDialog;
    private FrameLayout mcMapContainer;
    private ViewGroup mcListContainer;

    private Boolean mapDisplayed = false;
    private Boolean mapReady = false;
    private Boolean markersAdded = false;


    public MyComplaintsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleMapFragment = new GoogleMapFragment();

        googleMapFragment.setContext(this);

        if(savedInstanceState == null) {
            getChildFragmentManager().beginTransaction().add(R.id.mcMapContainer, googleMapFragment).commit();
        }
        getChildFragmentManager().beginTransaction().hide(googleMapFragment).commit();
        getChildFragmentManager().executePendingTransactions();
        mapDisplayed = false;

        pDialog = new CustomProgressDialog(getActivity(), false, true, "Fetching your complaints ...");
        pDialog.show();



        middlewareService.loadUserComplaints(getActivity(), userSession.getUser(), true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_complaints, container, false);
        mcListOpen = (ListView) rootView.findViewById(R.id.mcListOpen);
        mcListClosed = (ListView) rootView.findViewById(R.id.mcListClosed);
        mapButton = (Button) rootView.findViewById(R.id.mcShowMap);
        listButton = (Button) rootView.findViewById(R.id.mcShowList);
        mcMapContainer = (FrameLayout) rootView.findViewById(R.id.mcMapContainer);
        mcListContainer = (ViewGroup) rootView.findViewById(R.id.mcListContainer);

        mcListOpen.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ComplaintSelectedEvent event = new ComplaintSelectedEvent();
                event.setComplaintDto((ComplaintDto) mcListOpen.getAdapter().getItem(position));
                eventBus.post(event);
            }
        });

        mcListClosed.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ComplaintSelectedEvent event = new ComplaintSelectedEvent();
                event.setComplaintDto((ComplaintDto) mcListClosed.getAdapter().getItem(position));
                eventBus.post(event);
            }
        });


        listButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapDisplayed) {
                    mapDisplayed = false;
                    getChildFragmentManager().beginTransaction().hide(googleMapFragment).commit();
                    mcListContainer.setVisibility(View.VISIBLE);
                    getChildFragmentManager().executePendingTransactions();
                }
            }
        });
        mapButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mapDisplayed) {
                    mapDisplayed = true;
                    mcListContainer.setVisibility(View.INVISIBLE);
                    getChildFragmentManager().beginTransaction().show(googleMapFragment).commit();
                    getChildFragmentManager().executePendingTransactions();
                    //Post it on UI thread so that it gets en-queued behind fragment transactions and gets executed only after layout has happened for map
                    mcMapContainer.post(new Runnable() {
                        @Override
                        public void run() {
                            if(!markersAdded) {
                                googleMapFragment.addMarkers(complaintDtoList);
                                markersAdded = true;
                            }
                        }
                    });
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        eventBus.register(this);
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }

    public void setComplaintData(List<ComplaintDto> complaintDtoList) {
        filterListAndSetAdapter(complaintDtoList);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        if(complaintDtoList != null) {
            googleMapFragment.addMarkers(complaintDtoList);
            markersAdded = true;
        }
    }

    private void filterListAndSetAdapter(List<ComplaintDto> complaintDtoList) {
        List<ComplaintDto> closedComplaints = new ArrayList<ComplaintDto>();
        List<ComplaintDto> openComplaints = new ArrayList<ComplaintDto>();

        for(ComplaintDto complaintDto : complaintDtoList) {
            if(complaintDto.getStatus().equals("Closed")) {
                closedComplaints.add(complaintDto);
            }
            else {
                openComplaints.add(complaintDto);
            }
        }

        mcListOpen.setAdapter(new ComplaintListAdapter(getActivity(), R.layout.item_complaint_list, openComplaints));
        mcListClosed.setAdapter(new ComplaintListAdapter(getActivity(), R.layout.item_complaint_list, closedComplaints));
    }

    public void onEventMainThread(GetUserComplaintsEvent event) {
        if(event.getSuccess()) {
            complaintDtoList = event.getComplaintDtoList();
            setComplaintData(complaintDtoList);
            if(mapReady && mapDisplayed) {
                googleMapFragment.addMarkers(complaintDtoList);
                markersAdded = true;
            }
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch user complaints. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
        pDialog.dismiss();
    }
}
