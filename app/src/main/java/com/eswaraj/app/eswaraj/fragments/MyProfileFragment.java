package com.eswaraj.app.eswaraj.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.activities.HomeActivity;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.config.ImageType;
import com.eswaraj.app.eswaraj.datastore.StorageCache;
import com.eswaraj.app.eswaraj.events.GetProfileEvent;
import com.eswaraj.app.eswaraj.events.GetProfileImageEvent;
import com.eswaraj.app.eswaraj.events.LoginStatusEvent;
import com.eswaraj.app.eswaraj.events.ProfileUpdateEvent;
import com.eswaraj.app.eswaraj.events.StartAnotherActivityEvent;
import com.eswaraj.app.eswaraj.events.UserContinueEvent;
import com.eswaraj.app.eswaraj.helpers.GoogleAnalyticsTracker;
import com.eswaraj.app.eswaraj.interfaces.BitmapWorkerCallback;
import com.eswaraj.app.eswaraj.middleware.MiddlewareService;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.util.GenericUtil;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.eswaraj.app.eswaraj.widgets.CustomProgressDialog;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.squareup.picasso.Picasso;

import java.util.Date;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class MyProfileFragment extends BaseFragment implements OnMapReadyCallback {

    @Inject
    EventBus eventBus;
    @Inject
    UserSessionUtil userSession;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    GoogleAnalyticsTracker googleAnalyticsTracker;

    private ImageView mpPhoto;
    private TextView mpName;
    private EditText mpNameInput;
    private TextView mpEditName;
    private TextView mpVoterId;
    private EditText mpVoterIdInput;
    private TextView mpEditVoterId;
    private TextView mpLocation;
    private TextView mpAc;
    private TextView mpPc;
    private TextView mpWard;
    private TextView mpMarkLocationButton;
    private Button mpSave;
    private Button mpCancel;
    private Button mpLogout;
    private GoogleMapFragment googleMapFragment;
    //private TextView mpUserDetails;

    private CustomProgressDialog pDialog;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        eventBus.register(this);
        middlewareService.loadProfileImage(getActivity(), userSession.getProfilePhoto(), userSession.getUser().getPerson().getId(), false);
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_profile, container, false);
        mpPhoto = (ImageView) rootView.findViewById(R.id.mpPhoto);
        mpName = (TextView) rootView.findViewById(R.id.mpName);
        mpNameInput = (EditText) rootView.findViewById(R.id.mpNameInput);
        mpEditName = (TextView) rootView.findViewById(R.id.mpEditName);
        mpVoterId = (TextView) rootView.findViewById(R.id.mpVoterId);
        mpVoterIdInput = (EditText) rootView.findViewById(R.id.mpVoterIdInput);
        mpEditVoterId = (TextView) rootView.findViewById(R.id.mpEditVoterId);
        mpLocation = (TextView) rootView.findViewById(R.id.mpLocation);
        mpAc = (TextView) rootView.findViewById(R.id.mpAc);
        mpPc = (TextView) rootView.findViewById(R.id.mpPc);
        mpWard = (TextView) rootView.findViewById(R.id.mpWard);
        mpMarkLocationButton = (TextView) rootView.findViewById(R.id.mpMarkLocation);
        mpSave = (Button) rootView.findViewById(R.id.mpSave);
        mpCancel = (Button) rootView.findViewById(R.id.mpCancel);
        mpLogout = (Button) rootView.findViewById(R.id.mpLogout);
        //mpUserDetails = (TextView) rootView.findViewById(R.id.mcUserDetails);

        mpName.setText(userSession.getUser().getPerson().getName());
        mpNameInput.setText(userSession.getUser().getPerson().getName());
        mpVoterId.setText(userSession.getUser().getPerson().getVoterId());
        mpVoterIdInput.setText(userSession.getUser().getPerson().getVoterId());
        Picasso.with(getActivity()).load(userSession.getProfilePhoto().replace("http", "https")).error(R.drawable.anon).placeholder(R.drawable.anon).into(mpPhoto);
        if(userSession.isUserLocationKnown()) {
            if(userSession.getUser().getPerson().getPersonAddress().getAc() != null && userSession.getUser().getPerson().getPersonAddress().getState() != null) {
                mpLocation.setText(userSession.getUser().getPerson().getPersonAddress().getAc().getName() + ", " + userSession.getUser().getPerson().getPersonAddress().getState().getName());
            }
            else {
                mpLocation.setText("");
            }
            if(userSession.getUser().getPerson().getPersonAddress().getAc() != null) {
                mpAc.setText(userSession.getUser().getPerson().getPersonAddress().getAc().getName());
            }
            else {
                mpAc.setText("");
            }
            if(userSession.getUser().getPerson().getPersonAddress().getPc() != null) {
                mpPc.setText(userSession.getUser().getPerson().getPersonAddress().getPc().getName());
            }
            else {
                mpPc.setText("");
            }
            if(userSession.getUser().getPerson().getPersonAddress().getWard() != null) {
                mpWard.setText(userSession.getUser().getPerson().getPersonAddress().getWard().getName());
            }
            else {
                mpWard.setText("");
            }
        }
        else {
            mpLocation.setText("");
            mpAc.setText("");
            mpPc.setText("");
            mpWard.setText("");
        }
        //mpInputName.setText(userSession.getUser().getPerson().getName());
        //if(userSession.getUser().getPerson().getDob() != null) {
        //    mpUserDetails.setText(GenericUtil.getAge(userSession.getUser().getPerson().getDob()) + " Years, " + userSession.getUser().getPerson().getGender());
        //}

        if(savedInstanceState == null) {
            googleMapFragment = new GoogleMapFragment();
            googleMapFragment.setContext(this);
            getChildFragmentManager().beginTransaction().add(R.id.mpMap, googleMapFragment).commit();
        }

        mpEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpName.setVisibility(View.GONE);
                mpNameInput.setVisibility(View.VISIBLE);
                mpNameInput.requestFocus();
            }
        });

        mpEditVoterId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpVoterId.setVisibility(View.GONE);
                mpVoterIdInput.setVisibility(View.VISIBLE);
                mpVoterIdInput.requestFocus();
            }
        });

        mpMarkLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "MyProfile: Mark Location");
                StartAnotherActivityEvent event = new StartAnotherActivityEvent();
                event.setSuccess(true);
                eventBus.post(event);
            }
        });

        mpSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "MyProfile: Save Profile");
                middlewareService.updateProfile(getActivity(), userSession.getToken(), mpNameInput.getText().toString(), mpVoterIdInput.getText().toString(), null, null);
                pDialog = new CustomProgressDialog(getActivity(), false, true, "Saving changes...");
                pDialog.show();
            }
        });

        mpCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserContinueEvent event = new UserContinueEvent();
                event.setSuccess(true);
                eventBus.post(event);
            }
        });

        mpLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "MyProfile: Logout");
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                alertDialogBuilder
                        .setTitle("Logout from eSwaraj")
                        .setMessage("Are you sure you want to logout?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userSession.logoutUser(getActivity());
                                LoginStatusEvent event = new LoginStatusEvent();
                                event.setLoggedIn(false);
                                event.setSuccess(true);
                                eventBus.post(event);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        return rootView;
    }

    public void onEventMainThread(ProfileUpdateEvent event) {
        if(event.getSuccess()) {
            userSession.setUser(event.getUserDto());
            mpName.setText(userSession.getUser().getPerson().getName());
            mpName.setVisibility(View.VISIBLE);
            mpNameInput.setVisibility(View.GONE);

            mpVoterId.setText(userSession.getUser().getPerson().getVoterId());
            mpVoterId.setVisibility(View.VISIBLE);
            mpVoterIdInput.setVisibility(View.GONE);
        }
        else {
            Toast.makeText(getActivity(), "Could not save changes to server. Please retry. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
        pDialog.dismiss();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(userSession.getUser().getPerson().getPersonAddress() != null) {
            if (userSession.getUser().getPerson().getPersonAddress().getLattitude() != null) {
                googleMapFragment.updateMarkerLocation(userSession.getUser().getPerson().getPersonAddress().getLattitude(), userSession.getUser().getPerson().getPersonAddress().getLongitude());
            }
        }
    }

    public void updateUserLocationDisplay() {
        if(userSession.getUser().getPerson().getPersonAddress() != null) {
            if (userSession.getUser().getPerson().getPersonAddress().getLattitude() != null) {
                googleMapFragment.updateMarkerLocation(userSession.getUser().getPerson().getPersonAddress().getLattitude(), userSession.getUser().getPerson().getPersonAddress().getLongitude());
            }
        }
        if(userSession.isUserLocationKnown()) {
            if(userSession.getUser().getPerson().getPersonAddress().getAc() != null && userSession.getUser().getPerson().getPersonAddress().getState() != null) {
                mpLocation.setText(userSession.getUser().getPerson().getPersonAddress().getAc().getName() + ", " + userSession.getUser().getPerson().getPersonAddress().getState().getName());
            }
            else {
                mpLocation.setText("");
            }
            if(userSession.getUser().getPerson().getPersonAddress().getAc() != null) {
                mpAc.setText(userSession.getUser().getPerson().getPersonAddress().getAc().getName());
            }
            else {
                mpAc.setText("");
            }
            if(userSession.getUser().getPerson().getPersonAddress().getPc() != null) {
                mpPc.setText(userSession.getUser().getPerson().getPersonAddress().getPc().getName());
            }
            else {
                mpPc.setText("");
            }
            if(userSession.getUser().getPerson().getPersonAddress().getWard() != null) {
                mpWard.setText(userSession.getUser().getPerson().getPersonAddress().getWard().getName());
            }
            else {
                mpWard.setText("");
            }
        }
        else {
            mpLocation.setText("");
            mpAc.setText("");
            mpPc.setText("");
            mpWard.setText("");
        }
    }
}
