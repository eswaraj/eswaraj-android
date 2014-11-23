package com.eswaraj.app.eswaraj.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.handlers.LoginButtonClickHandler;
import com.eswaraj.app.eswaraj.handlers.QuitButtonClickHandler;
import com.eswaraj.app.eswaraj.handlers.SkipButtonClickHandler;
import com.eswaraj.app.eswaraj.interfaces.DeviceRegisterInterface;
import com.eswaraj.app.eswaraj.interfaces.FacebookLoginInterface;
import com.eswaraj.app.eswaraj.interfaces.LoginSkipInterface;
import com.eswaraj.app.eswaraj.interfaces.ServerDataInterface;
import com.eswaraj.app.eswaraj.location.LocationUtil;
import com.eswaraj.app.eswaraj.util.FacebookLoginUtil;
import com.eswaraj.app.eswaraj.util.DeviceUtil;
import com.eswaraj.app.eswaraj.util.ServerDataUtil;

/**
 * Use the {@link SplashFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplashFragment extends Fragment implements FacebookLoginInterface, DeviceRegisterInterface, LoginSkipInterface, ServerDataInterface {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //UI elements holders
    Button buttonSkip;
    Button buttonLogin;
    Button buttonQuit;

    //Maintain async task return state
    Boolean loginOrSkipDone;
    Boolean serverDataDownloadDone;
    Boolean redirectDone;

    //Login Utility
    FacebookLoginUtil facebookLoginUtil;
    //Device Register Utility
    DeviceUtil deviceUtil;
    //Location Utility
    LocationUtil locationUtil;
    //ServerData Utility
    ServerDataUtil serverDataUtil;
    //Internet and Location service availability
    Boolean hasNeededServices;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static SplashFragment newInstance(String param1, String param2) {
        SplashFragment fragment = new SplashFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Utilities
        facebookLoginUtil = new FacebookLoginUtil(getActivity());
        deviceUtil = new DeviceUtil(getActivity());
        locationUtil = new LocationUtil(getActivity());
        serverDataUtil = new ServerDataUtil(getActivity());
        //References to buttons
        buttonSkip = (Button) getView().findViewById(R.id.buttonSkip);
        buttonLogin = (Button) getView().findViewById(R.id.buttonLogin);
        buttonQuit = (Button) getView().findViewById(R.id.buttonQuit);
        //Set up initial state
        loginOrSkipDone = false;
        serverDataDownloadDone = false;
        redirectDone = false;

        buttonQuit.setVisibility(View.INVISIBLE);
        //Check if Internet connection and Location services are present. If not, don't proceed.
        hasNeededServices = checkLocationAndInternet();
        if(!hasNeededServices) {
            buttonQuit.setVisibility(View.VISIBLE);
            buttonLogin.setVisibility(View.INVISIBLE);
            buttonSkip.setVisibility(View.INVISIBLE);
        }
        else {
            //Don't display Login and Skip button if user is already logged in.
            if (facebookLoginUtil.isUserLoggedIn()) {
                buttonLogin.setVisibility(View.INVISIBLE);
                buttonSkip.setVisibility(View.INVISIBLE);
            }
            //Start location service
            locationUtil.startLocationService();

            //Start data download from server, if needed
            serverDataUtil.getDataIfNeeded();
        }

        //Register callback handlers
        buttonLogin.setOnClickListener(new LoginButtonClickHandler(getActivity(), facebookLoginUtil));
        buttonQuit.setOnClickListener(new QuitButtonClickHandler(getActivity()));
        buttonSkip.setOnClickListener(new SkipButtonClickHandler(getActivity()));

    }

    private Boolean checkLocationAndInternet() {
        //Return true by default
        //TODO: Implement the checks
        return true;
    }

    private void takeUserToHomeScreen() {
        synchronized(this) {
            if (redirectDone) {
                return;
            } else {
                redirectDone = true;
                //TODO: Launch main activity from here
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }


    @Override
    public void onLoginDone() {
        //Hide login and skip button since login is done
        buttonLogin.setVisibility(View.INVISIBLE);
        buttonSkip.setVisibility(View.INVISIBLE);
        //Register the device now
        deviceUtil.startDeviceRegistration();
    }

    @Override
    public void onDeviceRegistered() {
        //Any registration dependent actions can be taken here
        loginOrSkipDone = true;
        if(serverDataDownloadDone) {
            takeUserToHomeScreen();
        }
    }

    @Override
    public void onSkipDone() {
        //Hide login and skip button since login is done
        buttonLogin.setVisibility(View.INVISIBLE);
        buttonSkip.setVisibility(View.INVISIBLE);
        loginOrSkipDone = true;
        if(serverDataDownloadDone) {
            takeUserToHomeScreen();
        }
    }

    @Override
    public void onServerDataAvailable() {
        //Hide login and skip button since login is done
        buttonLogin.setVisibility(View.INVISIBLE);
        buttonSkip.setVisibility(View.INVISIBLE);
        serverDataDownloadDone = true;
        if(loginOrSkipDone) {
            takeUserToHomeScreen();
        }
    }
}
