package com.eswaraj.app.eswaraj.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Button;

import com.eswaraj.app.eswaraj.fragments.SplashFragment;
import com.eswaraj.app.eswaraj.models.SplashScreenItem;

import java.util.ArrayList;

public class TextPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<SplashScreenItem> splashScreenItems;
    private Button.OnClickListener onClickListenerContinue;
    private Button.OnClickListener onClickListenerRetry;
    private SplashFragment lastFragment;
    private Boolean showContinueButton = false;
    private Boolean showRetryButton = false;

    public TextPagerAdapter(FragmentManager fragmentManager, ArrayList<SplashScreenItem> splashScreenItems) {
        super(fragmentManager);
        this.splashScreenItems = splashScreenItems;
    }

    public void setOnClickListenerContinue(Button.OnClickListener onClickListener) {
        this.onClickListenerContinue = onClickListener;
    }

    public void setOnClickListenerRetry(Button.OnClickListener onClickListener) {
        this.onClickListenerRetry = onClickListener;
    }

    @Override
    public int getCount() {
        return splashScreenItems.size();
    }

    @Override
    public Fragment getItem(int position) {
        SplashFragment fragment = new SplashFragment();
        fragment.setSplashScreenItem(splashScreenItems.get(position));
        fragment.addRadioButtonsAndSetActive(splashScreenItems.size(), position);
        if(position == splashScreenItems.size() - 1) {
            lastFragment = fragment;
            lastFragment.showSpinner();
            if(showContinueButton) {
                lastFragment.showContinueButton();
            }
            if(showRetryButton) {
                lastFragment.showRetryButton();
            }
            fragment.setOnClickListenerContinue(onClickListenerContinue);
            fragment.setOnClickListenerRetry(onClickListenerRetry);
        }
        return fragment;
    }

    public void showContinueButton() {
        if(lastFragment != null) {
            lastFragment.showContinueButton();
        }
        else {
            showContinueButton = true;
        }
    }

    public void showRetryButton() {
        if(lastFragment != null) {
            lastFragment.showRetryButton();
        }
        else {
            showRetryButton = true;
        }
    }

    public void showSpinner() {
        lastFragment.showSpinner();
    }
}
