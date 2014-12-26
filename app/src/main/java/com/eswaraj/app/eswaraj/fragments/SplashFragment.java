package com.eswaraj.app.eswaraj.fragments;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.adapters.TextPagerAdapter;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.interfaces.OnSwipeOutListenerInterface;
import com.eswaraj.app.eswaraj.models.SplashScreenItem;
import com.eswaraj.app.eswaraj.widgets.CustomViewPager;

import java.util.ArrayList;


public class SplashFragment extends BaseFragment {

    private CustomViewPager pager;
    private ArrayList<SplashScreenItem> splashScreenItems;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private OnSwipeOutListenerInterface onSwipeOutListenerInterface;
    private View.OnClickListener onClickListener;
    private int selectedPage;

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
        pager.setScrollDurationFactor(10);
        adapter = new TextPagerAdapter(getActivity().getSupportFragmentManager(), splashScreenItems);
        adapter.setOnClickListener(onClickListener);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);
        pager.setOnPageChangeListener(onPageChangeListener);
        pager.setOnSwipeOutListener(onSwipeOutListenerInterface);
        pager.setCurrentItem(0);
    }

    private void setUpPagerData() {
        splashScreenItems = new ArrayList<SplashScreenItem>();
        splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.constituency), "Some dummy text1"));
        splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.leader), "Some dummy text2"));
    }

    private void setUpListener() {
        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectedPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        onSwipeOutListenerInterface = new OnSwipeOutListenerInterface() {
            @Override
            public void onSwipeOutAtStart() {

            }

            @Override
            public void onSwipeOutAtEnd() {
                Log.d("SplashFragment", "Trying to go out of bound");
            }
        };

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Splash", "Ready");
            }
        };
    }

    
}
