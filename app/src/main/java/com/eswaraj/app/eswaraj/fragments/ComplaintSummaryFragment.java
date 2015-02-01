package com.eswaraj.app.eswaraj.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.UserContinueEvent;
import com.eswaraj.app.eswaraj.helpers.BitmapWorkerTask;
import com.eswaraj.app.eswaraj.helpers.GoogleAnalyticsTracker;
import com.eswaraj.app.eswaraj.models.ComplaintPostResponseDto;
import com.eswaraj.app.eswaraj.models.PoliticalBodyAdminDto;
import com.eswaraj.app.eswaraj.util.FacebookSharingUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class ComplaintSummaryFragment extends BaseFragment implements OnMapReadyCallback {

    @Inject
    EventBus eventBus;
    @Inject
    FacebookSharingUtil facebookSharingUtil;
    @Inject
    GoogleAnalyticsTracker googleAnalyticsTracker;

    private GoogleMapFragment googleMapFragment;
    private File imageFile;
    private ComplaintPostResponseDto complaintPostResponseDto;

    private TextView rootCategory;
    private TextView subCategory;
    private ImageView complaintPhoto;
    private TextView address;
    private TextView description;
    private Button done;
    private Button another;
    private ImageView facebook;
    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    private ImageView back;
    private ImageView forward;
    private TextView descriptionLabel;

    public ComplaintSummaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_complaint_summary, container, false);
        pager = (ViewPager) rootView.findViewById(R.id.csPager);
        forward = (ImageView) rootView.findViewById(R.id.csForward);
        back = (ImageView) rootView.findViewById(R.id.csBack);
        rootCategory = (TextView) rootView.findViewById(R.id.csRootCategory);
        subCategory = (TextView) rootView.findViewById(R.id.csSubCategory);
        address = (TextView) rootView.findViewById(R.id.csAddress);
        description = (TextView) rootView.findViewById(R.id.csDescription);
        descriptionLabel = (TextView) rootView.findViewById(R.id.csDescriptionLabel);
        complaintPhoto = (ImageView) rootView.findViewById(R.id.csComplaintPhoto);
        done = (Button) rootView.findViewById(R.id.csDone);
        another = (Button) rootView.findViewById(R.id.csAnother);
        facebook = (ImageView) rootView.findViewById(R.id.csFacebook);

        description.setMovementMethod(new ScrollingMovementMethod());

        googleMapFragment = new GoogleMapFragment();
        getChildFragmentManager().beginTransaction().add(R.id.csMapContainer, googleMapFragment).commit();
        googleMapFragment.setContext(this);

        imageFile = (File) getActivity().getIntent().getSerializableExtra("IMAGE");
        complaintPostResponseDto = (ComplaintPostResponseDto) getActivity().getIntent().getSerializableExtra("COMPLAINT");

        //Fill all complaint related details
        if(complaintPostResponseDto.getComplaintDto().getDescription() != null && !complaintPostResponseDto.getComplaintDto().getDescription().equals("")) {
            description.setText(complaintPostResponseDto.getComplaintDto().getDescription());
        }
        else {
            descriptionLabel.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
        }
        address.setText(complaintPostResponseDto.getComplaintDto().getLocationString());

        if(imageFile != null) {
            new BitmapWorkerTask(complaintPhoto, 200).execute(imageFile.getAbsolutePath());
        }
        else {
            complaintPhoto.setVisibility(View.GONE);
        }

        rootCategory.setText(complaintPostResponseDto.getAmenity().getName());
        subCategory.setText(complaintPostResponseDto.getTemplate().getName());

        if(complaintPostResponseDto.getPoliticalBodyAdminDtoList().size() > 0) {
            pagerAdapter = new LeaderSlidePagerAdapter(getChildFragmentManager(), complaintPostResponseDto.getPoliticalBodyAdminDtoList());
        }
        else {
            List<PoliticalBodyAdminDto> politicalBodyAdminDtoListDummy = new ArrayList<>();
            PoliticalBodyAdminDto leaderDummy = new PoliticalBodyAdminDto();
            leaderDummy.setName("Neta ji");
            leaderDummy.getPoliticalAdminType().setShortName("Elected Leader");
            leaderDummy.getLocation().setName("Your Constituency");
            leaderDummy.setProfilePhoto("");
            politicalBodyAdminDtoListDummy.add(leaderDummy);
            pagerAdapter = new LeaderSlidePagerAdapter(getChildFragmentManager(), politicalBodyAdminDtoListDummy);
            Toast.makeText(getActivity(), "Service not available in this location yet. Coming soon...", Toast.LENGTH_LONG).show();
        }
        if(complaintPostResponseDto.getPoliticalBodyAdminDtoList().size() < 2) {
            forward.setVisibility(View.GONE);
            back.setVisibility(View.GONE);
        }
        pager.setAdapter(pagerAdapter);

        done.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserContinueEvent event = new UserContinueEvent();
                event.setSuccess(true);
                event.setAnother(false);
                eventBus.post(event);
            }

        });

        another.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserContinueEvent event = new UserContinueEvent();
                event.setSuccess(true);
                event.setAnother(true);
                eventBus.post(event);
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "ComplaintSummary: Share Complaint");
                if(complaintPostResponseDto.getComplaintDto().getImages() != null) {
                    facebookSharingUtil.shareComplaintWithImage((Activity) v.getContext(), "Submitted new complaint using eSwaraj", complaintPostResponseDto.getComplaintDto().getDescription(), complaintPostResponseDto.getComplaintDto().getImages().get(0).getOrgUrl(), "http://dev.eswaraj.com/complaint/" + complaintPostResponseDto.getComplaintDto().getId() + ".html");
                }
                else {
                    facebookSharingUtil.shareComplaint((Activity) v.getContext(), "Submitted new complaint using eSwaraj", complaintPostResponseDto.getComplaintDto().getDescription(), "http://dev.eswaraj.com/complaint/" + complaintPostResponseDto.getComplaintDto().getId() + ".html");
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(pager.getCurrentItem() - 1, true);
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(pager.getCurrentItem() + 1, true);
            }
        });
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapFragment.disableGestures();
        googleMapFragment.updateMarkerLocation(complaintPostResponseDto.getComplaintDto().getLattitude(), complaintPostResponseDto.getComplaintDto().getLongitude());
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookSharingUtil.onCreate(getActivity(), savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        facebookSharingUtil.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        facebookSharingUtil.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        facebookSharingUtil.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookSharingUtil.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        facebookSharingUtil.onSaveInstanceState(outState);
    }


    private class LeaderSlidePagerAdapter extends FragmentStatePagerAdapter {

        private List<PoliticalBodyAdminDto> politicalBodyAdminDtoList;

        public LeaderSlidePagerAdapter(FragmentManager fm, List<PoliticalBodyAdminDto> politicalBodyAdminDtoList) {
            super(fm);
            this.politicalBodyAdminDtoList = politicalBodyAdminDtoList;
        }

        @Override
        public Fragment getItem(int position) {
            LeaderForComplaintFragment leaderForComplaintFragment = new LeaderForComplaintFragment();
            leaderForComplaintFragment.setPoliticalBodyAdminDto(politicalBodyAdminDtoList.get(position));
            return leaderForComplaintFragment;
        }

        @Override
        public int getCount() {
            return politicalBodyAdminDtoList.size();
        }
    }
}
