package com.eswaraj.app.eswaraj.modules;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.eswaraj.app.eswaraj.activities.AddDetailsActivity;
import com.eswaraj.app.eswaraj.activities.ComplaintSummaryActivity;
import com.eswaraj.app.eswaraj.activities.HomeActivity;
import com.eswaraj.app.eswaraj.activities.LoginActivity;
import com.eswaraj.app.eswaraj.activities.LoginDialogActivity;
import com.eswaraj.app.eswaraj.activities.MarkLocationActivity;
import com.eswaraj.app.eswaraj.activities.MyComplaintsActivity;
import com.eswaraj.app.eswaraj.activities.SelectAmenityActivity;
import com.eswaraj.app.eswaraj.activities.SelectTemplateActivity;
import com.eswaraj.app.eswaraj.activities.SingleComplaintActivity;
import com.eswaraj.app.eswaraj.activities.SplashActivity;
import com.eswaraj.app.eswaraj.activities.YoutubeActivity;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.datastore.CacheInterface;
import com.eswaraj.app.eswaraj.datastore.Server;
import com.eswaraj.app.eswaraj.fragments.AddDetailsFragment;
import com.eswaraj.app.eswaraj.fragments.AmenitiesFragment;
import com.eswaraj.app.eswaraj.fragments.AmenityBannerFragment;
import com.eswaraj.app.eswaraj.fragments.CommentsFragment;
import com.eswaraj.app.eswaraj.fragments.ComplaintSummaryFragment;
import com.eswaraj.app.eswaraj.fragments.GoogleMapFragment;
import com.eswaraj.app.eswaraj.fragments.GooglePlacesListFragment;
import com.eswaraj.app.eswaraj.fragments.ImageFragment;
import com.eswaraj.app.eswaraj.fragments.MyComplaintsFragment;
import com.eswaraj.app.eswaraj.fragments.SingleComplaintFragment;
import com.eswaraj.app.eswaraj.fragments.LoginFragment;
import com.eswaraj.app.eswaraj.fragments.SplashFragment;
import com.eswaraj.app.eswaraj.fragments.TemplatesFragment;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.helpers.ReverseGeocodingTask;
import com.eswaraj.app.eswaraj.helpers.SharedPreferencesHelper;
import com.eswaraj.app.eswaraj.util.DeviceUtil;
import com.eswaraj.app.eswaraj.util.GooglePlacesUtil;
import com.eswaraj.app.eswaraj.util.LocationUtil;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.util.FacebookLoginUtil;
import com.eswaraj.app.eswaraj.util.InternetServicesCheckUtil;
import com.eswaraj.app.eswaraj.util.LocationServicesCheckUtil;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.eswaraj.app.eswaraj.volley.CommentPostRequest;
import com.eswaraj.app.eswaraj.volley.CommentsRequest;
import com.eswaraj.app.eswaraj.volley.ComplaintImageRequest;
import com.eswaraj.app.eswaraj.volley.ComplaintPostRequest;
import com.eswaraj.app.eswaraj.volley.LoadCategoriesDataRequest;
import com.eswaraj.app.eswaraj.volley.LoadCategoriesImagesRequest;
import com.eswaraj.app.eswaraj.volley.RegisterFacebookUserRequest;
import com.eswaraj.app.eswaraj.volley.RegisterUserAndDeviceRequest;
import com.eswaraj.app.eswaraj.volley.UserComplaintsRequest;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

@Module(
        injects = {
                LoginActivity.class,
                LoginFragment.class,
                SelectAmenityActivity.class,
                AmenitiesFragment.class,
                Cache.class,
                Server.class,
                NetworkAccessHelper.class,
                LocationUtil.class,
                DeviceUtil.class,
                GooglePlacesUtil.class,
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
                ComplaintPostRequest.class,
                AddDetailsActivity.class,
                AddDetailsFragment.class,
                RegisterUserAndDeviceRequest.class,
                MarkLocationActivity.class,
                ComplaintSummaryFragment.class,
                ComplaintSummaryActivity.class,
                MyComplaintsActivity.class,
                MyComplaintsFragment.class,
                UserComplaintsRequest.class,
                SingleComplaintActivity.class,
                SingleComplaintFragment.class,
                ImageFragment.class,
                ComplaintImageRequest.class,
                CommentsFragment.class,
                CommentsRequest.class,
                CommentPostRequest.class,
                GooglePlacesListFragment.class,
                HomeActivity.class,
                UserSessionUtil.class,
                SplashFragment.class,
                SplashActivity.class,
                LoginDialogActivity.class,
                GoogleMapFragment.class
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


    @Provides
    FacebookLoginUtil provideFacebookLoginUtil() {
        return new FacebookLoginUtil();
    }

    @Provides @Singleton
    GooglePlacesUtil provideGooglePlacesUtil() {
        return new GooglePlacesUtil();
    }

    @Provides @Singleton
    UserSessionUtil provideUserSessionUtil() {
        return new UserSessionUtil();
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
    LocationUtil provideLocationUtil() {
        return new LocationUtil();
    }


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

    @Provides @Singleton
    RegisterUserAndDeviceRequest provideRegisterUserAndDeviceRequest() {
        return new RegisterUserAndDeviceRequest();
    }

    @Provides @Singleton
    ComplaintPostRequest provideComplaintPostRequest() {
        return new ComplaintPostRequest();
    }

    @Provides @Singleton
    UserComplaintsRequest provideUserComplaintsRequest() {
        return new UserComplaintsRequest();
    }

    @Provides @Singleton
    ComplaintImageRequest provideComplaintImageRequest() {
        return new ComplaintImageRequest();
    }

    @Provides @Singleton
    CommentsRequest provideCommentsRequest() {
        return new CommentsRequest();
    }

    @Provides @Singleton
    CommentPostRequest provideCommentPostRequest() {
        return new CommentPostRequest();
    }
}
