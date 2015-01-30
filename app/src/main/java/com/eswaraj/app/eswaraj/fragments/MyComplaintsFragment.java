    package com.eswaraj.app.eswaraj.fragments;


    import android.graphics.Bitmap;
    import android.os.Bundle;
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
    import android.widget.RelativeLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.eswaraj.app.eswaraj.R;
    import com.eswaraj.app.eswaraj.adapters.AmenityListAdapter;
    import com.eswaraj.app.eswaraj.adapters.ComplaintListAdapter;
    import com.eswaraj.app.eswaraj.base.BaseFragment;
    import com.eswaraj.app.eswaraj.events.ComplaintSelectedEvent;
    import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
    import com.eswaraj.app.eswaraj.events.GetCategoriesImagesEvent;
    import com.eswaraj.app.eswaraj.events.GetProfileImageEvent;
    import com.eswaraj.app.eswaraj.events.GetUserComplaintsEvent;
    import com.eswaraj.app.eswaraj.helpers.GoogleAnalyticsTracker;
    import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
    import com.eswaraj.app.eswaraj.models.ComplaintCounter;
    import com.eswaraj.app.eswaraj.models.ComplaintDto;
    import com.eswaraj.app.eswaraj.util.GenericUtil;
    import com.eswaraj.app.eswaraj.util.GlobalSessionUtil;
    import com.eswaraj.app.eswaraj.util.UserSessionUtil;
    import com.eswaraj.app.eswaraj.widgets.CustomProgressDialog;
    import com.eswaraj.app.eswaraj.widgets.CustomScrollView;
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
        GlobalSessionUtil globalSession;
        @Inject
        GoogleAnalyticsTracker googleAnalyticsTracker;

        private GoogleMapFragment googleMapFragment;
        private List<ComplaintDto> complaintDtoList;
        ArrayList<ComplaintCounter> complaintCounters;

        private ListView mcListOpen;
        private ListView mcListClosed;
        private ImageView mapButton;
        private ImageView listButton;
        private ImageView analyticsButton;
        private CustomProgressDialog pDialog;
        private FrameLayout mcMapContainer;
        private FrameLayout mcChartContainer;
        private ViewGroup mcListContainer;
        private GridView mcAmenityList;
        private LinearLayout mcAnalyticsContainer;
        private TextView mcName;
        private ImageView mcPhoto;
        private TextView mcUserDetails;
        private RelativeLayout mcDataView;
        private CustomScrollView mcScrollView;

        private Boolean mapDisplayed = false;
        private Boolean mapReady = false;
        private Boolean markersAdded = false;
        private Bitmap photo;

        private List<ComplaintDto> closedComplaints = new ArrayList<ComplaintDto>();
        private List<ComplaintDto> openComplaints = new ArrayList<ComplaintDto>();
        private ComplaintListAdapter openComplaintsAdapter;
        private ComplaintListAdapter closedComplaintsAdapter;


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

            eventBus.register(this);

            middlewareService.loadUserComplaints(getActivity(), userSession.getUser(), true);
            middlewareService.loadProfileImage(getActivity(), userSession.getProfilePhoto(), userSession.getUser().getPerson().getId(), false);
        }

        @Override
        public void onDestroy() {
            eventBus.unregister(this);
            super.onDestroy();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_my_complaints, container, false);
            mcListOpen = (ListView) rootView.findViewById(R.id.mcListOpen);
            mcListClosed = (ListView) rootView.findViewById(R.id.mcListClosed);
            mapButton = (ImageView) rootView.findViewById(R.id.mcShowMap);
            listButton = (ImageView) rootView.findViewById(R.id.mcShowList);
            analyticsButton = (ImageView) rootView.findViewById(R.id.mcShowAnalytics);
            mcMapContainer = (FrameLayout) rootView.findViewById(R.id.mcMapContainer);
            mcListContainer = (ViewGroup) rootView.findViewById(R.id.mcListContainer);
            mcChartContainer = (FrameLayout) rootView.findViewById(R.id.mcChartContainer);
            mcAmenityList = (GridView) rootView.findViewById(R.id.mcAmenityList);
            mcAnalyticsContainer = (LinearLayout) rootView.findViewById(R.id.mcAnalyticsContainer);
            mcName = (TextView) rootView.findViewById(R.id.mcName);
            mcPhoto = (ImageView) rootView.findViewById(R.id.mcPhoto);
            mcUserDetails = (TextView) rootView.findViewById(R.id.mcUserDetails);
            mcDataView = (RelativeLayout) rootView.findViewById(R.id.mcDataView);
            mcScrollView = (CustomScrollView) rootView.findViewById(R.id.mcScrollView);

            mcListOpen.setScrollingCacheEnabled(false);
            mcListClosed.setScrollingCacheEnabled(false);

            mcAnalyticsContainer.setVisibility(View.INVISIBLE);
            if(userSession.getUser().getPerson().getDob() != null) {
                mcUserDetails.setText(GenericUtil.getAge(userSession.getUser().getPerson().getDob()) + " Years, " + userSession.getUser().getPerson().getGender());
            }
            mcName.setText(userSession.getUser().getPerson().getName());
            if(photo != null) {
                mcPhoto.setImageBitmap(photo);
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
                    googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "My Complaints: Show List");
                    mcScrollView.removeInterceptScrollView(googleMapFragment.getView());
                    mapDisplayed = false;
                    mcDataView.removeAllViews();
                    mcDataView.addView(mcListContainer);
                    mcListContainer.setVisibility(View.VISIBLE);
                    mcScrollView.smoothScrollTo(0, 0);

                }
            });
            mapButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "My Complaints: Show Map");
                    mcScrollView.addInterceptScrollView(googleMapFragment.getView());
                    mapDisplayed = true;
                    getChildFragmentManager().beginTransaction().show(googleMapFragment).commit();
                    getChildFragmentManager().executePendingTransactions();
                    mcDataView.removeAllViews();
                    mcDataView.addView(mcMapContainer);
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
                    mcScrollView.smoothScrollTo(0, 0);
                }

            });
            analyticsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "My Complaints: Show Analytics");
                    mcScrollView.removeInterceptScrollView(googleMapFragment.getView());
                    mapDisplayed = false;
                    mcDataView.removeAllViews();
                    mcDataView.addView(mcAnalyticsContainer);
                    mcAnalyticsContainer.setVisibility(View.VISIBLE);
                    mcScrollView.smoothScrollTo(0, 0);
                }
            });

            return rootView;
        }

        private void setComplaintData(List<ComplaintDto> complaintDtoList) {
                filterListAndSetAdapter(complaintDtoList);
                populateCountersAndCreateChart();
                if (mapReady && mapDisplayed) {
                    googleMapFragment.addMarkers(complaintDtoList);
                    markersAdded = true;
                }
            mcScrollView.smoothScrollTo(0, 0);
        }

        public void markComplaintClosed(Long id) {
            ComplaintDto complaintDtoToMove = openComplaintsAdapter.removeComplaint(id);
            openComplaintsAdapter.notifyDataSetChanged();
            complaintDtoToMove.setStatus("Done");
            closedComplaintsAdapter.addComplaint(complaintDtoToMove);
            closedComplaintsAdapter.notifyDataSetChanged();
        }


        @Override
        public void onMapReady(GoogleMap googleMap) {
            mapReady = true;
            if(complaintDtoList != null) {
                googleMapFragment.addMarkers(complaintDtoList);
                markersAdded = true;
            }
        }

        private void populateCountersAndCreateChart() {
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
            GraphicalView chartView = PieChartView.getNewInstance(getActivity(), complaintCounters, globalSession.getCategoryDtoList());
            mcChartContainer.addView(chartView);
            AmenityListAdapter amenityListAdapter = new AmenityListAdapter(getActivity(), R.layout.item_amenity_list, globalSession.getCategoryDtoList(), complaintCounters, globalSession.getColorMap());
            mcAmenityList.setAdapter(amenityListAdapter);
        }

        private void filterListAndSetAdapter(List<ComplaintDto> complaintDtoList) {
            for(ComplaintDto complaintDto : complaintDtoList) {
                if(complaintDto.getStatus().equals("Done")) {
                    closedComplaints.add(complaintDto);
                }
                else {
                    openComplaints.add(complaintDto);
                }
            }

            openComplaintsAdapter = new ComplaintListAdapter(getActivity(), R.layout.item_complaint_list, openComplaints);
            closedComplaintsAdapter = new ComplaintListAdapter(getActivity(), R.layout.item_complaint_list, closedComplaints);

            mcListOpen.setAdapter(openComplaintsAdapter);
            mcListOpen.setDividerHeight(0);
            mcListClosed.setAdapter(closedComplaintsAdapter);
            mcListClosed.setDividerHeight(0);
        }

        public void onEventMainThread(GetUserComplaintsEvent event) {
            if(event.getSuccess()) {
                complaintDtoList = event.getComplaintDtoList();
                setComplaintData(complaintDtoList);
            }
            else {
                Toast.makeText(getActivity(), "Could not fetch user complaints. Error = " + event.getError(), Toast.LENGTH_LONG).show();
            }
            pDialog.dismiss();
        }


        public void onEventMainThread(GetProfileImageEvent event) {
            if(event.getId().equals(userSession.getUser().getPerson().getId())) {
                if (event.getSuccess()) {
                    if (mcPhoto != null) {
                        mcPhoto.setImageBitmap(event.getBitmap());
                    } else {
                        photo = event.getBitmap();
                    }
                } else {
                    Toast.makeText(getActivity(), "Could not fetch your profile image. Error = " + event.getError(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
