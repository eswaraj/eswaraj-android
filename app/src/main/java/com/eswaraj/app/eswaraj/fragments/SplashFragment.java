package com.eswaraj.app.eswaraj.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.activities.SplashActivity;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.interfaces.FacebookLoginInterface;
import com.eswaraj.app.eswaraj.util.FacebookLoginUtil;
import com.facebook.Session;
import com.facebook.widget.LoginButton;

import javax.inject.Inject;


public class SplashFragment extends BaseFragment implements FacebookLoginInterface {

    //UI elements holders
    Button buttonQuit;
    LoginButton buttonLogin;

    @Inject
    FacebookLoginUtil facebookLoginUtil;

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

        //References to buttons
        buttonLogin = (LoginButton) getView().findViewById(R.id.buttonLogin);
        buttonQuit = (Button) getView().findViewById(R.id.buttonQuit);

        //Set up initial state
        buttonQuit.setVisibility(View.INVISIBLE);

        //Register callback handlers
        buttonQuit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
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
            //Don't display Login and Skip button if user is already logged in.
            if (facebookLoginUtil.isUserLoggedIn()) {
                buttonLogin.setVisibility(View.INVISIBLE);
            }
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        return view;
    }


    @Override
    public void onFacebookLoginDone(Session session) {
        //Hide login and skip button since login is done
        //LoginDone flag will not be updated here since that should happen only once userDto is available
        //buttonLogin.setVisibility(View.INVISIBLE);
        ((SplashActivity)getActivity()).onFacebookLoginDone(session);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookLoginUtil.onActivityResult(requestCode, resultCode, data);
    }
}
