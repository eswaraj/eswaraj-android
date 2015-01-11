package com.eswaraj.app.eswaraj.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.adapters.AmenityListAdapter;
import com.eswaraj.app.eswaraj.adapters.ComplaintListAdapter;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.ComplaintSelectedEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesImagesEvent;
import com.eswaraj.app.eswaraj.events.GetHeaderImageEvent;
import com.eswaraj.app.eswaraj.events.GetLocationComplaintCountersEvent;
import com.eswaraj.app.eswaraj.events.GetLocationComplaintsEvent;
import com.eswaraj.app.eswaraj.events.GetLocationEvent;
import com.eswaraj.app.eswaraj.helpers.GoogleAnalyticsTracker;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.ComplaintCounter;
import com.eswaraj.app.eswaraj.models.ComplaintDto;
import com.eswaraj.app.eswaraj.util.GlobalSessionUtil;
import com.eswaraj.app.eswaraj.widgets.CustomProgressDialog;
import com.eswaraj.app.eswaraj.widgets.CustomScrollView;
import com.eswaraj.app.eswaraj.widgets.PieChartView;
import com.eswaraj.web.dto.CategoryDto;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.web.dto.LocationDto;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import org.achartengine.GraphicalView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class ConstituencyFragment extends BaseFragment implements OnMapReadyCallback{

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    GlobalSessionUtil globalSession;
    @Inject
    GoogleAnalyticsTracker googleAnalyticsTracker;

    private LocationDto locationDto;
    private List<ComplaintDto> complaintDtoList;
    private List<ComplaintCounter> complaintCounters;

    private ListView mcListOpen;
    private ListView mcListClosed;
    private ImageView mapButton;
    private ImageView listButton;
    private ImageView analyticsButton;
    private ImageView infoButton;
    private LinearLayout mcMapContainer;
    private FrameLayout mcMap;
    private FrameLayout mcChartContainer;
    private ViewGroup mcListContainer;
    private GridView mcAmenityList;
    private LinearLayout mcAnalyticsContainer;
    private TextView mcName;
    private ImageView mcPhoto;
    private TextView mcIssueCount;
    private LinearLayout mcMapButtons;
    private GoogleMapFragment googleMapFragment;
    private CustomProgressDialog pDialog;
    private Button mcShowMore;
    private LinearLayout mcInfoContainer;
    private CustomScrollView cScrollView;
    private RelativeLayout cDataView;

    private Button heatmap;
    private Button cluster;
    private Button markers;

    private Boolean mapDisplayed = false;
    private Boolean mapReady = false;
    private Boolean markersAdded = false;
    private Boolean complaintDataAvailable = false;
    private Boolean counterDataAvailable = false;
    private Boolean dataAlreadySet = false;
    private Boolean locationDtoAvailable = true;
    private Bitmap photo;
    private Long totalComplaints = 0L;
    private ComplaintListAdapter openListAdapter;
    private ComplaintListAdapter closeListAdapter;

    private TextView cTotalHouses;
    private TextView cTotalPopulation;
    private TextView cTotalMalePopulation;
    private TextView cTotalFemalePopulation;
    private TextView cTotalLiteratePopulation;
    private TextView cTotalMaleLiteratePopulation;
    private TextView cTotalFemaleLiteratePopulation;
    private TextView cTotalWorkingPopulation;
    private TextView cTotalMaleWorkingPopulation;
    private TextView cTotalFemaleWorkingPopulation;
    private TextView cArea;
    private TextView cPerimeter;


    public ConstituencyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationDto = (LocationDto) getActivity().getIntent().getSerializableExtra("LOCATION");
        if(locationDto == null) {
            locationDtoAvailable = false;
            middlewareService.loadLocation(getActivity(), getActivity().getIntent().getLongExtra("ID", -1));
        }
        eventBus.register(this);

        googleMapFragment = new GoogleMapFragment();
        googleMapFragment.setContext(this);
        if(savedInstanceState == null) {
            getChildFragmentManager().beginTransaction().add(R.id.cMap, googleMapFragment).commit();
        }
        getChildFragmentManager().beginTransaction().hide(googleMapFragment).commit();
        getChildFragmentManager().executePendingTransactions();
        mapDisplayed = false;

        pDialog = new CustomProgressDialog(getActivity(), false, true, "Fetching constituency complaints ...");
        pDialog.show();

        if(locationDtoAvailable) {
            middlewareService.loadLocationComplaints(getActivity(), locationDto, 0, 50);
            middlewareService.loadLocationComplaintCounters(getActivity(), locationDto);
            middlewareService.loadHeaderImage(getActivity(), locationDto.getMobileHeaderImageUrl(), locationDto.getId(), false);
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
        heatmap = (Button) rootView.findViewById(R.id.heatmap);
        cluster = (Button) rootView.findViewById(R.id.cluster);
        markers = (Button) rootView.findViewById(R.id.marker);

        mcListOpen = (ListView) rootView.findViewById(R.id.cListOpen);
        mcListClosed = (ListView) rootView.findViewById(R.id.cListClosed);
        mapButton = (ImageView) rootView.findViewById(R.id.cShowMap);
        listButton = (ImageView) rootView.findViewById(R.id.cShowList);
        analyticsButton = (ImageView) rootView.findViewById(R.id.cShowAnalytics);
        infoButton = (ImageView) rootView.findViewById(R.id.cShowInfo);
        mcMap = (FrameLayout) rootView.findViewById(R.id.cMap);
        mcMapContainer = (LinearLayout) rootView.findViewById(R.id.cMapContainer);
        mcListContainer = (ViewGroup) rootView.findViewById(R.id.cListContainer);
        mcChartContainer = (FrameLayout) rootView.findViewById(R.id.cChartContainer);
        mcAmenityList = (GridView) rootView.findViewById(R.id.cAmenityList);
        mcAnalyticsContainer = (LinearLayout) rootView.findViewById(R.id.cAnalyticsContainer);
        mcName = (TextView) rootView.findViewById(R.id.cName);
        mcPhoto = (ImageView) rootView.findViewById(R.id.cPhoto);
        mcMapButtons = (LinearLayout) rootView.findViewById(R.id.cMapButtons);
        mcIssueCount = (TextView) rootView.findViewById(R.id.issue_issues);
        mcShowMore = (Button) rootView.findViewById(R.id.cShowMore);
        mcInfoContainer = (LinearLayout) rootView.findViewById(R.id.cInfoContainer);
        cScrollView = (CustomScrollView) rootView.findViewById(R.id.cScrollView);
        cDataView = (RelativeLayout) rootView.findViewById(R.id.cDataView);

        cTotalHouses = (TextView)rootView.findViewById( R.id.cTotalHouses );
        cTotalPopulation = (TextView)rootView.findViewById( R.id.cTotalPopulation );
        cTotalMalePopulation = (TextView)rootView.findViewById( R.id.cTotalMalePopulation );
        cTotalFemalePopulation = (TextView)rootView.findViewById( R.id.cTotalFemalePopulation );
        cTotalLiteratePopulation = (TextView)rootView.findViewById( R.id.cTotalLiteratePopulation );
        cTotalMaleLiteratePopulation = (TextView)rootView.findViewById( R.id.cTotalMaleLiteratePopulation );
        cTotalFemaleLiteratePopulation = (TextView)rootView.findViewById( R.id.cTotalFemaleLiteratePopulation );
        cTotalWorkingPopulation = (TextView)rootView.findViewById( R.id.cTotalWorkingPopulation );
        cTotalMaleWorkingPopulation = (TextView)rootView.findViewById( R.id.cTotalMaleWorkingPopulation );
        cTotalFemaleWorkingPopulation = (TextView)rootView.findViewById( R.id.cTotalFemaleWorkingPopulation );
        cArea = (TextView)rootView.findViewById( R.id.cArea );
        cPerimeter = (TextView)rootView.findViewById( R.id.cPerimeter );

        setupMenu(rootView.findViewById(R.id.menu));

        mcAnalyticsContainer.setVisibility(View.INVISIBLE);
        mcMapButtons.setVisibility(View.INVISIBLE);
        mcInfoContainer.setVisibility(View.INVISIBLE);
        if(photo != null) {
            mcPhoto.setImageBitmap(photo);
        }

        if(locationDto != null) {
            mcName.setText(locationDto.getName());

            //Info data
            if (locationDto.getTotalNumberOfHouses() != null) {
                cTotalHouses.setText(locationDto.getTotalNumberOfHouses().toString());
                cTotalPopulation.setText(locationDto.getTotalPopulation().toString());
                cTotalMalePopulation.setText(locationDto.getTotalMalePopulation().toString());
                cTotalFemalePopulation.setText(locationDto.getTotalFemalePopulation().toString());
                cTotalLiteratePopulation.setText(locationDto.getTotalLiteratePopulation().toString());
                cTotalMaleLiteratePopulation.setText(locationDto.getTotalMaleLiteratePopulation().toString());
                cTotalFemaleLiteratePopulation.setText(locationDto.getTotalFemaleLiteratePopulation().toString());
                cTotalWorkingPopulation.setText(locationDto.getTotalWorkingPopulation().toString());
                cTotalMaleWorkingPopulation.setText(locationDto.getTotalMaleWorkingPopulation().toString());
                cTotalFemaleWorkingPopulation.setText(locationDto.getTotalFemaleWorkingPopulation().toString());
                cArea.setText(locationDto.getArea().toString());
                cPerimeter.setText(locationDto.getPerimeter().toString());
            }
        }

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
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Constituency: Show List");
                cScrollView.removeInterceptScrollView(googleMapFragment.getView());
                mapDisplayed = false;
                cDataView.removeAllViews();
                cDataView.addView(mcListContainer);
                cScrollView.smoothScrollTo(0, 0);
            }
        });
        mapButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Constituency: Show Map");
                cScrollView.addInterceptScrollView(googleMapFragment.getView());
                mapDisplayed = true;
                mcMapButtons.setVisibility(View.VISIBLE);
                cDataView.removeAllViews();
                cDataView.addView(mcMapContainer);
                getChildFragmentManager().beginTransaction().show(googleMapFragment).commit();
                getChildFragmentManager().executePendingTransactions();
                //Post it on UI thread so that it gets en-queued behind fragment transactions and gets executed only after layout has happened for map
                mcMap.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!markersAdded) {
                            googleMapFragment.addMarkers(complaintDtoList);
                            markersAdded = true;
                        }
                    }
                });
                cScrollView.smoothScrollTo(0, 0);
            }

        });
        analyticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Constituency: Show Analytics");
                cScrollView.removeInterceptScrollView(googleMapFragment.getView());
                mapDisplayed = false;
                cDataView.removeAllViews();
                cDataView.addView(mcAnalyticsContainer);
                mcAnalyticsContainer.setVisibility(View.VISIBLE);
                cScrollView.smoothScrollTo(0, 0);
            }
        });
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Constituency: Show Info");
                cScrollView.removeInterceptScrollView(googleMapFragment.getView());
                mapDisplayed = false;
                cDataView.removeAllViews();
                cDataView.addView(mcInfoContainer);
                mcInfoContainer.setVisibility(View.VISIBLE);
                cScrollView.smoothScrollTo(0, 0);
            }
        });

        mcShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Constituency: Show More");
                middlewareService.loadLocationComplaints(getActivity(), locationDto, complaintDtoList.size(), 50);
                markersAdded = false;
                dataAlreadySet = false;
                pDialog = new CustomProgressDialog(getActivity(), false, true, "Fetching more complaints ...");
                pDialog.show();
            }
        });

        heatmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Constituency: Show Heatmap");
                googleMapFragment.addHeatMap(complaintDtoList);
            }
        });
        cluster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Constituency: Show Cluster");
                googleMapFragment.addCluster(complaintDtoList);
            }
        });
        markers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Constituency: Show Markers");
                googleMapFragment.addMarkers(complaintDtoList);
            }
        });
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        if(complaintDtoList != null) {
            googleMapFragment.addMarkers(complaintDtoList);
            markersAdded = true;
        }
    }

    public synchronized void setComplaintData() {
        if(!dataAlreadySet) {
            filterListAndSetAdapter();
            populateCountersAndCreateChart();
            if (mapReady && mapDisplayed) {
                googleMapFragment.addMarkers(complaintDtoList);
                markersAdded = true;
            }
            dataAlreadySet = true;
        }
        cScrollView.smoothScrollTo(0, 0);
    }

    private void filterListAndSetAdapter() {
        final List<ComplaintDto> closedComplaints = new ArrayList<ComplaintDto>();
        final List<ComplaintDto> openComplaints = new ArrayList<ComplaintDto>();

        for(ComplaintDto complaintDto : complaintDtoList) {
            if(complaintDto.getStatus().equals("Closed")) {
                closedComplaints.add(complaintDto);
            }
            else {
                openComplaints.add(complaintDto);
            }
        }

        if(openListAdapter == null) {

            openListAdapter = new ComplaintListAdapter(getActivity(), R.layout.item_complaint_list, openComplaints);
            closeListAdapter = new ComplaintListAdapter(getActivity(), R.layout.item_complaint_list, closedComplaints);

            mcListOpen.setAdapter(openListAdapter);
            mcListOpen.setDividerHeight(0);
            mcListClosed.setAdapter(closeListAdapter);
            mcListClosed.setDividerHeight(0);
        }
        else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    openListAdapter.clear();
                    closeListAdapter.clear();
                    for(ComplaintDto complaintDto : openComplaints) {
                        openListAdapter.addComplaint(complaintDto);
                    }
                    for(ComplaintDto complaintDto : closedComplaints) {
                        closeListAdapter.addComplaint(complaintDto);
                    }
                    openListAdapter.notifyDataSetChanged();
                    closeListAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void populateCountersAndCreateChart() {
        GraphicalView chartView = PieChartView.getNewInstance(getActivity(), complaintCounters, globalSession.getColorMap());
        mcChartContainer.removeAllViews();
        mcChartContainer.addView(chartView);
        totalComplaints = 0L;
        for(ComplaintCounter complaintCounter : complaintCounters) {
            totalComplaints += complaintCounter.getCount();
        }
        mcIssueCount.setText(totalComplaints + " Issues");
    }

    public void markComplaintClosed(Long id) {
        ComplaintDto complaintDtoToMove = openListAdapter.removeComplaint(id);
        openListAdapter.notifyDataSetChanged();
        complaintDtoToMove.setStatus("Done");
        closeListAdapter.addComplaint(complaintDtoToMove);
        closeListAdapter.notifyDataSetChanged();
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
            complaintDataAvailable = true;
            if(counterDataAvailable && !dataAlreadySet) {
                setComplaintData();
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
            counterDataAvailable = true;
            if(complaintDataAvailable && !dataAlreadySet) {
                setComplaintData();
            }
            AmenityListAdapter amenityListAdapter = new AmenityListAdapter(getActivity(), R.layout.item_amenity_list, globalSession.getCategoryDtoList(), complaintCounters, globalSession.getColorMap());
            mcAmenityList.setAdapter(amenityListAdapter);
        }
        else {
            AmenityListAdapter amenityListAdapter = new AmenityListAdapter(getActivity(), R.layout.item_amenity_list, globalSession.getCategoryDtoList(), null, globalSession.getColorMap());
            mcAmenityList.setAdapter(amenityListAdapter);
            Toast.makeText(getActivity(), "Could not fetch constituency complaint counters. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

    public void onEventMainThread(GetHeaderImageEvent event) {
        if(event.getSuccess()) {
            if(mcPhoto != null) {
                mcPhoto.setImageBitmap(event.getBitmap());
            }
            else {
                photo = event.getBitmap();
            }
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch header image. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

    public void onEventMainThread(GetLocationEvent event) {
        if(event.getSuccess()) {
            locationDto = event.getLocationDto();
            middlewareService.loadLocationComplaints(getActivity(), locationDto, 0, 50);
            middlewareService.loadLocationComplaintCounters(getActivity(), locationDto);
            middlewareService.loadHeaderImage(getActivity(), locationDto.getMobileHeaderImageUrl(), locationDto.getId(), false);

            mcName.setText(locationDto.getName());

            //Info data
            if (locationDto.getTotalNumberOfHouses() != null) {
                cTotalHouses.setText(locationDto.getTotalNumberOfHouses().toString());
                cTotalPopulation.setText(locationDto.getTotalPopulation().toString());
                cTotalMalePopulation.setText(locationDto.getTotalMalePopulation().toString());
                cTotalFemalePopulation.setText(locationDto.getTotalFemalePopulation().toString());
                cTotalLiteratePopulation.setText(locationDto.getTotalLiteratePopulation().toString());
                cTotalMaleLiteratePopulation.setText(locationDto.getTotalMaleLiteratePopulation().toString());
                cTotalFemaleLiteratePopulation.setText(locationDto.getTotalFemaleLiteratePopulation().toString());
                cTotalWorkingPopulation.setText(locationDto.getTotalWorkingPopulation().toString());
                cTotalMaleWorkingPopulation.setText(locationDto.getTotalMaleWorkingPopulation().toString());
                cTotalFemaleWorkingPopulation.setText(locationDto.getTotalFemaleWorkingPopulation().toString());
                cArea.setText(locationDto.getArea().toString());
                cPerimeter.setText(locationDto.getPerimeter().toString());
            }
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch constituency details. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }
}
