package com.eswaraj.app.eswaraj.modules;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.datastore.CacheInterface;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.helpers.SharedPreferencesHelper;
import com.eswaraj.app.eswaraj.location.LocationUtil;
import com.eswaraj.app.eswaraj.util.FacebookLoginUtil;
import com.eswaraj.app.eswaraj.util.InternetServicesCheckUtil;
import com.eswaraj.app.eswaraj.util.LocationServicesCheckUtil;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

@Module
public class MiddlewareGraph {
    private Context applicationContext;

    public MiddlewareGraph(Context context) {
        this.applicationContext = context;
    }

    @Provides
    public Context provideApplicationContext() {
        return applicationContext;
    }


    @Provides
    FacebookLoginUtil provideFacebookLoginUtil() {
        return new FacebookLoginUtil();
    }

    @Provides @Singleton
    SharedPreferencesHelper provideSharedPreferencesHelper() {
        return new SharedPreferencesHelper();
    }

    @Provides @Singleton
    CacheInterface provideLocalCacheService() {
        return new Cache();
    }


    @Provides @Singleton
    LocationUtil provideLocationUtil(Context context) {
        return new LocationUtil(context);
    }


    @Provides @Singleton @Named("default")
    EventBus provideEventBus() {
        return EventBus.getDefault();
    }

    @Provides @Singleton
    public RequestQueue provideRequestQueue(Context context) {
        /** Set up to use OkHttp */
        return Volley.newRequestQueue(context);
    }

    @Provides
    InternetServicesCheckUtil provideInternetServicesCheckUtil() {
        return new InternetServicesCheckUtil();
    }

    @Provides
    LocationServicesCheckUtil provideLocationServicesCheckUtil() {
        return new LocationServicesCheckUtil();
    }

    @Provides @Singleton
    NetworkAccessHelper provideNetworkAccessHelper() {
        return new NetworkAccessHelper();
    }
}
