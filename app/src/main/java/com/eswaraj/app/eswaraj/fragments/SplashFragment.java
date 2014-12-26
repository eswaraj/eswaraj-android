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
        TextPagerAdapter adapter;
        pager.setScrollDurationFactor(2);
        adapter = new TextPagerAdapter(getActivity().getSupportFragmentManager(), splashScreenItems);
        adapter.setOnClickListener(onClickListener);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);
        pager.setCurrentItem(0);
    }

    private void setUpPagerData() {
        splashScreenItems = new ArrayList<SplashScreenItem>();
        splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.constituency),"Some heading1", "Some dummy text1"));
        splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.leader), "Some heading2", "Some dummy text2"));
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
    
}
