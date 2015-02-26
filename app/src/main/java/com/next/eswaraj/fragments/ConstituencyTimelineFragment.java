package com.next.eswaraj.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eswaraj.web.dto.LocationDto;
import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseFragment;
import com.next.eswaraj.config.TimelineType;


public class ConstituencyTimelineFragment extends BaseFragment {

    private TimelineFragment timelineFragment;
    private LocationDto locationDto;

    public ConstituencyTimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {
            timelineFragment = new TimelineFragment();
            getChildFragmentManager().beginTransaction().add(R.id.ctTimeline, timelineFragment).commit();
        }
        locationDto = (LocationDto) getActivity().getIntent().getSerializableExtra("LOCATION");
        timelineFragment.setTypeAndId(TimelineType.LOCATION, locationDto.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_constituency_timeline, container, false);
        return rootView;
    }


}
