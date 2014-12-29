package com.eswaraj.app.eswaraj.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.models.SplashScreenItem;
import com.pnikosis.materialishprogress.ProgressWheel;

public class TextPagerFragment extends Fragment {

    private TextView mText;
    private TextView mHeading;
    private ImageView mImage;
    private Button mButton;
    private ProgressWheel progressWheel;
    private View root;

    private SplashScreenItem splashScreenItem;
    private Button.OnClickListener onClickListener;

    private Boolean showSpinner = false;


    public void setOnClickListener(Button.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public  void setSplashScreenItem (SplashScreenItem splashScreenItem) {
        this.splashScreenItem = splashScreenItem;
    }

    public TextPagerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.splash_pager, container, false);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/HandmadeTypewriter.ttf");
        root = rootView;
        mText = (TextView) rootView.findViewById(R.id.splash_pager_text);
        mImage = (ImageView) rootView.findViewById(R.id.splash_pager_image);
        mHeading = (TextView) rootView.findViewById(R.id.splash_pager_heading);
        mButton = (Button) rootView.findViewById(R.id.splash_pager_button);
        progressWheel = (ProgressWheel) rootView.findViewById(R.id.splashProgressWheel);
        mText.setTypeface(custom_font);

        if(showSpinner) {
            progressWheel.setVisibility(View.VISIBLE);
        }
        progressWheel.spin();
        mButton.setOnClickListener(onClickListener);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        root.setBackground(splashScreenItem.getBgImage());

        mText.setText(splashScreenItem.getText());
        mText.setTextSize(18);
        mText.setTextColor(Color.parseColor("#e47979"));

        mHeading.setText(splashScreenItem.getHeading());
        mHeading.setTextSize(40);
        mHeading.setPadding(0,30,0,0);
        mHeading.setTextColor(Color.parseColor("#ea5c5c"));

        mImage.setImageDrawable(splashScreenItem.getImage());
        mImage.setPadding(0, 10,0,0 );
    }

    public void showContinueButton() {
        progressWheel.setVisibility(View.INVISIBLE);
        mButton.setVisibility(View.VISIBLE);
    }

    public void showSpinner() {
        if(progressWheel != null) {
            progressWheel.setVisibility(View.VISIBLE);
        }
        else {
            showSpinner = true;
        }
    }
}
