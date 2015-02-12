package com.next.eswaraj.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseFragment;
import com.next.eswaraj.events.ComplaintClosedEvent;
import com.next.eswaraj.events.GetSingleComplaintEvent;
import com.next.eswaraj.helpers.GoogleAnalyticsTracker;
import com.next.eswaraj.middleware.MiddlewareServiceImpl;
import com.next.eswaraj.util.UserSessionUtil;
import com.next.eswaraj.widgets.CustomNetworkImageView;
import com.next.eswaraj.widgets.CustomProgressDialog;
import com.eswaraj.web.dto.CategoryDto;
import com.next.eswaraj.models.ComplaintDto;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;


import java.util.Date;

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
    GoogleAnalyticsTracker googleAnalyticsTracker;

    private CommentsFragment commentsFragment;
    private CustomNetworkImageView complaintImage;
    private CustomNetworkImageView submitterImage;
    private CustomNetworkImageView scIcon;
    private TextView submitterName;
    private GoogleMapFragment googleMapFragment;
    private ComplaintDto complaintDto;
    private CustomProgressDialog pDialog;
    private CustomProgressDialog pDialogSave;

    private Button scClose;
    private TextView scComplaintId;
    private TextView scStatus;
    private TextView scCategory;
    private TextView scSubCategory;
    private TextView scDescription;
    private TextView scAddress;
    private TextView scDate;

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
    public void onStart() {
        super.onStart();
        if(pDialogSave != null && pDialogSave.isShowing()) {
            pDialogSave.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_single_complaint, container, false);

        //Get handles
        scClose = (Button) rootView.findViewById(R.id.scClose);
        scCategory = (TextView) rootView.findViewById(R.id.scCategory);
        scSubCategory = (TextView) rootView.findViewById(R.id.scSubCategory);
        scDescription = (TextView) rootView.findViewById(R.id.scDescription);
        complaintImage = (CustomNetworkImageView) rootView.findViewById(R.id.scComplaintPhoto);
        submitterName = (TextView) rootView.findViewById(R.id.scSubmitterName);
        submitterImage = (CustomNetworkImageView) rootView.findViewById(R.id.scSubmitterImage);
        scIcon = (CustomNetworkImageView) rootView.findViewById(R.id.scIcon);
        scStatus = (TextView) rootView.findViewById(R.id.scStatus);
        scComplaintId = (TextView) rootView.findViewById(R.id.scComplaintId);
        scAddress = (TextView) rootView.findViewById(R.id.scAddress);
        scDate = (TextView) rootView.findViewById(R.id.scDate);

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
                pDialogSave = new CustomProgressDialog(getActivity(), false, true, "Closing your complaint ...");
                pDialogSave.show();
                middlewareService.closeComplaint(complaintDto);
            }
        });
        return rootView;
    }

    private void showData() {
        if(complaintDto.getDescription() == null || complaintDto.getDescription().equals("")) {
            scDescription.setVisibility(View.GONE);
        }
        else {
            scDescription.setText(complaintDto.getDescription());
        }
        scComplaintId.setText(complaintDto.getId().toString());
        scStatus.setText(complaintDto.getStatus());
        scDate.setText(DateUtils.getRelativeTimeSpanString(complaintDto.getComplaintTime(), new Date().getTime(), DateUtils.MINUTE_IN_MILLIS));
        if(complaintDto.getLocationAddress() != null) {
            scAddress.setText(complaintDto.getLocationAddress());
        }
        else {
            scAddress.setText("");
        }

        for(CategoryDto category : complaintDto.getCategories()) {
            if(category.isRoot()) {
                scCategory.setText(category.getName());
                scIcon.setImageURI(Uri.parse(getActivity().getFilesDir() + "/eSwaraj_" + String.valueOf(category.getId()) + ".png"));
            }
            else {
                scSubCategory.setText(category.getName());
            }
        }
        if(complaintDto.getCreatedBy() != null) {
            if (!userSession.getUser().getPerson().getExternalId().equals(complaintDto.getCreatedBy().get(0).getExternalId()) || complaintDto.getStatus().equals("Done")) {
                scClose.setVisibility(View.GONE);
            }
        }
        else {
            scClose.setVisibility(View.GONE);
        }
        if(complaintDto.getImages() != null) {
            complaintImage.loadComplaintImage(complaintDto.getImages().get(0).getOrgUrl(), complaintDto.getId());
        }
        else {
            complaintImage.setVisibility(View.GONE);
        }

        //Submitter details
        if(complaintDto.getCreatedBy() != null) {
            submitterName.setText(complaintDto.getCreatedBy().get(0).getName());
            if (!complaintDto.getCreatedBy().get(0).getProfilePhoto().equals("")) {
                submitterImage.loadProfileImage(complaintDto.getCreatedBy().get(0).getProfilePhoto(), complaintDto.getCreatedBy().get(0).getId());
            }
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
            scClose.setVisibility(View.GONE);
            scStatus.setText("Closed");
        }
        else {
            Toast.makeText(getActivity(),"Failed to close complaint. Please try again. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
        pDialogSave.dismiss();
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
