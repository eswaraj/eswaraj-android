package com.eswaraj.app.eswaraj.fragments;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.models.SplashScreenItem;
import com.pnikosis.materialishprogress.ProgressWheel;

public class SplashFragment extends Fragment {

    private TextView mText;
    private TextView mHeading;
    private ImageView mImage;
    private Button mButtonContinue;
    private Button mButtonRetry;
    private ProgressWheel progressWheel;
    private RadioGroup radioGroup;
    private View root;

    private SplashScreenItem splashScreenItem;
    private Button.OnClickListener onClickListenerContinue;
    private Button.OnClickListener onClickListenerRetry;

    private Boolean showSpinner = false;
    private Boolean showContinueButton = false;
    private Boolean showRetryButton = false;
    private Boolean showRadioGroup = true;

    private Integer count;
    private Integer active;


    public void setOnClickListenerContinue(Button.OnClickListener onClickListener) {
        this.onClickListenerContinue = onClickListener;
    }

    public void setOnClickListenerRetry(Button.OnClickListener onClickListener) {
        this.onClickListenerRetry = onClickListener;
    }

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
        View rootView = inflater.inflate(R.layout.splash_pager, container, false);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/HandmadeTypewriter.ttf");
        root = rootView;
        mText = (TextView) rootView.findViewById(R.id.splash_pager_text);
        mImage = (ImageView) rootView.findViewById(R.id.splash_pager_image);
        mHeading = (TextView) rootView.findViewById(R.id.splash_pager_heading);
        mButtonContinue = (Button) rootView.findViewById(R.id.splash_pager_button_continue);
        mButtonRetry = (Button) rootView.findViewById(R.id.splash_pager_button_retry);
        progressWheel = (ProgressWheel) rootView.findViewById(R.id.splashProgressWheel);
        radioGroup = (RadioGroup) rootView.findViewById(R.id.splashRadioGroup);
        mText.setTypeface(custom_font);

        if(showSpinner) {
            mButtonRetry.setVisibility(View.INVISIBLE);
            mButtonContinue.setVisibility(View.INVISIBLE);
            radioGroup.setVisibility(View.INVISIBLE);
            progressWheel.setVisibility(View.VISIBLE);
        }
        if(showContinueButton) {
            progressWheel.setVisibility(View.INVISIBLE);
            mButtonRetry.setVisibility(View.INVISIBLE);
            radioGroup.setVisibility(View.INVISIBLE);
            mButtonContinue.setVisibility(View.VISIBLE);
        }
        if(showRetryButton) {
            progressWheel.setVisibility(View.INVISIBLE);
            mButtonContinue.setVisibility(View.INVISIBLE);
            radioGroup.setVisibility(View.INVISIBLE);
            mButtonRetry.setVisibility(View.VISIBLE);
        }
        if(showRadioGroup) {
            progressWheel.setVisibility(View.INVISIBLE);
            mButtonContinue.setVisibility(View.INVISIBLE);
            mButtonRetry.setVisibility(View.INVISIBLE);
            radioGroup.setVisibility(View.VISIBLE);
        }

        if(count != null) {
            for(int i = 0; i < count; i++) {
                RadioButton radioButton = new RadioButton(getActivity());
                radioButton.setId(i);
                radioButton.setEnabled(false);
                radioGroup.addView(radioButton);
            }
            radioGroup.check(active);
            radioGroup.setEnabled(false);
        }
        progressWheel.spin();
        mButtonContinue.setOnClickListener(onClickListenerContinue);
        mButtonRetry.setOnClickListener(onClickListenerRetry);
        return rootView;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //TODO:Remove the bg setting from here if needed as it is only support from api level 16
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
        if(mButtonContinue != null) {
            progressWheel.setVisibility(View.INVISIBLE);
            mButtonRetry.setVisibility(View.INVISIBLE);
            radioGroup.setVisibility(View.INVISIBLE);
            mButtonContinue.setVisibility(View.VISIBLE);
        }
        else {
            showContinueButton = true;
            showSpinner = false;
            showRetryButton = false;
            showRadioGroup = false;
        }
    }

    public void showRetryButton() {
        if(mButtonRetry != null) {
            progressWheel.setVisibility(View.INVISIBLE);
            mButtonContinue.setVisibility(View.INVISIBLE);
            radioGroup.setVisibility(View.INVISIBLE);
            mButtonRetry.setVisibility(View.VISIBLE);
        }
        else {
            showRetryButton = true;
            showContinueButton = false;
            showSpinner = false;
            showRadioGroup = false;
        }
    }

    public void showSpinner() {
        if(progressWheel != null) {
            mButtonRetry.setVisibility(View.INVISIBLE);
            mButtonContinue.setVisibility(View.INVISIBLE);
            radioGroup.setVisibility(View.INVISIBLE);
            progressWheel.setVisibility(View.VISIBLE);
        }
        else {
            showSpinner = true;
            showContinueButton = false;
            showRetryButton = false;
            showRadioGroup = false;
        }
    }

    public void addRadioButtonsAndSetActive(int count, int active) {
        if(count < 2) {
            return;
        }
        if(radioGroup == null) {
            this.count = count;
            this.active = active;
            return;
        }
        for(int i = 0; i < count; i++) {
            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setId(i);
            radioButton.setEnabled(false);
            radioGroup.addView(radioButton);
        }
        radioGroup.check(active);
        radioGroup.setEnabled(false);
    }
}
