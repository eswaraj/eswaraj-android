package com.eswaraj.app.eswaraj.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.adapters.TextPagerAdapter;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.CacheClearedEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesImagesEvent;
import com.eswaraj.app.eswaraj.events.UserReadyEvent;
import com.eswaraj.app.eswaraj.fragments.LoginFragment;
import com.eswaraj.app.eswaraj.helpers.GoogleAnalyticsTracker;
import com.eswaraj.app.eswaraj.helpers.StorageCacheClearingTask;
import com.eswaraj.app.eswaraj.middleware.MiddlewareService;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.SplashScreenItem;
import com.eswaraj.app.eswaraj.util.GcmUtil;
import com.eswaraj.app.eswaraj.util.GlobalSessionUtil;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.eswaraj.app.eswaraj.widgets.CustomViewPager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class SplashActivity extends BaseActivity {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    GcmUtil gcmUtil;
    @Inject
    GoogleAnalyticsTracker googleAnalyticsTracker;
    @Inject
    GlobalSessionUtil globalSession;

    private StorageCacheClearingTask storageCacheClearingTask;
    private Boolean serverDataDownloadDone;
    private Boolean cacheCleared;
    private Boolean dontShowContinueButton;
    private Boolean startedFromMenu;

    private Long startTime;
    private Long endTime;

    private CustomViewPager pager;
    private TextPagerAdapter adapter;
    private ArrayList<SplashScreenItem> splashScreenItems;
    private View.OnClickListener onClickListenerContinue;
    private View.OnClickListener onClickListenerRetry;

    static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        eventBus.register(this);

        startedFromMenu = getIntent().getBooleanExtra("MODE", false);

        startTime = new Date().getTime();

        pager = (CustomViewPager) findViewById(R.id.viewPager);

        cacheCleared = false;
        serverDataDownloadDone = false;
        dontShowContinueButton = false;

        storageCacheClearingTask = new StorageCacheClearingTask(this);

        if(!startedFromMenu) {
            storageCacheClearingTask.execute();
            gcmUtil.registerWithGcmServerIfNeeded(this);
            dontShowContinueButton = middlewareService.wasImageDownloadLaunchedBefore(this);
            middlewareService.loadCategoriesData(this);
        }

        setUpListener();
        setUpPagerData(dontShowContinueButton);
        setUpPager();

        if(startedFromMenu) {
            readyToProceed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onDestroy() {
        endTime = new Date().getTime();
        googleAnalyticsTracker.trackTimeSpent("SplashActivity", "SplashScreens", endTime - startTime);
        eventBus.unregister(this);
        super.onDestroy();
    }

    private void setUpPager() {
        pager.setScrollDurationFactor(2);
        adapter = new TextPagerAdapter(getSupportFragmentManager(), splashScreenItems);
        adapter.setOnClickListenerContinue(onClickListenerContinue);
        adapter.setOnClickListenerRetry(onClickListenerRetry);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);
        pager.setCurrentItem(0);
    }

    private void setUpPagerData(Boolean onlyOne) {
        splashScreenItems = new ArrayList<SplashScreenItem>();
        if(onlyOne) {
            splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswarajlogo), getResources().getDrawable(R.drawable.bgpic), "Welcome to eSwaraj", "A Mobile & Web Platform for better Governance"));
        }
        else {
            splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswarajlogo), getResources().getDrawable(R.drawable.bgpic), "Welcome to eSwaraj", "A Mobile & Web Platform for better Governance"));
            splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswarajposter), getResources().getDrawable(R.drawable.bgpic), "About eSwaraj", "eSwaraj is for everyone. It enables citizens to report issues in their neighbourhood and provides trends and statistics to the government to govern better."));
            splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.mobile), getResources().getDrawable(R.drawable.bgpic), "Why eSwaraj ?", "Let's try to understand the need for eSwaraj."));
            splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswaraj01), getResources().getDrawable(R.drawable.bgpic), "Signing the constitution", "We came together and signed a contract named Constitution which paved the way for founding of this great nation. A contract that promised that our lives would be much better by being part of this nation. A contract that promised that together we can be much more than we are as individuals."));
            splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswaraj02), getResources().getDrawable(R.drawable.bgpic), "Administrative Structure", "A system of administration was laid out to achieve the promises in the Constitution. Considering everyone's similar basic needs, India was divided in multiple parts, each with same administrative and political structure."));
            splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswaraj03), getResources().getDrawable(R.drawable.bgpic), "The need for social audit", "But then, Even the greatest plans fall short if ground-level feedback is not taken from end beneficiaries. eSwaraj aims to remove the disconnect between ground level realities and top level perception and bring transparency in quality of service delivery and governance."));
            splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswaraj04), getResources().getDrawable(R.drawable.bgpic), "Analytics and More", "Visual analytics offering deeper insights to improve governance Per constituency/colony based map view to locate problems"));
            splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswaraj04), getResources().getDrawable(R.drawable.bgpic), "Offline Mode", "You can use eSwaraj when you are not connected to internet. All the complaints will be saved on your device and will be sent to server automatically the next time when you are connected to internet"));
        }
    }

    private void setUpListener() {
        onClickListenerContinue = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserReadyEvent event = new UserReadyEvent();
                event.setSuccess(true);
                eventBus.post(event);
            }
        };
        onClickListenerRetry = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                middlewareService.loadCategoriesData(v.getContext());
                adapter.showSpinner();
            }
        };
    }

    public void readyToProceed() {
        if(dontShowContinueButton) {
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra("MODE", false);
            startActivity(i);
            finish();
        }
        else {
            adapter.showContinueButton();
        }
    }

    private boolean checkPlayServices() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                GooglePlayServicesUtil.getErrorDialog(status, this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
            } else {
                Toast.makeText(this, "This device is not supported.",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }


    public void onEventMainThread(UserReadyEvent event) {
        if(startedFromMenu) {
            finish();
        }
        else {
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra("MODE", false);
            startActivity(i);
            finish();
        }
    }

    public void onEventMainThread(GetCategoriesDataEvent event) {
        if(event.getSuccess()) {
            //Launch image download now.
            globalSession.setCategoryDtoList(event.getCategoryList());
            middlewareService.loadCategoriesImages(this, event.getCategoryList(), false);
        }
        else {
            Toast.makeText(this, "Could not fetch categories from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
            adapter.showRetryButton();
        }
    }

    public void onEventMainThread(GetCategoriesImagesEvent event) {
        if(event.getSuccess()) {
            Log.d("SplashActivity", "GetCategoriesImagesEvent:Success");
        }
        else {
            Toast.makeText(this, "Could not fetch all categories images from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
            //Show retry button which will re-trigger the request.
        }
        serverDataDownloadDone = true;
        if (cacheCleared) {
            readyToProceed();
        }
    }

    public void onEventMainThread(CacheClearedEvent event) {
        cacheCleared = true;
        if(serverDataDownloadDone) {
            readyToProceed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_RECOVER_PLAY_SERVICES:
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Google Play Services must be installed.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
