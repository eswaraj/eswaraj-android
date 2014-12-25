package com.eswaraj.app.eswaraj.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.adapters.TextPagerAdapter;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.FacebookSessionEvent;
import com.eswaraj.app.eswaraj.events.GetUserEvent;
import com.eswaraj.app.eswaraj.events.UserButtonClickEvent;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.util.FacebookLoginUtil;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.eswaraj.app.eswaraj.widgets.CustomViewPager;
import com.facebook.widget.LoginButton;
import com.pnikosis.materialishprogress.ProgressWheel;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class SplashFragment extends BaseFragment {

    //UI elements holders
    Button buttonQuit;
    Button buttonGotIt;
    LoginButton buttonLogin;
    TextView welcomeText;
    ProgressWheel progressWheel;
    CustomViewPager pager;
    private RadioGroup radioGroup;

    @Inject
    FacebookLoginUtil facebookLoginUtil;
    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    UserSessionUtil userSession;

    private Boolean showInstruction;

    public static SplashFragment newInstance() {
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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.registerSticky(this);
        facebookLoginUtil.onCreate(getActivity(), savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
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
        facebookLoginUtil.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        pager.start();
    }

    @Override
    public void onStop() {
        pager.cancel();
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        facebookLoginUtil.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);

        //References to UI elements
        buttonLogin = (LoginButton) view.findViewById(R.id.buttonLogin);
        buttonQuit = (Button) view.findViewById(R.id.buttonQuit);
        buttonGotIt = (Button) view.findViewById(R.id.buttonGotIt);
        welcomeText = (TextView) view.findViewById(R.id.welcomeText);
        progressWheel = (ProgressWheel) view.findViewById(R.id.splashProgressWheel);
        pager = (CustomViewPager) view.findViewById(R.id.viewPager);
        radioGroup = (RadioGroup) view.findViewById(R.id.radiogroup);

        pager.setRadioGroup(radioGroup);

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
                eventBus.post(new UserButtonClickEvent());
            }
        });

        setUpPager();
        return view;
    }
    private void setUpPager() {
        TextPagerAdapter adapter;
        pager.setScrollDurationFactor(10);
        adapter = new TextPagerAdapter(getActivity().getSupportFragmentManager(), getActivity(), R.array.splash_text);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);
        //pager.setOnPageChangeListener(onPageChangeListener);
        pager.setCurrentItem(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookLoginUtil.onActivityResult(requestCode, resultCode, data);
    }

    public void onEventMainThread(FacebookSessionEvent event) {
        if(event.getLogin()) {
            //LoginDone flag will not be updated here since that should happen only once userDto is available
            buttonLogin.setVisibility(View.INVISIBLE);
            progressWheel.setVisibility(View.VISIBLE);
            progressWheel.spin();
            middlewareService.loadUserData(getActivity(), event.getSession());
        }
        else {
            //Update the cache with null to indicate that user has logged out and user object in cache is not valid anymore
            middlewareService.updateUserData(getActivity(), null);
        }
    }

    public void onEventMainThread(GetUserEvent event) {
        if(event.getSuccess()) {
            Log.d("SplashFragment", "GetUserEvent:Success");
            userSession.setUser(event.getUserDto());
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch user details from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
            //Show retry button which will re-trigger the request.
        }
    }
}
