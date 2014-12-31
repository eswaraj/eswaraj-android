package com.eswaraj.app.eswaraj.fragments;


import android.content.Intent;
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
import com.eswaraj.app.eswaraj.events.ProfileUpdateEvent;
import com.eswaraj.app.eswaraj.events.StartAnotherActivityEvent;
import com.eswaraj.app.eswaraj.events.UserContinueEvent;
import com.eswaraj.app.eswaraj.middleware.MiddlewareService;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.eswaraj.app.eswaraj.widgets.CustomProgressDialog;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class MyProfileFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    UserSessionUtil userSession;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    StorageCache storageCache;

    private ImageView mpPhoto;
    private TextView mpName;
    private EditText mpInputName;
    private ImageView mpMarkLocationButton;
    private Button mpSave;
    private Button mpCancel;

    private CustomProgressDialog pDialog;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pDialog = new CustomProgressDialog(getActivity(), false, true, "Getting profile updates...");
        pDialog.show();
        eventBus.register(this);
        middlewareService.loadProfileUpdates(getActivity(), userSession.getToken());
        middlewareService.loadProfileImage(getActivity(), userSession.getProfilePhoto(), userSession.getUser().getPerson().getId());

    }

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_profile, container, false);
        mpPhoto = (ImageView) rootView.findViewById(R.id.mpPhoto);
        mpName = (TextView) rootView.findViewById(R.id.mpName);
        mpInputName = (EditText) rootView.findViewById(R.id.mpInputName);
        mpMarkLocationButton = (ImageView) rootView.findViewById(R.id.mpMarkLocation);
        mpSave = (Button) rootView.findViewById(R.id.mpSave);
        mpCancel = (Button) rootView.findViewById(R.id.mpCancel);

        mpName.setText(userSession.getUser().getPerson().getName());
        mpInputName.setText(userSession.getUser().getPerson().getName());

        mpMarkLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartAnotherActivityEvent event = new StartAnotherActivityEvent();
                event.setSuccess(true);
                eventBus.post(event);
            }
        });

        mpSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                middlewareService.updateProfile(getActivity(), userSession.getToken(), mpInputName.getText().toString(), null, null);
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
        return rootView;
    }

    public void onEventMainThread(GetProfileEvent event) {
        if(event.getSuccess()) {
            userSession.setUser(event.getUserDto());
        }
        else {
            Toast.makeText(getActivity(), "Could not get profile updates from server" + event.getError(), Toast.LENGTH_LONG).show();
        }
        pDialog.dismiss();
    }

    public void onEventMainThread(GetProfileImageEvent event) {
        if(event.getSuccess()) {
            mpPhoto.setImageBitmap(storageCache.getBitmap(userSession.getUser().getPerson().getId(), getActivity(), ImageType.PROFILE));
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch your profile image. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

    public void onEventMainThread(ProfileUpdateEvent event) {
        if(event.getSuccess()) {
            userSession.setUser(event.getUserDto());
            pDialog.dismiss();
        }
        else {
            Toast.makeText(getActivity(), "Could not save changes to server. Please retry. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }
}
