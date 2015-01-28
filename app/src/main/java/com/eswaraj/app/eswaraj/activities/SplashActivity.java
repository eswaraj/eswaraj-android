package com.eswaraj.app.eswaraj.activities;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.adapters.SplashPagerAdapter;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.CacheClearedEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesImagesEvent;
import com.eswaraj.app.eswaraj.helpers.GoogleAnalyticsTracker;
import com.eswaraj.app.eswaraj.helpers.StorageCacheClearingTask;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.SplashScreenItem;
import com.eswaraj.app.eswaraj.util.GcmUtil;
import com.eswaraj.app.eswaraj.util.GlobalSessionUtil;
import com.eswaraj.app.eswaraj.widgets.CustomViewPager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.pnikosis.materialishprogress.ProgressWheel;

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
    private Boolean criticalDataLoadFailed = false;
    private Boolean nextActivityLaunched = false;
    private int currentPage = 0;

    private Long startTime;
    private Long endTime;

    private CustomViewPager pager;
    private SplashPagerAdapter adapter;
    private RadioGroup radioGroup;
    private Button cont;
    private Button retry;
    private ProgressWheel progressWheel;

    private ArrayList<SplashScreenItem> splashScreenItems;
    private View.OnClickListener onClickListenerContinue;
    private View.OnClickListener onClickListenerRetry;
    private ViewPager.OnPageChangeListener onPageChangeListener;

    static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        eventBus.register(this);

        startedFromMenu = getIntent().getBooleanExtra("MODE", false);

        startTime = new Date().getTime();

        pager = (CustomViewPager) findViewById(R.id.viewPager);
        radioGroup = (RadioGroup) findViewById(R.id.sRadioGroup);
        cont = (Button) findViewById(R.id.sContinue);
        retry = (Button) findViewById(R.id.sRetry);
        progressWheel = (ProgressWheel) findViewById(R.id.sProgressWheel);

        cacheCleared = false;
        serverDataDownloadDone = false;
        dontShowContinueButton = false;

        storageCacheClearingTask = new StorageCacheClearingTask(this);

        setUpListener();
        if(!startedFromMenu) {
            setUpPagerData(middlewareService.wasImageDownloadLaunchedBefore(this));
        }
        else {
            setUpPagerData(false);
        }
        setUpPager();

        cont.setOnClickListener(onClickListenerContinue);
        retry.setOnClickListener(onClickListenerRetry);

        if(!startedFromMenu) {
            storageCacheClearingTask.execute();
            gcmUtil.registerWithGcmServerIfNeeded(this);
            dontShowContinueButton = middlewareService.wasImageDownloadLaunchedBefore(this);
            middlewareService.loadCategoriesData(this);
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
        adapter = new SplashPagerAdapter(getSupportFragmentManager(), splashScreenItems);
        pager.setAdapter(adapter);
        pager.setScrollDurationFactor(2);
        pager.setOffscreenPageLimit(2);
        pager.setCurrentItem(0);
        pager.setOnPageChangeListener(onPageChangeListener);
    }

    private void setUpPagerData(Boolean onlyOne) {
        splashScreenItems = new ArrayList<SplashScreenItem>();
        if(onlyOne) {
            splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswarajlogo), getResources().getDrawable(R.drawable.bgpic), "Welcome to eSwaraj", "A Mobile & Web Platform for better Governance"));
        }
        else {
            splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswarajlogo), getResources().getDrawable(R.drawable.splash_home_bg), "Welcome to eSwaraj", "A Mobile & Web Platform for better Governance"));
            splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswarajposter), getResources().getDrawable(R.drawable.splash_home_bg), "About eSwaraj", "eSwaraj is for everyone. It enables citizens to report issues in their neighbourhood and provides trends and statistics to the government to govern better."));
            splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.mobile), getResources().getDrawable(R.drawable.splash_home_bg), "Why eSwaraj ?", "Let's try to understand the need for eSwaraj."));
            splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswaraj01), getResources().getDrawable(R.drawable.splash_home_bg), "Signing the constitution", "We came together and signed a contract named Constitution which paved the way for founding of this great nation. A contract that promised that our lives would be much better by being part of this nation. A contract that promised that together we can be much more than we are as individuals."));
            splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswaraj02), getResources().getDrawable(R.drawable.splash_home_bg), "Administrative Structure", "A system of administration was laid out to achieve the promises in the Constitution. Considering everyone's similar basic needs, India was divided in multiple parts, each with same administrative and political structure."));
            splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswaraj03), getResources().getDrawable(R.drawable.splash_home_bg), "The need for social audit", "But then, Even the greatest plans fall short if ground-level feedback is not taken from end beneficiaries. eSwaraj aims to remove the disconnect between ground level realities and top level perception and bring transparency in quality of service delivery and governance."));
            splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswaraj04), getResources().getDrawable(R.drawable.splash_home_bg), "Analytics and More", "Visual analytics offering deeper insights to improve governance Per constituency/colony based map view to locate problems"));
            splashScreenItems.add(new SplashScreenItem(getResources().getDrawable(R.drawable.eswaraj04), getResources().getDrawable(R.drawable.splash_home_bg), "Offline Mode", "You can use eSwaraj when you are not connected to internet. All the complaints will be saved on your device and will be sent to server automatically the next time when you are connected to internet"));
        }

        if(splashScreenItems.size() < 2) {
            radioGroup.setVisibility(View.GONE);
        }
        for (int i = 0; i < splashScreenItems.size(); i++) {
            RadioButton radioButton = (RadioButton) getLayoutInflater().inflate(R.layout.splash_radio_button, null);
            radioButton.setId(i);
            radioButton.setEnabled(false);
            radioGroup.addView(radioButton);
        }
        radioGroup.check(0);
        radioGroup.setEnabled(false);
    }

    private void setUpListener() {
        onClickListenerContinue = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startedFromMenu) {
                    finish();
                }
                else {
                    readyToProceed();
                }
            }
        };
        onClickListenerRetry = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                middlewareService.loadCategoriesData(v.getContext());
                criticalDataLoadFailed = false;
                serverDataDownloadDone = false;
                updateUi();
            }
        };
        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPage = position;
                radioGroup.check(position);
                updateUi();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }

    private void updateUi() {
        if(currentPage == splashScreenItems.size() - 1) {
            radioGroup.setVisibility(View.INVISIBLE);
            if(startedFromMenu) {
                cont.setVisibility(View.VISIBLE);
            }
            if(criticalDataLoadFailed) {
                retry.setVisibility(View.VISIBLE);
            }
            if((!cacheCleared || !serverDataDownloadDone) && !startedFromMenu) {
                progressWheel.setVisibility(View.VISIBLE);
            }
            if((cacheCleared && serverDataDownloadDone) && !startedFromMenu && !dontShowContinueButton) {
                cont.setVisibility(View.VISIBLE);
            }
            if((cacheCleared && serverDataDownloadDone) && !startedFromMenu && dontShowContinueButton) {
                readyToProceed();
            }
        }
        else {
            radioGroup.setVisibility(View.VISIBLE);
            cont.setVisibility(View.INVISIBLE);
            retry.setVisibility(View.INVISIBLE);
            progressWheel.setVisibility(View.INVISIBLE);
        }
    }

    public synchronized void readyToProceed() {
        if(!nextActivityLaunched) {
            nextActivityLaunched = true;
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra("MODE", false);
            startActivity(i);
            finish();
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

    public void onEventMainThread(GetCategoriesDataEvent event) {
        if(event.getSuccess()) {
            //Launch image download now.
            globalSession.setCategoryDtoList(event.getCategoryList());
            middlewareService.loadCategoriesImages(this, event.getCategoryList(), false);
        }
        else {
            Toast.makeText(this, "Could not fetch categories from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
            criticalDataLoadFailed = true;
            updateUi();
        }
    }

    public void onEventMainThread(GetCategoriesImagesEvent event) {
        if(event.getSuccess()) {
            Log.d("SplashActivity", "GetCategoriesImagesEvent:Success");
        }
        else {
            Toast.makeText(this, "Could not fetch all categories images from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
        serverDataDownloadDone = true;
        updateUi();
    }

    public void onEventMainThread(CacheClearedEvent event) {
        cacheCleared = true;
        updateUi();
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
