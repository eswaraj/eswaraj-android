package com.eswaraj.app.eswaraj.fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.text.method.ScrollingMovementMethod;
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
import com.eswaraj.app.eswaraj.models.ComplaintSavedResponseDto;
import com.eswaraj.app.eswaraj.models.PoliticalBodyAdminDto;
import com.eswaraj.app.eswaraj.util.FacebookSharingUtil;
import com.google.android.gms.maps.GoogleMap;

import java.io.File;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class ComplaintSummaryOfflineFragment extends BaseFragment {


    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;

    private File imageFile;
    private ComplaintSavedResponseDto complaintSavedResponseDto;

    private TextView rootCategory;
    private TextView subCategory;
    private ImageView complaintPhoto;
    private TextView address;
    private TextView description;
    private Button done;
    private Button another;
    private TextView descriptionLabel;


    public ComplaintSummaryOfflineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_complaint_summary_offline, container, false);
        rootCategory = (TextView) rootView.findViewById(R.id.csRootCategory);
        subCategory = (TextView) rootView.findViewById(R.id.csSubCategory);
        address = (TextView) rootView.findViewById(R.id.csAddress);
        description = (TextView) rootView.findViewById(R.id.csDescription);
        complaintPhoto = (ImageView) rootView.findViewById(R.id.csComplaintPhoto);
        done = (Button) rootView.findViewById(R.id.csDone);
        another = (Button) rootView.findViewById(R.id.csAnother);

        description.setMovementMethod(new ScrollingMovementMethod());

        imageFile = (File) getActivity().getIntent().getSerializableExtra("IMAGE");
        complaintSavedResponseDto = (ComplaintSavedResponseDto) getActivity().getIntent().getSerializableExtra("COMPLAINT");

        //Fill all complaint related details
        description.setText(complaintSavedResponseDto.getDescription());
        if(complaintSavedResponseDto.getDescription() != null && !complaintSavedResponseDto.getDescription().equals("")) {
            description.setText(complaintSavedResponseDto.getDescription());
        }
        else {
            descriptionLabel.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
        }
        address.setText(complaintSavedResponseDto.getLocationString());

        if(imageFile != null) {
            new BitmapWorkerTask(complaintPhoto, 200).execute(imageFile.getAbsolutePath());
        }
        else {
            complaintPhoto.setVisibility(View.GONE);
        }

        rootCategory.setText(complaintSavedResponseDto.getAmenity().getName());
        subCategory.setText(complaintSavedResponseDto.getTemplate().getName());


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

        return rootView;
    }

}
