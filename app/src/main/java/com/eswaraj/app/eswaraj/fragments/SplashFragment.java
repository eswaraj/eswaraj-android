package com.eswaraj.app.eswaraj.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.interfaces.FacebookLoginInterface;
import com.eswaraj.app.eswaraj.interfaces.LaunchNextActivityInterface;
import com.eswaraj.app.eswaraj.util.FacebookLoginUtil;
import com.facebook.Session;
import com.facebook.widget.LoginButton;
import com.pnikosis.materialishprogress.ProgressWheel;

import javax.inject.Inject;


public class SplashFragment extends BaseFragment implements FacebookLoginInterface {

    //UI elements holders
    Button buttonQuit;
    Button buttonGotIt;
    LoginButton buttonLogin;
    TextView welcomeText;
    ProgressWheel progressWheel;

    @Inject
    FacebookLoginUtil facebookLoginUtil;

    private Boolean showInstruction;

    public static SplashFragment newInstance(String param1, String param2) {
        SplashFragment fragment = new SplashFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //References to UI elements
        buttonLogin = (LoginButton) getView().findViewById(R.id.buttonLogin);
        buttonQuit = (Button) getView().findViewById(R.id.buttonQuit);
        buttonGotIt = (Button) getView().findViewById(R.id.buttonGotIt);
        welcomeText = (TextView) getView().findViewById(R.id.welcomeText);
        progressWheel = (ProgressWheel) getView().findViewById(R.id.splashProgressWheel);

        //Set up initial state
        buttonQuit.setVisibility(View.INVISIBLE);
        buttonGotIt.setVisibility(View.INVISIBLE);
        progressWheel.setVisibility(View.INVISIBLE);
        welcomeText.setText("Lets be the change we want to see in the world.\nLets play our part in betterment of nation through click of a button.\n Lets live the dream of Swaraj");

        //Register callback handlers
        buttonQuit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        buttonGotIt.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LaunchNextActivityInterface) v.getContext()).takeUserToNextScreen();
            }
        });

        //Any setup needed
        facebookLoginUtil.setup(buttonLogin);

    }

    public void notifyServiceAvailability(Boolean hasNeededServices) {
        if(!hasNeededServices) {
            buttonQuit.setVisibility(View.VISIBLE);
            buttonLogin.setVisibility(View.INVISIBLE);
        }
        else {
            if (facebookLoginUtil.isUserLoggedIn()) {
                buttonLogin.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setShowInstruction(Boolean show) {
        showInstruction = show;
    }

    public void notifyAppReady() {
        buttonLogin.setVisibility(View.INVISIBLE);
        buttonQuit.setVisibility(View.INVISIBLE);
        buttonGotIt.setVisibility(View.VISIBLE);
        progressWheel.setVisibility(View.INVISIBLE);
        if(showInstruction) {
            welcomeText.setText("The app is setup.\n\nOn the next screen you will be asked to mark your home location on a map.\n\nThis is a one-time activity and will help us serve you data about your constituency.");
            buttonGotIt.setVisibility(View.VISIBLE);
        }
        else {
            ((LaunchNextActivityInterface) getActivity()).takeUserToNextScreen();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookLoginUtil.onCreate(this, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        facebookLoginUtil.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        facebookLoginUtil.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        facebookLoginUtil.onResume(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        facebookLoginUtil.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        return view;
    }


    @Override
    public void onFacebookLoginDone(Session session) {
        //LoginDone flag will not be updated here since that should happen only once userDto is available
        buttonLogin.setVisibility(View.INVISIBLE);
        progressWheel.setVisibility(View.VISIBLE);
        progressWheel.spin();
        ((FacebookLoginInterface)getActivity()).onFacebookLoginDone(session);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookLoginUtil.onActivityResult(requestCode, resultCode, data);
    }
}
