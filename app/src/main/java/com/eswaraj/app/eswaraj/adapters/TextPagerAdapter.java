package com.eswaraj.app.eswaraj.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.Button;

import com.eswaraj.app.eswaraj.fragments.TextPagerFragment;
import com.eswaraj.app.eswaraj.models.SplashScreenItem;

import java.util.ArrayList;

public class TextPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<SplashScreenItem> splashScreenItems;
    private Button.OnClickListener onClickListener;

    public TextPagerAdapter(FragmentManager fragmentManager, ArrayList<SplashScreenItem> splashScreenItems) {
        super(fragmentManager);
        this.splashScreenItems = splashScreenItems;
    }

    public void setOnClickListener(Button.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getCount() {
        return splashScreenItems.size();
    }

    @Override
    public Fragment getItem(int position) {
        TextPagerFragment fragment = new TextPagerFragment();
        fragment.setSplashScreenItem(splashScreenItems.get(position));
        if(position == splashScreenItems.size() - 1) {
            fragment.setShowButton(true);
            fragment.setOnClickListener(onClickListener);
        }
        return fragment;
    }
}
