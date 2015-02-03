package com.eswaraj.app.eswaraj.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.models.PoliticalBodyAdminDto;
import com.squareup.picasso.Picasso;


public class LeaderForComplaintFragment extends BaseFragment {

    private ImageView photo;
    private TextView name;
    private TextView details;
    private TextView message;

    private PoliticalBodyAdminDto politicalBodyAdminDto;

    public LeaderForComplaintFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_leader_for_complaint, container, false);
        photo = (ImageView) rootView.findViewById(R.id.lfcPhoto);
        name = (TextView) rootView.findViewById(R.id.lfcName);
        details = (TextView) rootView.findViewById(R.id.lfcDetails);
        message = (TextView) rootView.findViewById(R.id.lfcMessage);
        setFields();
        return rootView;
    }

    public void setPoliticalBodyAdminDto(PoliticalBodyAdminDto politicalBodyAdminDto) {
        this.politicalBodyAdminDto = politicalBodyAdminDto;
        setFields();
    }

    private void setFields() {
        if(photo != null && politicalBodyAdminDto != null) {
            name.setText(politicalBodyAdminDto.getName());
            details.setText(politicalBodyAdminDto.getPoliticalAdminType().getShortName() + ", " + politicalBodyAdminDto.getLocation().getName());
            message.setText("Thank you for using eSwaraj. Together we will offer better support to Government");
            if(politicalBodyAdminDto.getProfilePhoto() != null && !politicalBodyAdminDto.getProfilePhoto().equals("")) {
                Picasso.with(getActivity()).load(politicalBodyAdminDto.getProfilePhoto().replace("http:", "https:") + "?type=large").error(R.drawable.anon).placeholder(R.drawable.anon).into(photo);
            }
            else {
                photo.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.anon));
            }
        }
    }
}

