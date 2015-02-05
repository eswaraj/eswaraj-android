package com.next.eswaraj.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseFragment;
import com.next.eswaraj.events.BannerClickEvent;
import com.next.eswaraj.helpers.GoogleAnalyticsTracker;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class AmenityBannerFragment extends BaseFragment implements View.OnClickListener {

    @Inject
    EventBus eventBus;
    @Inject
    GoogleAnalyticsTracker googleAnalyticsTracker;

    private CategoryWithChildCategoryDto amenity;
    private ImageView banner;
    private ImageView videoLink;
    private TextView amenityName;
    private TextView issueCount;
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
        videoLink = (ImageView) rootView.findViewById(R.id.video_click_link);
        amenityName = (TextView) rootView.findViewById(R.id.issue_name);
        issueCount = (TextView) rootView.findViewById(R.id.issue_issues);

        banner.setImageURI(Uri.parse(getActivity().getFilesDir() + "/eSwaraj_banner_" + String.valueOf(amenity.getId()) + ".png"));
        videoLink.setOnClickListener(this);
        amenityName.setText(amenity.getName());
        issueCount.setText(Integer.toString(amenity.getChildCategories().size()) + " issues");
        return rootView;
    }


    @Override
    public void onClick(View view) {
        googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "AmenityBanner: Video = " + video);
        BannerClickEvent event = new BannerClickEvent();
        event.setSuccess(true);
        event.setVideo(video);
        eventBus.post(event);
    }

    private void extractVideoId() {
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(amenity.getVideoUrl());
        if (m.find( )) {
            video = m.group(1);
        }
        else {
            video = null;
        }
    }
}
