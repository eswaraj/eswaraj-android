package com.eswaraj.app.eswaraj.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.activities.YoutubeActivity;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AmenityBannerFragment extends BaseFragment implements View.OnClickListener {

    private CategoryWithChildCategoryDto amenity;
    private ImageView banner;
    private String video;
    private String pattern = ".*v=(.*)";

    public AmenityBannerFragment() {
        // Required empty public constructor
    }

    public void setAmenity(CategoryWithChildCategoryDto amenity) {
        this.amenity = amenity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extractVideoId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_amenity_banner, container, false);
        banner = (ImageView) rootView.findViewById(R.id.abImage);
        banner.setImageURI(Uri.parse(getActivity().getFilesDir() + "/eSwaraj_" + String.valueOf(amenity.getId()) + ".png"));
        banner.setOnClickListener(this);
        return rootView;
    }


    @Override
    public void onClick(View view) {
        Intent i = new Intent(getActivity(), YoutubeActivity.class);
        i.putExtra("VIDEO_ID", video);
        startActivity(i);
    }

    private void extractVideoId() {
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(amenity.getVideoUrl());
        if (m.find( )) {
            video = m.group(0);
        }
        else {
            video = null;
        }
    }
}
