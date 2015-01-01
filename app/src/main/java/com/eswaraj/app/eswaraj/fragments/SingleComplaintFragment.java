package com.eswaraj.app.eswaraj.fragments;


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

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.config.ImageType;
import com.eswaraj.app.eswaraj.datastore.StorageCache;
import com.eswaraj.app.eswaraj.events.ComplaintClosedEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.GetComplaintImageEvent;
import com.eswaraj.app.eswaraj.events.GetProfileImageEvent;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
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


    private CommentsFragment commentsFragment;
    private ImageView complaintImage;
    private ImageView submitterImage;
    private TextView submitterName;
    private GoogleMapFragment googleMapFragment;
    private ComplaintDto complaintDto;
    private CustomProgressDialog pDialog;

    private Button scClose;
    private TextView scComplaintId;
    private TextView scStatus;
    private TextView scCategory;
    private TextView scSubCategory;
    private TextView scDescription;

    Long complaintId;
    Long personId;

    public SingleComplaintFragment() {
        // Required empty public constructor
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
        scStatus = (TextView) rootView.findViewById(R.id.scStatus);
        scComplaintId = (TextView) rootView.findViewById(R.id.scComplaintId);

        //Create fragments
        commentsFragment = new CommentsFragment();
        googleMapFragment = new GoogleMapFragment();

        //Get data from intent
        complaintDto = (ComplaintDto) getActivity().getIntent().getSerializableExtra("COMPLAINT");


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
            middlewareService.loadComplaintImage(getActivity(), complaintDto.getImages().get(0).getOrgUrl(), complaintId);
        }

        //Submitter details
        submitterName.setText(complaintDto.getCreatedBy().get(0).getName());
        if(!complaintDto.getCreatedBy().get(0).getProfilePhoto().equals("")) {
            personId = complaintDto.getCreatedBy().get(0).getId();
            middlewareService.loadProfileImage(getActivity(), complaintDto.getCreatedBy().get(0).getProfilePhoto(), personId);
        }

        //Set up fragments
        commentsFragment.setComplaintDto(complaintDto);
        googleMapFragment.setContext(this);

        //Add fragments
        if(savedInstanceState == null) {
            getChildFragmentManager().beginTransaction().add(R.id.scCommentContainer, commentsFragment).commit();
            getChildFragmentManager().beginTransaction().add(R.id.scDisplayContainer, googleMapFragment).commit();
        }

        scClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new CustomProgressDialog(getActivity(), false, true, "Closing your complaint ...");
                pDialog.show();
                middlewareService.closeComplaint(complaintDto);
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
            complaintImage.setImageBitmap(event.getBitmap());
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch complaint image. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

    public void onEventMainThread(GetProfileImageEvent event) {
        if(event.getSuccess()) {
            submitterImage.setImageBitmap(event.getBitmap());
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch submitter image. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }
}
