package com.eswaraj.app.eswaraj.fragments;

import android.annotation.TargetApi;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.models.SplashScreenItem;

public class SplashFragment extends Fragment {

    private TextView mText;
    private TextView mHeading;
    private ImageView mImage;
    private View root;

    private SplashScreenItem splashScreenItem;

    public  void setSplashScreenItem (SplashScreenItem splashScreenItem) {
        this.splashScreenItem = splashScreenItem;
    }

    public SplashFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_splash, container, false);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/HandmadeTypewriter.ttf");
        root = rootView;
        mText = (TextView) rootView.findViewById(R.id.splash_pager_text);
        mImage = (ImageView) rootView.findViewById(R.id.splash_pager_image);
        mHeading = (TextView) rootView.findViewById(R.id.splash_pager_heading);

        //mText.setTypeface(custom_font);

        return rootView;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //TODO:Remove the bg setting from here if needed as it is only support from api level 16
        if(splashScreenItem.getBgImage() != null) {
            root.setBackground(splashScreenItem.getBgImage());
        }

        mText.setText(splashScreenItem.getText());
        //mText.setTextSize(18);
        //mText.setTextColor(Color.parseColor("#e47979"));

        mHeading.setText(splashScreenItem.getHeading());
        //mHeading.setTextSize(40);
        //mHeading.setPadding(0,30,0,0);
        //mHeading.setTextColor(Color.parseColor("#ea5c5c"));

        if(splashScreenItem.getImage() != null) {
            mImage.setImageDrawable(splashScreenItem.getImage());
            mImage.setVisibility(View.VISIBLE);
        }
        //mImage.setPadding(0, 10,0,0 );
    }

}
