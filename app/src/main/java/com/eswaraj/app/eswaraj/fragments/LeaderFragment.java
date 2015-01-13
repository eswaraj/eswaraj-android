package com.eswaraj.app.eswaraj.fragments;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.GetProfileImageEvent;
import com.eswaraj.app.eswaraj.events.StartAnotherActivityEvent;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.PoliticalBodyAdminDto;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class LeaderFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;

    private ImageView lPhoto;
    private TextView lName;
    private TextView lPost;
    private WebView lDetails;
    private Button lConstituency;

    private PoliticalBodyAdminDto politicalBodyAdminDto;
    private Bitmap photoBitmap;

    public LeaderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);
        politicalBodyAdminDto = (PoliticalBodyAdminDto) getActivity().getIntent().getSerializableExtra("LEADER");
        middlewareService.loadProfileImage(getActivity(), politicalBodyAdminDto.getProfilePhoto(), politicalBodyAdminDto.getId(), true, false);
    }

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_leader, container, false);
        lPhoto = (ImageView) rootView.findViewById(R.id.lPhoto);
        lName = (TextView) rootView.findViewById(R.id.lName);
        lPost = (TextView) rootView.findViewById(R.id.lPost);
        lDetails = (WebView) rootView.findViewById(R.id.lDetails);
        lConstituency = (Button) rootView.findViewById(R.id.lConstituency);

        setupMenu(rootView.findViewById(R.id.menu));

        if(photoBitmap != null) {
            lPhoto.setImageBitmap(photoBitmap);
        }

        lName.setText(politicalBodyAdminDto.getName());
        lPost.setText(politicalBodyAdminDto.getPoliticalAdminTypeDto().getShortName() + ", " + politicalBodyAdminDto.getLocation().getName());
        lConstituency.setText("Go to " + politicalBodyAdminDto.getLocation().getName());
        if(politicalBodyAdminDto.getBiodata() != null) {
            lDetails.loadData(politicalBodyAdminDto.getBiodata(), "text/html", null);
        }
        lConstituency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartAnotherActivityEvent event = new StartAnotherActivityEvent();
                event.setId(politicalBodyAdminDto.getLocation().getId());
                eventBus.post(event);
            }
        });
        return rootView;
    }

    public void onEventMainThread(GetProfileImageEvent event) {
        if(event.getSuccess()) {
            if(lPhoto != null) {
                lPhoto.setImageBitmap(event.getBitmap());
            }
            else {
                photoBitmap = event.getBitmap();
            }
        }
    }

    public static Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri;
        try {
            pm.getPackageInfo("com.facebook.katana", 0);
            // http://stackoverflow.com/a/24547437/1048340
            uri = Uri.parse("fb://facewebmodal/f?href=" + url);
        } catch (PackageManager.NameNotFoundException e) {
            uri = Uri.parse(url);
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }
}
