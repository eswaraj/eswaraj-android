package com.eswaraj.app.eswaraj.fragments;

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

public class TextPagerFragment extends Fragment {
    private TextView mText;
    private ImageView mImage;
    private Button mButton;

    private SplashScreenItem splashScreenItem;
    private Boolean showButton = false;
    private Button.OnClickListener onClickListener;


    public void setShowButton(Boolean showButton) {
        this.showButton = showButton;
    }


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
        mText = (TextView) rootView.findViewById(R.id.splash_pager_text);
        mImage = (ImageView) rootView.findViewById(R.id.splash_pager_image);
        mButton = (Button) rootView.findViewById(R.id.splash_pager_button);
        mText.setTypeface(custom_font);
        mButton.setOnClickListener(onClickListener);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mText.setText(splashScreenItem.getText());
        mImage.setImageDrawable(splashScreenItem.getImage());
        if(showButton) {
            mButton.setVisibility(View.VISIBLE);
        }
    }
}
