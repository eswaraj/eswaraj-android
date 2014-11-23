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
import com.eswaraj.app.eswaraj.interfaces.DeviceRegisterInterface;
import com.eswaraj.app.eswaraj.interfaces.FacebookLoginInterface;
import com.eswaraj.app.eswaraj.util.FacebookLoginUtil;

/**
 * Use the {@link SplashFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplashFragment extends Fragment implements FacebookLoginInterface, DeviceRegisterInterface {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //UI elements holders
    Button buttonContinue;
    Button buttonLogin;

    //Login Utility
    FacebookLoginUtil facebookLoginUtil;
    //Device Register Utility
    DeviceUtil deviceUtil;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SplashFragment.
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
        //Login utility
        facebookLoginUtil = new FacebookLoginUtil(getActivity());
        //Device register utility
        deviceUtil = new DeviceUtil(getActivity());
        //References to buttons
        buttonContinue = (Button) getView().findViewById(R.id.buttonContinue);
        buttonLogin = (Button) getView().findViewById(R.id.buttonLogin);

        //Don't display Continue button until login and data download is complete
        buttonContinue.setVisibility(View.INVISIBLE);

        //Don't display Login button if user is already logged in.
        if(facebookLoginUtil.isUserLoggedIn()) {
            buttonLogin.setVisibility(View.INVISIBLE);
        }

        //User login. It will callback onLoginDone()
        buttonLogin.setOnClickListener(new LoginButtonClickHandler(getActivity(), facebookLoginUtil));
        //Register device if needed
        //Prepare data. On completion, it will call appReadyForLaunch()
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
        //Hide login button since login is done
        buttonLogin.setVisibility(View.INVISIBLE);
        //Register the device now
    }

    @Override
    public void onDeviceRegistered() {
        //Start the download of data here
    }
}
