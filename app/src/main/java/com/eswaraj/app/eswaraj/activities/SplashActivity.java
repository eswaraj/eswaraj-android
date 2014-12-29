package com.eswaraj.app.eswaraj.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.CacheClearedEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesImagesEvent;
import com.eswaraj.app.eswaraj.events.UserReadyEvent;
import com.eswaraj.app.eswaraj.fragments.LoginFragment;
import com.eswaraj.app.eswaraj.fragments.SplashFragment;
import com.eswaraj.app.eswaraj.helpers.StorageCacheClearingTask;
import com.eswaraj.app.eswaraj.middleware.MiddlewareService;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class SplashActivity extends BaseActivity {

    @Inject
    UserSessionUtil userSession;
    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;

    private SplashFragment splashFragment;
    private StorageCacheClearingTask storageCacheClearingTask;
    private Boolean serverDataDownloadDone;
    private Boolean cacheCleared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashFragment = (SplashFragment) getSupportFragmentManager().findFragmentById(R.id.splashFragment);

        cacheCleared = false;
        serverDataDownloadDone = false;

        storageCacheClearingTask = new StorageCacheClearingTask(this);

        eventBus.register(this);
        middlewareService.loadCategoriesData(this);
        storageCacheClearingTask.execute();

        //if(userSession.isUserLoggedIn(this)) {
        if(middlewareService.isCategoriesDataAvailable(this) && middlewareService.isCategoriesImagesAvailable(this)) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(UserReadyEvent event) {
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra("MODE", false);
        startActivity(i);
        finish();
    }

    public void onEventMainThread(GetCategoriesDataEvent event) {
        if(event.getSuccess()) {
            //Launch image download now.
            middlewareService.loadCategoriesImages(this, event.getCategoryList(), false);
        }
        else {
            Toast.makeText(this, "Could not fetch categories from server. Error = " + event.getError(), Toast.LENGTH_LONG).show();
            //Show retry button which will re-trigger the request.
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
            splashFragment.readyToProceed();
        }
    }

    public void onEventMainThread(CacheClearedEvent event) {
        Log.d("SplashActivity", "Cache:Success");
        cacheCleared = true;
        if(serverDataDownloadDone) {
            splashFragment.readyToProceed();
        }
    }
}
