package com.eswaraj.app.eswaraj.modules;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.eswaraj.app.eswaraj.activities.AddDetailsActivity;
import com.eswaraj.app.eswaraj.activities.SelectAmenityActivity;
import com.eswaraj.app.eswaraj.activities.SelectTemplateActivity;
import com.eswaraj.app.eswaraj.activities.SplashActivity;
import com.eswaraj.app.eswaraj.activities.YoutubeActivity;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.datastore.CacheInterface;
import com.eswaraj.app.eswaraj.datastore.Server;
import com.eswaraj.app.eswaraj.fragments.AddDetailsFragment;
import com.eswaraj.app.eswaraj.fragments.AmenitiesFragment;
import com.eswaraj.app.eswaraj.fragments.AmenityBannerFragment;
import com.eswaraj.app.eswaraj.fragments.GoogleMapFragment;
import com.eswaraj.app.eswaraj.fragments.SplashFragment;
import com.eswaraj.app.eswaraj.fragments.TemplatesFragment;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.helpers.ReverseGeocodingTask;
import com.eswaraj.app.eswaraj.helpers.SharedPreferencesHelper;
import com.eswaraj.app.eswaraj.util.DeviceUtil;
import com.eswaraj.app.eswaraj.util.LocationUtil;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.util.FacebookLoginUtil;
import com.eswaraj.app.eswaraj.util.InternetServicesCheckUtil;
import com.eswaraj.app.eswaraj.util.LocationServicesCheckUtil;
import com.eswaraj.app.eswaraj.volley.LoadCategoriesDataRequest;
import com.eswaraj.app.eswaraj.volley.LoadCategoriesImagesRequest;
import com.eswaraj.app.eswaraj.volley.RegisterFacebookUserRequest;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

@Module(
        injects = {
                SplashActivity.class,
                SplashFragment.class,
                SelectAmenityActivity.class,
                AmenitiesFragment.class,
                Cache.class,
                Server.class,
                NetworkAccessHelper.class,
                LocationUtil.class,
                DeviceUtil.class,
                FacebookLoginUtil.class,
                MiddlewareServiceImpl.class,
                ReverseGeocodingTask.class,
                SelectTemplateActivity.class,
                TemplatesFragment.class,
                AmenityBannerFragment.class,
                YoutubeActivity.class,
                GoogleMapFragment.class,
                LoadCategoriesDataRequest.class,
                LoadCategoriesImagesRequest.class,
                RegisterFacebookUserRequest.class,
                AddDetailsActivity.class,
                AddDetailsFragment.class
        },
        complete = false,
        library = true
)
public class MiddlewareGraph {
    private Context applicationContext;

    public MiddlewareGraph(Context context) {
        this.applicationContext = context;
    }

    @Provides
    public Context provideApplicationContext() {
        return applicationContext;
    }


    @Provides @Singleton
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


    //@Provides @Singleton
    //LocationUtil provideLocationUtil(Context context) {
        //return new LocationUtil(context);
    //}


    @Provides @Singleton
    EventBus provideEventBus() {
        return EventBus.getDefault();
    }

    @Provides @Singleton
    public RequestQueue provideRequestQueue() {
        /** Set up to use OkHttp */
        return Volley.newRequestQueue(this.applicationContext);
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

    @Provides @Singleton
    MiddlewareServiceImpl provideMiddlewareService() {
        return new MiddlewareServiceImpl();
    }

    @Provides @Singleton
    LoadCategoriesDataRequest provideLoadCategoriesDataRequest() {
        return new LoadCategoriesDataRequest();
    }

    @Provides
    LoadCategoriesImagesRequest provideLoadCategoriesImagesRequest() {
        return new LoadCategoriesImagesRequest();
    }
}
