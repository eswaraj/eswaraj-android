package com.next.eswaraj.base;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.next.eswaraj.BuildConfig;
import com.next.eswaraj.R;
import com.next.eswaraj.activities.AboutUsActivity;
import com.next.eswaraj.activities.ComplaintFilterActivity;
import com.next.eswaraj.activities.ContentActivity;
import com.next.eswaraj.activities.MyProfileActivity;
import com.next.eswaraj.activities.SplashActivity;
import com.next.eswaraj.application.EswarajApplication;
import com.next.eswaraj.helpers.WindowAnimationHelper;
import com.next.eswaraj.models.ComplaintFilter;
import com.next.eswaraj.util.UserSessionUtil;

import java.lang.reflect.Method;

import javax.inject.Inject;

public class BaseActivity extends ActionBarActivity {

    @Inject
    UserSessionUtil userSession;

    private final int SHOW_FILTER_REQUEST = 9999;
    protected Boolean dontUseAnimation = true;
    protected Boolean showFilter = false;
    protected ComplaintFilter complaintFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((EswarajApplication)getApplication()).inject(this);
        setTitle("eSwaraj");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(BuildConfig.DEBUG) {
            setTitle("Test: " + getTitle());
        }
    }

    @Override
    protected void onStop() {
        if(BuildConfig.DEBUG) {
            setTitle(getTitle().toString().replace("Test: ", ""));
        }
        super.onStop();
    }

    @Override
    public void finish() {
        super.finish();
        if(!dontUseAnimation) {
            WindowAnimationHelper.finish(this);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if(dontUseAnimation) {
            super.startActivityForResult(intent, requestCode);
        }
        else {
            WindowAnimationHelper.startActivityForResultWithSlideFromRight(this, intent, requestCode);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if(dontUseAnimation) {
            super.startActivity(intent);
        }
        else {
            WindowAnimationHelper.startActivityWithSlideFromRight(this, intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(s.length() < 3) {
                    Toast.makeText(getBaseContext(), "Please enter minimum 3 characters", Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.menu_user);
        if(userSession.getUser() != null) {
            item.setTitle(userSession.getUser().getPerson().getName());
            if(userSession.getUserProfilePhoto() != null) {
                item.setIcon(new BitmapDrawable(getResources(), userSession.getUserProfilePhoto()));
            }
        }
        else {
            item.setVisible(false);
        }
        if(!showFilter) {
            item = menu.findItem(R.id.menu_filter);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.menu_user:
                i = new Intent(getBaseContext(), MyProfileActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_splash:
                i = new Intent(getBaseContext(), SplashActivity.class);
                i.putExtra("MODE", true);
                startActivity(i);
                return true;
            case R.id.menu_content:
                i = new Intent(getBaseContext(), ContentActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_filter:
                i = new Intent(getBaseContext(), ComplaintFilterActivity.class);
                i.putExtra("FILTER", complaintFilter);
                startActivityForResult(i, SHOW_FILTER_REQUEST);
                return true;
            case R.id.menu_aboutus:
                i = new Intent(getBaseContext(), AboutUsActivity.class);
                startActivity(i);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if(featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if(menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch(NoSuchMethodException e) { //...
                } catch(Exception e) { // ...
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

}
