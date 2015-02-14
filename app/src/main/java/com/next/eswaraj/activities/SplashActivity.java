package com.next.eswaraj.activities;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.next.eswaraj.R;
import com.next.eswaraj.adapters.SplashPagerAdapter;
import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.events.CacheClearedEvent;
import com.next.eswaraj.events.GetCategoriesDataEvent;
import com.next.eswaraj.events.GetCategoriesImagesEvent;
import com.next.eswaraj.helpers.GoogleAnalyticsTracker;
import com.next.eswaraj.helpers.StorageCacheClearingTask;
import com.next.eswaraj.middleware.MiddlewareServiceImpl;
import com.next.eswaraj.models.SplashScreenItem;
import com.next.eswaraj.util.GcmUtil;
import com.next.eswaraj.util.GlobalSessionUtil;
import com.next.eswaraj.util.InternetServicesCheckUtil;
import com.next.eswaraj.widgets.CustomViewPager;
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
    @Inject
    InternetServicesCheckUtil internetServicesCheckUtil;

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
            splashScreenItems.add(new SplashScreenItem(null, null, "eSwaraj", "Making Good Governance a People's Movement"));
        }
        else {
            splashScreenItems.add(new SplashScreenItem(null, null, "eSwaraj", "Making Good Governance a People's Movement"));
            splashScreenItems.add(new SplashScreenItem(null, null, "Constitution : A Nation's Blueprint", "We came together and signed a contract called constitution which paved the way for founding of this great nation. A Contract that promised that our lives would be much better by being part of this nation. A contract that promised that together we can be much more than we are as individuals."));
            splashScreenItems.add(new SplashScreenItem(null, null, "Political & Administrative Structure", "A system of public administration was laid out to achieve the promises in the Constitution. Considering everyone has similar basic needs, India was divided in multiple parts, each with same political and administrative structure. People elect representative who coordinate with administration to deliver governance."));
            splashScreenItems.add(new SplashScreenItem(null, null, "Promises Need Dose of Reality : Social Audit", "Even the greatest plans fall short if ground-level feedback is not taken from end beneficiaries. eSwaraj aims to remove the disconnect between ground level realities and top level perception and bring transparency in quality of service delivery and governance."));
            splashScreenItems.add(new SplashScreenItem(null, null, "Keep System Running Effectively : Run a Tight Ship", " In absence of continuous dialogue, no proper tool to monitor development, missing progress metrics, directionless talks and rising disconnect, people get disillusioned leading to widespread negativity and pessimism, and the system loses steam. eSwaraj aims to become the missing link"));
            splashScreenItems.add(new SplashScreenItem(null, null, "eSwaraj : Banking on the new age revolution #DigitalIndia", "eSwaraj is a continuous engagement platform for citizens and leaders. It aims to eliminate the divide between ground level realities and top level perception and render transparency in quality of service delivery and governance."));
            splashScreenItems.add(new SplashScreenItem(null, null, "Let's unite?", "\u2022 Report problems\n" +
                    "\u2022 Stay updated about progress from elected leaders\n" +
                    "\u2022 Always be aware of your leaders and their policies\n" +
                    "\u2022 Be acquainted with problems in your constituency"));
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
            if(internetServicesCheckUtil.isServiceAvailable(this)) {
                Toast.makeText(this, "Could not fetch data from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "No internet connection found", Toast.LENGTH_LONG).show();
            }
            criticalDataLoadFailed = true;
            updateUi();
        }
    }

    public void onEventMainThread(GetCategoriesImagesEvent event) {
        if(event.getSuccess()) {
            Log.d("SplashActivity", "GetCategoriesImagesEvent:Success");
        }
        else {
            //Toast.makeText(this, "Could not fetch all categories images from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
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
