package com.eswaraj.app.eswaraj.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.FacebookSessionEvent;
import com.eswaraj.app.eswaraj.events.GetUserEvent;
import com.eswaraj.app.eswaraj.events.LoginStatusEvent;
import com.eswaraj.app.eswaraj.events.UserContinueEvent;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.util.FacebookLoginUtil;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.facebook.Session;
import com.facebook.widget.LoginButton;
import com.pnikosis.materialishprogress.ProgressWheel;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class LoginFragment extends BaseFragment {

    //UI elements holders
    Button buttonRetry;
    Button buttonGotIt;
    TextView buttonSkip;
    LoginButton buttonLogin;
    TextView welcomeText;
    ProgressWheel progressWheel;

    @Inject
    FacebookLoginUtil facebookLoginUtil;
    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    UserSessionUtil userSession;

    private Boolean showInstruction;
    private Boolean dialogMode;
    private Session session;

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public LoginFragment() {
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
            buttonLogin.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "Internet and Location services not found", Toast.LENGTH_LONG).show();
        }
        /*
        else {
            if (facebookLoginUtil.isUserLoggedIn()) {
                buttonLogin.setVisibility(View.INVISIBLE);
            }
        }
        */
    }

    public void setShowInstruction(Boolean show) {
        showInstruction = show;
    }

    public void notifyAppReady() {
        buttonLogin.setVisibility(View.INVISIBLE);
        buttonRetry.setVisibility(View.INVISIBLE);
        buttonSkip.setVisibility(View.INVISIBLE);
        if(showInstruction) {
            welcomeText.setText("The app is setup.\n\nOn the next screen you will be asked to mark your home location on a map.\n\nThis is a one-time activity and will help us serve you data about your constituency.");
            progressWheel.setVisibility(View.INVISIBLE);
            buttonGotIt.setVisibility(View.VISIBLE);
        }
        else {
            UserContinueEvent event = new UserContinueEvent();
            event.setLoggedIn(true);
            eventBus.post(event);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);
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
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        facebookLoginUtil.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        if(dialogMode) {
            view = inflater.inflate(R.layout.fragment_login_dialog, container, false);
        }
        else {
            view = inflater.inflate(R.layout.fragment_login, container, false);
        }
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/HandmadeTypewriter.ttf");

        if(!dialogMode) {
            //References to UI elements
            buttonLogin = (LoginButton) view.findViewById(R.id.buttonLogin);
            buttonRetry = (Button) view.findViewById(R.id.buttonRetry);
            buttonGotIt = (Button) view.findViewById(R.id.buttonGotIt);
            buttonSkip = (TextView) view.findViewById(R.id.buttonSkip);
            welcomeText = (TextView) view.findViewById(R.id.welcomeText);
            progressWheel = (ProgressWheel) view.findViewById(R.id.loginProgressWheel);


            //Set up initial state
            buttonRetry.setVisibility(View.INVISIBLE);
            buttonGotIt.setVisibility(View.INVISIBLE);
            progressWheel.setVisibility(View.INVISIBLE);
            if (facebookLoginUtil.isUserLoggedIn()) {
                buttonLogin.setVisibility(View.INVISIBLE);
            }
            welcomeText.setText("Lets be the change we want to see in the world.\nLets play our part in betterment of nation through click of a button.\n Lets live the dream of Swaraj");
            welcomeText.setTypeface(custom_font);

            //Register callback handlers
            buttonRetry.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    middlewareService.loadUserData(getActivity(), session);
                    progressWheel.setVisibility(View.VISIBLE);
                }
            });
            buttonGotIt.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserContinueEvent event = new UserContinueEvent();
                    event.setLoggedIn(true);
                    eventBus.post(event);
                }
            });
            buttonSkip.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonLogin.setVisibility(View.INVISIBLE);
                    buttonSkip.setVisibility(View.INVISIBLE);
                    progressWheel.setVisibility(View.VISIBLE);
                    progressWheel.spin();
                    LoginStatusEvent loginStatusEvent = new LoginStatusEvent();
                    loginStatusEvent.setSuccess(true);
                    loginStatusEvent.setLoggedIn(false);
                    eventBus.post(loginStatusEvent);
                }
            });
        }
        else {
            buttonLogin = (LoginButton) view.findViewById(R.id.buttonLogin);
            buttonRetry = (Button) view.findViewById(R.id.buttonRetry);
            progressWheel = (ProgressWheel) view.findViewById(R.id.loginProgressWheel);
            progressWheel.setVisibility(View.INVISIBLE);
        }

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookLoginUtil.onActivityResult(requestCode, resultCode, data);
    }

    public void onEventMainThread(FacebookSessionEvent event) {
        if(event.getLogin()) {
            if(!dialogMode) {
                buttonSkip.setVisibility(View.INVISIBLE);
            }
            buttonLogin.setVisibility(View.INVISIBLE);
            progressWheel.setVisibility(View.VISIBLE);
            progressWheel.spin();
            middlewareService.loadUserData(getActivity(), event.getSession());
            session = event.getSession();
        }
        else {
            userSession.logoutUser(getActivity());
        }
    }

    public void onEventMainThread(GetUserEvent event) {
        progressWheel.setVisibility(View.INVISIBLE);
        if(event.getSuccess()) {
            Log.d("LoginFragment", "GetUserEvent:Success");
            userSession.setUser(event.getUserDto());
            userSession.setToken(event.getToken());
            LoginStatusEvent loginStatusEvent = new LoginStatusEvent();
            loginStatusEvent.setSuccess(true);
            loginStatusEvent.setLoggedIn(true);
            eventBus.post(loginStatusEvent);
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch user details from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
            //Show retry button which will re-trigger the request.
        }
    }

    public void setMode(Boolean mode) {
        dialogMode = mode;
    }
}
