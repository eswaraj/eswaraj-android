package com.next.eswaraj.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseFragment;
import com.next.eswaraj.config.ImageType;
import com.next.eswaraj.datastore.StorageCache;
import com.next.eswaraj.events.GetComplaintImageEvent;
import com.next.eswaraj.events.GetProfileImageEvent;
import com.next.eswaraj.middleware.MiddlewareServiceImpl;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class ImageFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    StorageCache storageCache;

    private ImageView iImage;
    private String path;
    private Long id;

    public ImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        iImage = (ImageView) rootView.findViewById(R.id.iImage);
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

    public void setImage(String path, Long id, ImageType type, Boolean keep) {
        this.path = path;
        this.id = id;
        if(type == ImageType.COMPLAINT) {
            middlewareService.loadComplaintImage(getActivity(), path, id, keep);
        }
        else if(type == ImageType.PROFILE) {
            middlewareService.loadProfileImage(getActivity(), path, id, keep);
        }
    }

    public void onEventMainThread(GetComplaintImageEvent event) {
        if(event.getSuccess()) {
            iImage.setImageBitmap(event.getBitmap());
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch complaint image. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

    public void onEventMainThread(GetProfileImageEvent event) {
        if(event.getSuccess()) {
            iImage.setImageBitmap(event.getBitmap());
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch profile image. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }
}
