package com.eswaraj.app.eswaraj.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.FacebookSessionEvent;
import com.eswaraj.app.eswaraj.events.GetUserEvent;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.util.FacebookLoginUtil;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.facebook.widget.LoginButton;
import com.pnikosis.materialishprogress.ProgressWheel;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class LoginDialogActivity extends BaseActivity {

    @Inject
    FacebookLoginUtil facebookLoginUtil;
    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    UserSessionUtil userSession;

    private LoginButton loginButton;
    private ProgressWheel progressWheel;
    private TextView ldMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_dialog);
        loginButton = (LoginButton) findViewById(R.id.ldButtonLogin);
        progressWheel = (ProgressWheel) findViewById(R.id.ldProgressWheel);
        ldMessage = (TextView) findViewById(R.id.ldMessage);
        eventBus.register(this);
        facebookLoginUtil.onCreate(this, savedInstanceState);
        facebookLoginUtil.setup(loginButton);
        progressWheel.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onDestroy() {
        facebookLoginUtil.onDestroy();
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        facebookLoginUtil.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        facebookLoginUtil.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        facebookLoginUtil.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookLoginUtil.onActivityResult(requestCode, resultCode, data);
    }

    public void onEventMainThread(FacebookSessionEvent event) {
        if(event.getLogin()) {
            loginButton.setVisibility(View.INVISIBLE);
            ldMessage.setText("Setting up app for YOU");
            progressWheel.setVisibility(View.VISIBLE);
            progressWheel.spin();
            middlewareService.loadUserData(this, event.getSession());
        }
        else {
            userSession.logoutUser(this);
        }
    }

    public void onEventMainThread(GetUserEvent event) {
        if(event.getSuccess()) {
            Log.d("LoginFragment", "GetUserEvent:Success");
            userSession.setUser(event.getUserDto());
        }
        else {
            Toast.makeText(this, "Could not fetch user details from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
            //Show retry button which will re-trigger the request.
        }
        progressWheel.stopSpinning();
        progressWheel.setVisibility(View.INVISIBLE);
        finish();
    }
}
