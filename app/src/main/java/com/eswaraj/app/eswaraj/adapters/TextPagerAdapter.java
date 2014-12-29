package com.eswaraj.app.eswaraj.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Button;

import com.eswaraj.app.eswaraj.fragments.SplashFragment;
import com.eswaraj.app.eswaraj.fragments.TextPagerFragment;
import com.eswaraj.app.eswaraj.models.SplashScreenItem;

import java.util.ArrayList;

public class TextPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<SplashScreenItem> splashScreenItems;
    private Button.OnClickListener onClickListener;
    private TextPagerFragment lastFragment;
    private Boolean showButton = false;

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
            lastFragment = fragment;
            lastFragment.showSpinner();
            if(showButton) {
                lastFragment.showContinueButton();
            }
            fragment.setOnClickListener(onClickListener);
        }
        return fragment;
    }

    public void showProceedButton() {
        if(lastFragment != null) {
            lastFragment.showContinueButton();
        }
        else {
            showButton = true;
        }
    }
}
