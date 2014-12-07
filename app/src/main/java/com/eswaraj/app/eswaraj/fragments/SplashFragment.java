package com.eswaraj.app.eswaraj.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesImagesEvent;
import com.eswaraj.app.eswaraj.events.GetUserEvent;
import com.eswaraj.app.eswaraj.handlers.QuitButtonClickHandler;
import com.eswaraj.app.eswaraj.interfaces.FacebookLoginInterface;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.util.FacebookLoginUtil;
import com.eswaraj.app.eswaraj.util.InternetServicesCheckUtil;
import com.eswaraj.app.eswaraj.util.LocationServicesCheckUtil;
import com.eswaraj.web.dto.UserDto;
import com.facebook.widget.LoginButton;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Use the {@link SplashFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplashFragment extends Fragment implements FacebookLoginInterface {

    //Logged-in user
    UserDto userDto;

    //Next activity that will be launched
    Activity nextActivity;

    //UI elements holders
    Button buttonSkip;
    //Button buttonLogin;
    Button buttonQuit;
    LoginButton buttonLogin;

    //Maintain async task return state
    Boolean loginOrSkipDone;
    Boolean serverDataDownloadDone;
    Boolean redirectDone;

    @Inject
    FacebookLoginUtil facebookLoginUtil;
    @Inject
    InternetServicesCheckUtil internetServicesCheckUtil;
    @Inject
    LocationServicesCheckUtil locationServicesCheckUtil;
    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;

    //Internet and Location service availability
    Boolean hasNeededServices;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
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
        buttonSkip = (Button) getView().findViewById(R.id.buttonSkip);
        buttonLogin = (LoginButton) getView().findViewById(R.id.buttonLogin);
        buttonQuit = (Button) getView().findViewById(R.id.buttonQuit);

        //Set up initial state
        loginOrSkipDone = false;
        serverDataDownloadDone = false;
        redirectDone = false;
        hasNeededServices = false;
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
        }

        //Register callback handlers
        //buttonLogin.setOnClickListener(new LoginButtonClickHandler(getActivity(), facebookLoginUtil));
        buttonQuit.setOnClickListener(new QuitButtonClickHandler(getActivity()));
        //buttonSkip.setOnClickListener(new SkipButtonClickHandler((LoginSkipInterface)getActivity()));

        //Any setup needed
        facebookLoginUtil.setup(buttonLogin);

    }

    private Boolean checkLocationAndInternet() {
        return internetServicesCheckUtil.isServiceAvailable(getActivity()) && locationServicesCheckUtil.isServiceAvailable(getActivity());
    }

    private void takeUserToNextScreen() {
        if(!hasNeededServices) {
            return;
        }
        synchronized(this) {
            if (redirectDone) {
                return;
            } else {
                redirectDone = true;
                //TODO: Launch main activity from here using this.nextActivity
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookLoginUtil.onCreate(this, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        eventBus.registerSticky(this);

    }

    @Override
    public void onStop() {
        eventBus.unregister(this);
        super.onStop();
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
    public void onFacebookLoginDone() {
        //Hide login and skip button since login is done
        //LoginDone flag will not be updated here since that should happen only once userDto is available
        buttonLogin.setVisibility(View.INVISIBLE);
        buttonSkip.setVisibility(View.INVISIBLE);
        Log.d("SplashFragment", "onFacebookLoginDone");
    }


    //@Override
    public void onSkipDone() {
        //Hide login and skip button since login is done
        buttonLogin.setVisibility(View.INVISIBLE);
        buttonSkip.setVisibility(View.INVISIBLE);
        loginOrSkipDone = true;
        if(serverDataDownloadDone) {
            takeUserToNextScreen();
        }
    }

    public void onEventMainThread(GetCategoriesDataEvent event) {
        if(event.getSuccess()) {
            //Launch image download now. Always launch with dontGetFromCache=true
            middlewareService.loadCategoriesImages(getActivity(), event.getCategoryList(), true);
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch categories from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
            //Show retry button which will re-trigger the request.
        }
    }

    public void onEventMainThread(GetUserEvent event) {
        if(event.getSuccess()) {
            this.userDto = event.getUserDto();
            loginOrSkipDone = true;
            if(this.userDto.getPerson().getPersonAddress().getLongitude() != null) {
                //this.nextActivity = HomeActivity.class;
            }
            else {
                //this.nextActivity = MarkLocationActivity.class;
            }
            if(serverDataDownloadDone) {
                takeUserToNextScreen();
            }
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch user details from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
            //Show retry button which will re-trigger the request.
        }
    }

    public void onEventMainThread(GetCategoriesImagesEvent event) {
        if(event.getSuccess()) {
            serverDataDownloadDone = true;
            if (loginOrSkipDone) {
                takeUserToNextScreen();
            }
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch categories images from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
            //Show retry button which will re-trigger the request.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookLoginUtil.onActivityResult(requestCode, resultCode, data);
    }
}
