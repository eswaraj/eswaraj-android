package com.eswaraj.app.eswaraj.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;

import com.eswaraj.app.eswaraj.fragments.TextPagerFragment;

public class TextPagerAdapter extends FragmentPagerAdapter {

    LayoutInflater inflater;
    View view;
    String[] splashText;


    public TextPagerAdapter(FragmentManager fragmentManager, Context context, int resourceId) {
        super(fragmentManager);
        splashText = context.getResources().getStringArray(resourceId);

    }

    @Override
    public int getCount() {
        return splashText.length;
    }

    @Override
    public Fragment getItem(int position) {
        return TextPagerFragment.newInstance(splashText[position]);
    }
}
