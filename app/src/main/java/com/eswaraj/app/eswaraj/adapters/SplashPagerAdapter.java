package com.eswaraj.app.eswaraj.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Button;

import com.eswaraj.app.eswaraj.fragments.SplashFragment;
import com.eswaraj.app.eswaraj.models.SplashScreenItem;

import java.util.ArrayList;

public class SplashPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<SplashScreenItem> splashScreenItems;
    private SplashFragment lastFragment;

    public SplashPagerAdapter(FragmentManager fragmentManager, ArrayList<SplashScreenItem> splashScreenItems) {
        super(fragmentManager);
        this.splashScreenItems = splashScreenItems;
    }

    @Override
    public int getCount() {
        return splashScreenItems.size();
    }

    @Override
    public Fragment getItem(int position) {
        SplashFragment fragment = new SplashFragment();
        fragment.setSplashScreenItem(splashScreenItems.get(position));
        return fragment;
    }

}
