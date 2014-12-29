package com.eswaraj.app.eswaraj.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.adapters.TextPagerAdapter;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.UserReadyEvent;
import com.eswaraj.app.eswaraj.models.SplashScreenItem;
import com.eswaraj.app.eswaraj.widgets.CustomViewPager;

import java.util.ArrayList;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class SplashFragment extends BaseFragment {

    @Inject
    EventBus eventBus;

    private CustomViewPager pager;
    private TextPagerAdapter adapter;
    private ArrayList<SplashScreenItem> splashScreenItems;
    private View.OnClickListener onClickListener;

    public SplashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_splash, container, false);
        pager = (CustomViewPager) rootView.findViewById(R.id.viewPager);
        setUpListener();
        setUpPagerData();
        setUpPager();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void setUpPager() {
        pager.setScrollDurationFactor(2);
        adapter = new TextPagerAdapter(getActivity().getSupportFragmentManager(), splashScreenItems);
        adapter.setOnClickListener(onClickListener);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);
        pager.setCurrentItem(0);
    }

    private void setUpPagerData() {
        splashScreenItems = new ArrayList<SplashScreenItem>();
        splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswarajlogo),"Welcome to eSwaraj", "A Mobile & Web Platform for better Governance"));
        splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswarajposter),"About eSwaraj","eSwaraj is for everyone. It enables citizens to report issues in their neighbourhood and provides trends and statistics to the government to govern better."));
        splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.mobile),"Why eSwaraj ?","Let's try to understand the need for eSwaraj."));
        splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswaraj01),"Signing the constitution", "We came together and signed a contract named Constitution which paved the way for founding of this great nation. A contract that promised that our lives would be much better by being part of this nation. A contract that promised that together we can be much more than we are as individuals."));
        splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswaraj02), "Administrative Structure", "A system of administration was laid out to achieve the promises in the Constitution. Considering everyone's similar basic needs, India was divided in multiple parts, each with same administrative and political structure."));
        splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswaraj03), "The need for social audit", "But then, Even the greatest plans fall short if ground-level feedback is not taken from end beneficiaries. eSwaraj aims to remove the disconnect between ground level realities and top level perception and bring transparency in quality of service delivery and governance."));
        splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswaraj04), "Analytics and More", "Visual analytics offering deeper insights to improve governance Per constituency/colony based map view to locate problems"));
    }

    private void setUpListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserReadyEvent event = new UserReadyEvent();
                event.setSuccess(true);
                eventBus.post(event);
            }
        };
    }

    public void readyToProceed() {
        adapter.showProceedButton();
    }
    
}
