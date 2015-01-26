package com.eswaraj.app.eswaraj.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.activities.LoginActivity;
import com.eswaraj.app.eswaraj.events.ComplaintPostedEvent;
import com.eswaraj.app.eswaraj.events.ComplaintReportedEvent;
import com.eswaraj.app.eswaraj.events.ComplaintSavedEvent;
import com.eswaraj.app.eswaraj.helpers.BitmapWorkerTask;
import com.eswaraj.app.eswaraj.helpers.CameraHelper;
import com.eswaraj.app.eswaraj.helpers.GoogleAnalyticsTracker;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.ComplaintSavedResponseDto;
import com.eswaraj.app.eswaraj.util.InternetServicesCheckUtil;
import com.eswaraj.app.eswaraj.util.LocationUtil;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.eswaraj.app.eswaraj.widgets.CustomProgressDialog;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.google.gson.Gson;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class AddDetailsFragment extends CameraHelper.CameraUtilFragment {

    @Inject
    Context applicationContext;
    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    LocationUtil locationUtil;
    @Inject
    UserSessionUtil userSession;
    @Inject
    CameraHelper cameraHelper;
    @Inject
    InternetServicesCheckUtil internetServicesCheckUtil;
    @Inject
    GoogleAnalyticsTracker googleAnalyticsTracker;

    private Button takePhoto;
    private Button attachPhoto;
    private Button deletePhoto;
    private Button retakePhoto;
    private Button post;
    private Button descriptionBtn;
    private EditText description;
    private TextView selected;
    private ImageView photoDisplay;
    private CheckBox anonCheckbox;
    private ViewGroup takePhotoContainer;
    private ViewGroup photoTakenContainer;

    private CategoryWithChildCategoryDto amenity;
    private CategoryWithChildCategoryDto template;
    private Location location;

    private static final int REQUEST_POST_BUTTON = 11;

    private Boolean posted = false;
    private CustomProgressDialog pDialog;

    public AddDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraHelper.setFragment(this);
        eventBus.registerSticky(this);
    }

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        locationUtil.subscribe(applicationContext, false);
        if(pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        locationUtil.unsubscribe();
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_details, container, false);

        //Get handles
        takePhoto = (Button) rootView.findViewById(R.id.adTakePhoto);
        attachPhoto = (Button) rootView.findViewById(R.id.adAttachPhoto);
        deletePhoto = (Button) rootView.findViewById(R.id.adDeletePhoto);
        retakePhoto = (Button) rootView.findViewById(R.id.adRetakePhoto);
        post = (Button) rootView.findViewById(R.id.adPost);
        descriptionBtn = (Button) rootView.findViewById(R.id.adDescriptionbtn);
        description = (EditText) rootView.findViewById(R.id.adDescription);
        selected = (TextView) rootView.findViewById(R.id.adSelected);
        photoDisplay = (ImageView) rootView.findViewById(R.id.adPhotoDisplay);
        anonCheckbox = (CheckBox) rootView.findViewById(R.id.adAnonCheckbox);
        takePhotoContainer = (ViewGroup) rootView.findViewById(R.id.take_photo_container_ref);
        photoTakenContainer = (ViewGroup) rootView.findViewById(R.id.photo_taken_container_ref);

        //Init
        locationUtil.setup(getActivity());

        //Get the data from intent and display
        template = (CategoryWithChildCategoryDto) getActivity().getIntent().getSerializableExtra("TEMPLATE");
        amenity = (CategoryWithChildCategoryDto) getActivity().getIntent().getSerializableExtra("AMENITY");
        selected.setText(template.getName());

        //Initial state
        descriptionBtn.setVisibility(View.VISIBLE);
        description.setVisibility(View.INVISIBLE);

        //Register on-click listeners
        takePhoto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraHelper.openOnlyCameraIntent();
            }
        });

        attachPhoto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraHelper.openOnlyGalleryIntent();
            }
        });

        deletePhoto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraHelper.setImageName(null);
                resetIssueImageView();
            }
        });

        retakePhoto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraHelper.openImageIntent();
            }
        });

        post.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "Post Complaint");
                if(userSession.isUserLoggedIn(getActivity())) {
                    if (!posted) {
                        if(internetServicesCheckUtil.isServiceAvailable(getActivity())) {
                            googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.ONLINE, "Post Complaint: Online");
                            middlewareService.postComplaint(userSession.getUser(), amenity, template, location, description.getText().toString(), cameraHelper.getImageFile(), anonCheckbox.isChecked(), userSession.getUserRevGeocodedLocation());
                            pDialog = new CustomProgressDialog(getActivity(), false, true, "Posting your complaint ...");
                            pDialog.setCancelable(false);
                            pDialog.show();
                        }
                        else {
                            googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.OFFLINE, "Post Complaint: Offline");
                            middlewareService.saveComplaint(userSession.getUser(), amenity, template, location, description.getText().toString(), cameraHelper.getImageFile(), anonCheckbox.isChecked(), userSession.getUserRevGeocodedLocation());
                            Address bestMatch = new Gson().fromJson(userSession.getUserRevGeocodedLocation(), Address.class);
                            String userLocationString = null;
                            if(bestMatch != null) {
                                userLocationString = bestMatch.getAddressLine(1) + ", " + bestMatch.getAddressLine(2);
                            }
                            ComplaintSavedResponseDto complaintSavedResponseDto = new ComplaintSavedResponseDto();
                            ComplaintSavedEvent event = new ComplaintSavedEvent();
                            complaintSavedResponseDto.setAmenity(amenity);
                            complaintSavedResponseDto.setTemplate(template);
                            complaintSavedResponseDto.setDescription(description.getText().toString());
                            complaintSavedResponseDto.setAnonymous(anonCheckbox.isChecked());
                            complaintSavedResponseDto.setLocationString(userLocationString);
                            event.setComplaintSavedResponseDto(complaintSavedResponseDto);
                            event.setImageFile(cameraHelper.getImageFile());
                            eventBus.post(event);
                        }
                    }
                }
                else {
                    googleAnalyticsTracker.trackAppAction(GoogleAnalyticsTracker.AppAction.ACCESS_DENIED, "Post Complaint: Not logged-in");
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    i.putExtra("MODE", true);
                    startActivityForResult(i, REQUEST_POST_BUTTON);
                }
            }
        });

        descriptionBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                descriptionBtn.setVisibility(View.INVISIBLE);
                description.requestFocus();
                description.setVisibility(View.VISIBLE);
            }
        });

        return rootView;
    }

    private void resetIssueImageView() {
        if(TextUtils.isEmpty(cameraHelper.getImageName())) {
            takePhotoContainer.setVisibility(View.VISIBLE);
            photoTakenContainer.setVisibility(View.GONE);
        } else {
            takePhotoContainer.setVisibility(View.GONE);
            photoTakenContainer.setVisibility(View.VISIBLE);
        }
    }

    private void displayImageIfAvailable() {
        resetIssueImageView();
        if(!TextUtils.isEmpty(cameraHelper.getImageName())) {
            new BitmapWorkerTask(photoDisplay, 200).execute(cameraHelper.getImageName());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        cameraHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cameraHelper.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cameraHelper.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == REQUEST_POST_BUTTON) {
                post.performClick();
            }
        }
    }

    public void onEventMainThread(Location location) {
        this.location = location;
    }

    public void onEventMainThread(ComplaintReportedEvent event) {
        pDialog.dismiss();
        if(event.getSuccess()) {
            ComplaintPostedEvent complaintPostedEvent = new ComplaintPostedEvent();
            complaintPostedEvent.setSuccess(true);
            complaintPostedEvent.setComplaintPostResponseDto(event.getComplaintPostResponseDto());
            complaintPostedEvent.setImageFile(cameraHelper.getImageFile());
            eventBus.post(complaintPostedEvent);
            posted = true;
        }
        else {
            //If the request fails dont go to next screen instead try again
            Toast.makeText(getActivity(), "Complaint save failed. Try again. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCameraPicTaken() {
        displayImageIfAvailable();
    }

    @Override
    public void onGalleryPicChosen() {
        displayImageIfAvailable();
    }
}
