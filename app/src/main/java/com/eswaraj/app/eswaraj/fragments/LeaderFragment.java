package com.eswaraj.app.eswaraj.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.StartAnotherActivityEvent;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.PoliticalBodyAdminDto;
import com.eswaraj.app.eswaraj.widgets.CustomNetworkImageView;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class LeaderFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;

    private CustomNetworkImageView lPhoto;
    private TextView lName;
    private TextView lPost;
    private WebView lDetails;
    private Button lConstituency;

    private PoliticalBodyAdminDto politicalBodyAdminDto;

    public LeaderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_leader, container, false);
        lPhoto = (CustomNetworkImageView) rootView.findViewById(R.id.lPhoto);
        lName = (TextView) rootView.findViewById(R.id.lName);
        lPost = (TextView) rootView.findViewById(R.id.lPost);
        lDetails = (WebView) rootView.findViewById(R.id.lDetails);
        lConstituency = (Button) rootView.findViewById(R.id.lConstituency);

        politicalBodyAdminDto = (PoliticalBodyAdminDto) getActivity().getIntent().getSerializableExtra("LEADER");

        lPhoto.loadProfileImage(politicalBodyAdminDto.getProfilePhoto(), politicalBodyAdminDto.getId());

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

}
