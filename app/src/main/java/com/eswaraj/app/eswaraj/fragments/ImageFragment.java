package com.eswaraj.app.eswaraj.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.config.ImageType;
import com.eswaraj.app.eswaraj.datastore.StorageCache;
import com.eswaraj.app.eswaraj.events.GetComplaintImageEvent;
import com.eswaraj.app.eswaraj.events.GetProfileImageEvent;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;

import java.io.File;

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

    public void setImage(String path, Long id, ImageType type) {
        this.path = path;
        this.id = id;
        if(type == ImageType.COMPLAINT) {
            middlewareService.loadComplaintImage(getActivity(), path, id);
        }
        else if(type == ImageType.PROFILE) {
            middlewareService.loadProfileImage(getActivity(), path, id);
        }
    }

    public void onEventMainThread(GetComplaintImageEvent event) {
        if(event.getSuccess()) {
            iImage.setImageBitmap(storageCache.getBitmap(id, getActivity(), ImageType.COMPLAINT));
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch complaint image. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

    public void onEventMainThread(GetProfileImageEvent event) {
        if(event.getSuccess()) {
            iImage.setImageBitmap(storageCache.getBitmap(id, getActivity(), ImageType.PROFILE));
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch profile image. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }
}
