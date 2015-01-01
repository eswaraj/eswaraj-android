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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.adapters.AmenityListAdapter;
import com.eswaraj.app.eswaraj.adapters.ComplaintListAdapter;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.config.ImageType;
import com.eswaraj.app.eswaraj.datastore.StorageCache;
import com.eswaraj.app.eswaraj.events.ComplaintSelectedEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesImagesEvent;
import com.eswaraj.app.eswaraj.events.GetComplaintImageEvent;
import com.eswaraj.app.eswaraj.events.GetProfileImageEvent;
import com.eswaraj.app.eswaraj.events.GetUserComplaintsEvent;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.ComplaintCounter;
import com.eswaraj.app.eswaraj.models.ComplaintDto;
import com.eswaraj.web.dto.UserDto;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.eswaraj.app.eswaraj.widgets.CustomProgressDialog;
import com.eswaraj.app.eswaraj.widgets.PieChartView;
import com.eswaraj.web.dto.CategoryDto;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import org.achartengine.GraphicalView;

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
    @Inject
    StorageCache storageCache;

    private GoogleMapFragment googleMapFragment;
    private List<ComplaintDto> complaintDtoList;
    private List<CategoryWithChildCategoryDto> categoryDtoList;

    private ListView mcListOpen;
    private ListView mcListClosed;
    private Button mapButton;
    private Button listButton;
    private Button analyticsButton;
    private CustomProgressDialog pDialog;
    private FrameLayout mcMapContainer;
    private FrameLayout mcChartContainer;
    private ViewGroup mcListContainer;
    private GridView mcAmenityList;
    private LinearLayout mcAnalyticsContainer;
    private TextView mcName;
    private ImageView mcPhoto;

    private Boolean mapDisplayed = false;
    private Boolean mapReady = false;
    private Boolean markersAdded = false;
    private Boolean categoriesDataAvailable = false;
    private Boolean complaintDataAvailable = false;
    private Boolean dataAlreadySet = false;


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
        middlewareService.loadProfileImage(getActivity(), userSession.getProfilePhoto(), userSession.getUser().getPerson().getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_complaints, container, false);
        mcListOpen = (ListView) rootView.findViewById(R.id.mcListOpen);
        mcListClosed = (ListView) rootView.findViewById(R.id.mcListClosed);
        mapButton = (Button) rootView.findViewById(R.id.mcShowMap);
        listButton = (Button) rootView.findViewById(R.id.mcShowList);
        analyticsButton = (Button) rootView.findViewById(R.id.mcShowAnalytics);
        mcMapContainer = (FrameLayout) rootView.findViewById(R.id.mcMapContainer);
        mcListContainer = (ViewGroup) rootView.findViewById(R.id.mcListContainer);
        mcChartContainer = (FrameLayout) rootView.findViewById(R.id.mcChartContainer);
        mcAmenityList = (GridView) rootView.findViewById(R.id.mcAmenityList);
        mcAnalyticsContainer = (LinearLayout) rootView.findViewById(R.id.mcAnalyticsContainer);
        mcName = (TextView) rootView.findViewById(R.id.mcName);
        mcPhoto = (ImageView) rootView.findViewById(R.id.mcPhoto);

        mcAnalyticsContainer.setVisibility(View.INVISIBLE);
        mcName.setText(userSession.getUser().getPerson().getName());

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
                    mapDisplayed = false;
                    mcAnalyticsContainer.setVisibility(View.INVISIBLE);
                    getChildFragmentManager().beginTransaction().hide(googleMapFragment).commit();
                    getChildFragmentManager().executePendingTransactions();
                    mcListContainer.setVisibility(View.VISIBLE);

            }
        });
        mapButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mapDisplayed = true;
                    mcListContainer.setVisibility(View.INVISIBLE);
                    mcAnalyticsContainer.setVisibility(View.INVISIBLE);
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

        });
        analyticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChildFragmentManager().beginTransaction().hide(googleMapFragment).commit();
                mcListContainer.setVisibility(View.INVISIBLE);
                getChildFragmentManager().executePendingTransactions();
                mcAnalyticsContainer.setVisibility(View.VISIBLE);
            }
        });

        return rootView;
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

    public synchronized void setComplaintData(List<ComplaintDto> complaintDtoList) {
        if(!dataAlreadySet) {
            filterListAndSetAdapter(complaintDtoList);
            populateCountersAndCreateChart();
            if (mapReady && mapDisplayed) {
                googleMapFragment.addMarkers(complaintDtoList);
                markersAdded = true;
            }
            dataAlreadySet = true;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        if(complaintDtoList != null) {
            googleMapFragment.addMarkers(complaintDtoList);
            markersAdded = true;
        }
    }

    public void populateCountersAndCreateChart() {
        ArrayList<ComplaintCounter> complaintCounters = new ArrayList<ComplaintCounter>();
        for(CategoryWithChildCategoryDto categoryDto : categoryDtoList) {
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
        GraphicalView chartView = PieChartView.getNewInstance(getActivity(), complaintCounters);
        mcChartContainer.addView(chartView);
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
            complaintDataAvailable = true;
            if(categoriesDataAvailable && !dataAlreadySet) {
                setComplaintData(complaintDtoList);
            }
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch user complaints. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
        pDialog.dismiss();
    }

    public void onEventMainThread(GetCategoriesDataEvent event) {
        if(event.getSuccess()) {
            categoryDtoList = event.getCategoryList();
            categoriesDataAvailable = true;
            if(complaintDataAvailable && !dataAlreadySet) {
                setComplaintData(complaintDtoList);
            }
            middlewareService.loadCategoriesImages(getActivity(), categoryDtoList);
        }
    }

    public void onEventMainThread(GetCategoriesImagesEvent event) {
        if(event.getSuccess()) {
            AmenityListAdapter amenityListAdapter = new AmenityListAdapter(getActivity(), R.layout.item_amenity_list, categoryDtoList);
            mcAmenityList.setAdapter(amenityListAdapter);
        }
    }

    public void onEventMainThread(GetProfileImageEvent event) {
        if(event.getSuccess()) {
            mcPhoto.setImageBitmap(event.getBitmap());
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch your profile image. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }
}
