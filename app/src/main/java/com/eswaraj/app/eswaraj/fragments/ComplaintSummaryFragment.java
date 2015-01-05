package com.eswaraj.app.eswaraj.fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.GetProfileImageEvent;
import com.eswaraj.app.eswaraj.events.UserContinueEvent;
import com.eswaraj.app.eswaraj.helpers.BitmapWorkerTask;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.ComplaintPostResponseDto;
import com.eswaraj.app.eswaraj.models.PoliticalBodyAdminDto;
import com.eswaraj.app.eswaraj.util.FacebookSharingUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.File;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class ComplaintSummaryFragment extends BaseFragment implements OnMapReadyCallback {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    FacebookSharingUtil facebookSharingUtil;

    private GoogleMapFragment googleMapFragment;
    private File imageFile;
    private ComplaintPostResponseDto complaintPostResponseDto;

    private TextView mlaName;
    private ImageView mlaPhoto;
    private TextView mlaLocation;
    private TextView rootCategory;
    private TextView subCategory;
    private ImageView complaintPhoto;
    private TextView address;
    private TextView description;
    private Button done;
    private Button another;
    private ImageView facebook;

    private Long id;
    private Bitmap mlaBitmap;

    public ComplaintSummaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_complaint_summary, container, false);
        mlaName = (TextView) rootView.findViewById(R.id.csMlaName);
        mlaLocation = (TextView) rootView.findViewById(R.id.csMlaLocation);
        mlaPhoto = (ImageView) rootView.findViewById(R.id.csMlaPhoto);
        rootCategory = (TextView) rootView.findViewById(R.id.csRootCategory);
        subCategory = (TextView) rootView.findViewById(R.id.csSubCategory);
        address = (TextView) rootView.findViewById(R.id.csAddress);
        description = (TextView) rootView.findViewById(R.id.csDescription);
        complaintPhoto = (ImageView) rootView.findViewById(R.id.csComplaintPhoto);
        done = (Button) rootView.findViewById(R.id.csDone);
        another = (Button) rootView.findViewById(R.id.csAnother);
        facebook = (ImageView) rootView.findViewById(R.id.csFacebook);

        googleMapFragment = new GoogleMapFragment();
        getChildFragmentManager().beginTransaction().add(R.id.csMapContainer, googleMapFragment).commit();
        googleMapFragment.setContext(this);

        imageFile = (File) getActivity().getIntent().getSerializableExtra("IMAGE");
        complaintPostResponseDto = (ComplaintPostResponseDto) getActivity().getIntent().getSerializableExtra("COMPLAINT");

        //Fill all complaint related details
        description.setText(complaintPostResponseDto.getComplaintDto().getDescription());
        address.setText(complaintPostResponseDto.getComplaintDto().getLocationString());

        if(imageFile != null) {
            new BitmapWorkerTask(complaintPhoto, 200).execute(imageFile.getAbsolutePath());
        }

        rootCategory.setText(complaintPostResponseDto.getAmenity().getName());
        subCategory.setText(complaintPostResponseDto.getTemplate().getName());


        //Fill all admin related details
        for(PoliticalBodyAdminDto politicalBodyAdminDto : complaintPostResponseDto.getPoliticalBodyAdminDtoList()) {
            if(politicalBodyAdminDto.getPoliticalAdminTypeDto().getShortName().equals("CM")) {
                mlaName.setText(politicalBodyAdminDto.getName());
                mlaLocation.setText(politicalBodyAdminDto.getPoliticalAdminTypeDto().getShortName() + ", " + politicalBodyAdminDto.getLocation().getName());
                if(!politicalBodyAdminDto.getProfilePhoto().equals("")) {
                    id = politicalBodyAdminDto.getId();
                    middlewareService.loadProfileImage(getActivity(), politicalBodyAdminDto.getProfilePhoto().replace("http", "https"), politicalBodyAdminDto.getId(), false);
                }
            }
        }
        if(mlaBitmap != null) {
            mlaPhoto.setImageBitmap(mlaBitmap);
        }


        done.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserContinueEvent event = new UserContinueEvent();
                event.setSuccess(true);
                event.setAnother(false);
                eventBus.post(event);
            }

        });

        another.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserContinueEvent event = new UserContinueEvent();
                event.setSuccess(true);
                event.setAnother(true);
                eventBus.post(event);
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(complaintPostResponseDto.getComplaintDto().getImages() != null) {
                    facebookSharingUtil.shareComplaintWithImage((Activity) v.getContext(), "Submitted new complaint using eSwaraj", complaintPostResponseDto.getComplaintDto().getDescription(), complaintPostResponseDto.getComplaintDto().getImages().get(0).getOrgUrl(), "http://dev.eswaraj.com/complaint/" + complaintPostResponseDto.getComplaintDto().getId() + ".html");
                }
                else {
                    facebookSharingUtil.shareComplaint((Activity) v.getContext(), "Submitted new complaint using eSwaraj", complaintPostResponseDto.getComplaintDto().getDescription(), "http://dev.eswaraj.com/complaint/" + complaintPostResponseDto.getComplaintDto().getId() + ".html");
                }
            }
        });
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapFragment.disableGestures();
        googleMapFragment.updateMarkerLocation(complaintPostResponseDto.getComplaintDto().getLattitude(), complaintPostResponseDto.getComplaintDto().getLongitude());
    }

    public void onEventMainThread(GetProfileImageEvent event) {
        if(event.getSuccess()) {
            if(mlaPhoto != null) {
                mlaPhoto.setImageBitmap(event.getBitmap());
            }
            else {
                mlaBitmap = event.getBitmap();
            }
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch MLA image. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);
        facebookSharingUtil.onCreate(getActivity(), savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        facebookSharingUtil.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        facebookSharingUtil.onPause();
    }

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
        facebookSharingUtil.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookSharingUtil.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        facebookSharingUtil.onSaveInstanceState(outState);
    }
}
