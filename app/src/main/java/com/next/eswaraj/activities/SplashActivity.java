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
        Log.e("SplashActivity", "startedFromMenu=" + startedFromMenu);

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
            Log.e("SplashActivity", "Load categories data initiated");
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
            splashScreenItems.add(new SplashScreenItem(null, null, getResources().getString(R.string.splashProjectName), getResources().getString(R.string.splashTagLine)));
        }
        else {
            splashScreenItems.add(new SplashScreenItem(null, null, getResources().getString(R.string.splashProjectName), getResources().getString(R.string.splashTagLine)));
            splashScreenItems.add(new SplashScreenItem(null, null, getResources().getString(R.string.splashHead1), getResources().getString(R.string.splashContent1)));
            splashScreenItems.add(new SplashScreenItem(null, null, getResources().getString(R.string.splashHead2), getResources().getString(R.string.splashContent2)));
            splashScreenItems.add(new SplashScreenItem(null, null, getResources().getString(R.string.splashHead3), getResources().getString(R.string.splashContent3)));
            splashScreenItems.add(new SplashScreenItem(null, null, getResources().getString(R.string.splashHead4), getResources().getString(R.string.splashContent4)));
            splashScreenItems.add(new SplashScreenItem(null, null, getResources().getString(R.string.splashHead5), getResources().getString(R.string.splashContent5)));
            splashScreenItems.add(new SplashScreenItem(null, null, getResources().getString(R.string.splashHead6), getResources().getString(R.string.splashContent6)));
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
                Toast.makeText(this, getResources().getString(R.string.deviceNotSupportedLabel), Toast.LENGTH_LONG).show();
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
            Log.e("SplashActivity", "Set category list in GlobalSession. List = " + globalSession.getCategoryDtoList(this).toString());
            middlewareService.loadCategoriesImages(this, event.getCategoryList(), false);
        }
        else {
            if(internetServicesCheckUtil.isServiceAvailable(this)) {
                Toast.makeText(this, event.getError(), Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, getResources().getString(R.string.noInternetConnectionFound), Toast.LENGTH_LONG).show();
            }
            criticalDataLoadFailed = true;
            updateUi();
        }
        Log.e("SplashActivity", "GetCategoriesDataEvent done");
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
                    Toast.makeText(this, getResources().getString(R.string.googlePlayMustBeInstalled), Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
