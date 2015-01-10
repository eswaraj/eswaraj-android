package com.eswaraj.app.eswaraj.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.config.ImageType;
import com.eswaraj.app.eswaraj.datastore.StorageCache;
import com.eswaraj.app.eswaraj.events.ComplaintClosedEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.GetComplaintImageEvent;
import com.eswaraj.app.eswaraj.events.GetProfileImageEvent;
import com.eswaraj.app.eswaraj.events.GetSingleComplaintEvent;
import com.eswaraj.app.eswaraj.helpers.GoogleAnalyticsTracker;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.eswaraj.app.eswaraj.util.VolleyUtil;
import com.eswaraj.app.eswaraj.widgets.CustomProgressDialog;
import com.eswaraj.web.dto.CategoryDto;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.app.eswaraj.models.ComplaintDto;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class SingleComplaintFragment extends BaseFragment implements OnMapReadyCallback {

    @Inject
    EventBus eventBus;
    @Inject
    UserSessionUtil userSession;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    VolleyUtil volleyUtil;
    @Inject
    GoogleAnalyticsTracker googleAnalyticsTracker;


    private CommentsFragment commentsFragment;
    private ImageView complaintImage;
    private ImageView submitterImage;
    private TextView submitterName;
    private TextView submitterDetails;
    private GoogleMapFragment googleMapFragment;
    private ComplaintDto complaintDto;
    private CustomProgressDialog pDialog;

    private Button scClose;
    private TextView scComplaintId;
    private TextView scStatus;
    private TextView scCategory;
    private TextView scSubCategory;
    private TextView scDescription;

    private Long complaintId;
    private Long personId;
    private Bitmap submitterBitmap;
    private Bitmap complaintBitmap;
    private Bundle savedInstanceState;

    public SingleComplaintFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_single_complaint, container, false);

        //Get handles
        scClose = (Button) rootView.findViewById(R.id.scClose);
        scCategory = (TextView) rootView.findViewById(R.id.scCategory);
        scSubCategory = (TextView) rootView.findViewById(R.id.scSubCategory);
        scDescription = (TextView) rootView.findViewById(R.id.scDescription);
        complaintImage = (ImageView) rootView.findViewById(R.id.scComplaintPhoto);
        submitterName = (TextView) rootView.findViewById(R.id.scSubmitterName);
        submitterImage = (ImageView) rootView.findViewById(R.id.scSubmitterImage);
        submitterDetails = (TextView) rootView.findViewById(R.id.scSubmitterDetails);
        scStatus = (TextView) rootView.findViewById(R.id.scStatus);
        scComplaintId = (TextView) rootView.findViewById(R.id.scComplaintId);

        //Create fragments
        commentsFragment = new CommentsFragment();
        googleMapFragment = new GoogleMapFragment();

        this.savedInstanceState = savedInstanceState;

        //Get data from intent
        if(getActivity().getIntent().getBooleanExtra("DATA_PRESENT", false)) {
            complaintDto = (ComplaintDto) getActivity().getIntent().getSerializableExtra("COMPLAINT");
            showData();
        }
        else {
            middlewareService.loadSingleComplaint(getActivity(), getActivity().getIntent().getLongExtra("COMPLAINT_ID", 0L));
            pDialog = new CustomProgressDialog(getActivity(), false, true, "Loading your complaint ...");
            pDialog.show();
        }

        scClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "SingleComplaint: Close");
                pDialog = new CustomProgressDialog(getActivity(), false, true, "Closing your complaint ...");
                pDialog.show();
                middlewareService.closeComplaint(complaintDto);
            }
        });
        return rootView;
    }

    private void showData() {
        scDescription.setText(complaintDto.getDescription());
        scComplaintId.setText(complaintDto.getId().toString());
        scStatus.setText(complaintDto.getStatus());

        for(CategoryDto category : complaintDto.getCategories()) {
            if(category.isRoot()) {
                scCategory.setText(category.getName());
            }
            else {
                scSubCategory.setText(category.getName());
            }
        }
        if(!userSession.getUser().getPerson().getExternalId().equals(complaintDto.getCreatedBy().get(0).getExternalId()) || complaintDto.getStatus().equals("Closed")) {
            scClose.setVisibility(View.INVISIBLE);
        }
        if(complaintDto.getImages() != null) {
            complaintId = complaintDto.getId();
            middlewareService.loadComplaintImage(getActivity(), complaintDto.getImages().get(0).getOrgUrl(), complaintId, false);
        }
        if(complaintBitmap != null) {
            complaintImage.setImageBitmap(complaintBitmap);
        }

        //Submitter details
        submitterName.setText(complaintDto.getCreatedBy().get(0).getName());
        if(!complaintDto.getCreatedBy().get(0).getProfilePhoto().equals("")) {
            //PersonId is null. Using complaintId for caching
            personId = complaintDto.getCreatedBy().get(0).getId();
            middlewareService.loadProfileImage(getActivity(), complaintDto.getCreatedBy().get(0).getProfilePhoto(), complaintDto.getId(), false);
            //submitterImage.setImageUrl(complaintDto.getCreatedBy().get(0).getProfilePhoto(), volleyUtil.getImageLoader());
        }
        if(submitterBitmap != null) {
            submitterImage.setImageBitmap(submitterBitmap);
        }

        //Set up fragments
        commentsFragment.setComplaintDto(complaintDto);
        googleMapFragment.setContext(this);

        //Add fragments
        if(savedInstanceState == null) {
            getChildFragmentManager().beginTransaction().add(R.id.scCommentContainer, commentsFragment).commit();
            getChildFragmentManager().beginTransaction().add(R.id.scDisplayContainer, googleMapFragment).commit();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapFragment.disableGestures();
        googleMapFragment.updateMarkerLocation(complaintDto.getLattitude(), complaintDto.getLongitude());
    }

    public void onEventMainThread(ComplaintClosedEvent event) {
        if(event.getSuccess()) {
            scClose.setVisibility(View.INVISIBLE);
            scStatus.setText("Closed");
        }
        else {
            Toast.makeText(getActivity(),"Failed to close complaint. Please try again. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
        pDialog.dismiss();
    }

    public void onEventMainThread(GetComplaintImageEvent event) {
        if(event.getSuccess()) {
            if(complaintImage != null) {
                complaintImage.setImageBitmap(event.getBitmap());
            }
            else {
                complaintBitmap = event.getBitmap();
            }
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch complaint image. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

    public void onEventMainThread(GetProfileImageEvent event) {
        if(event.getSuccess()) {
            if(submitterImage != null) {
                submitterImage.setImageBitmap(event.getBitmap());
            }
            else {
                submitterBitmap = event.getBitmap();
            }
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch submitter image. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

    public void onEventMainThread(GetSingleComplaintEvent event) {
        if(event.getSuccess()) {
            complaintDto = event.getComplaintDto();
            showData();
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch complaint from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
        pDialog.dismiss();
    }
}
