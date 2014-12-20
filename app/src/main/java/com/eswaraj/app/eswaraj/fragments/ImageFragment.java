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
import com.eswaraj.app.eswaraj.events.GetComplaintImageEvent;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;

import java.io.File;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class ImageFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;

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

    public void setImage(String path, Long id) {
        this.path = path;
        this.id = id;
        if(path != null) {
            middlewareService.loadComplaintImage(getActivity(), path, id);
        }
    }

    public void onEventMainThread(GetComplaintImageEvent event) {
        if(event.getSuccess()) {
            File f = new File(getActivity().getFilesDir() + "/eSwaraj_complaint_" + id + ".png");
            if(f.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                iImage.setImageBitmap(bitmap);
            }
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch complaint image. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }
}
