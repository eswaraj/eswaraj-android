package com.eswaraj.app.eswaraj.modules;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.eswaraj.app.eswaraj.activities.AddDetailsActivity;
import com.eswaraj.app.eswaraj.activities.ComplaintFilterActivity;
import com.eswaraj.app.eswaraj.activities.ComplaintSummaryActivity;
import com.eswaraj.app.eswaraj.activities.ConstituencyComplaintsActivity;
import com.eswaraj.app.eswaraj.activities.ConstituencySnapshotActivity;
import com.eswaraj.app.eswaraj.activities.LeaderListActivity;
import com.eswaraj.app.eswaraj.activities.LocationListActivity;
import com.eswaraj.app.eswaraj.activities.MarkHomeActivity;
import com.eswaraj.app.eswaraj.activities.PlaceSearchActivity;
import com.eswaraj.app.eswaraj.activities.SearchableActivity;
import com.eswaraj.app.eswaraj.activities.UserComplaintsActivity;
import com.eswaraj.app.eswaraj.activities.ContentActivity;
import com.eswaraj.app.eswaraj.activities.HomeActivity;
import com.eswaraj.app.eswaraj.activities.LeaderActivity;
import com.eswaraj.app.eswaraj.activities.LoginActivity;
import com.eswaraj.app.eswaraj.activities.MarkLocationActivity;
import com.eswaraj.app.eswaraj.activities.MyProfileActivity;
import com.eswaraj.app.eswaraj.activities.SelectAmenityActivity;
import com.eswaraj.app.eswaraj.activities.SelectTemplateActivity;
import com.eswaraj.app.eswaraj.activities.SingleComplaintActivity;
import com.eswaraj.app.eswaraj.activities.SplashActivity;
import com.eswaraj.app.eswaraj.activities.UserSnapshotActivity;
import com.eswaraj.app.eswaraj.activities.YoutubeActivity;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.datastore.CacheInterface;
import com.eswaraj.app.eswaraj.datastore.Database;
import com.eswaraj.app.eswaraj.datastore.LruBitmapCache;
import com.eswaraj.app.eswaraj.datastore.Server;
import com.eswaraj.app.eswaraj.datastore.StorageCache;
import com.eswaraj.app.eswaraj.fragments.AddDetailsFragment;
import com.eswaraj.app.eswaraj.fragments.AnalyticsFragment;
import com.eswaraj.app.eswaraj.fragments.ComplaintListFragment;
import com.eswaraj.app.eswaraj.fragments.ComplaintSummaryOfflineFragment;
import com.eswaraj.app.eswaraj.fragments.ComplaintsFragment;
import com.eswaraj.app.eswaraj.fragments.ConstituencyComplaintsFragment;
import com.eswaraj.app.eswaraj.fragments.ConstituencySnapshotFragment;
import com.eswaraj.app.eswaraj.fragments.LeaderForComplaintFragment;
import com.eswaraj.app.eswaraj.fragments.LeaderListFragment;
import com.eswaraj.app.eswaraj.fragments.LocationListFragment;
import com.eswaraj.app.eswaraj.fragments.UserComplaintsFragment;
import com.eswaraj.app.eswaraj.fragments.ConstituencyFragment;
import com.eswaraj.app.eswaraj.fragments.ComplaintsMapFragment;
import com.eswaraj.app.eswaraj.fragments.MyConstituencyFragment;
import com.eswaraj.app.eswaraj.fragments.ConstituencyInfoFragment;
import com.eswaraj.app.eswaraj.fragments.ContentFragment;
import com.eswaraj.app.eswaraj.fragments.LeaderFragment;
import com.eswaraj.app.eswaraj.fragments.MyProfileFragment;
import com.eswaraj.app.eswaraj.fragments.SelectAmenityFragment;
import com.eswaraj.app.eswaraj.fragments.AmenityBannerFragment;
import com.eswaraj.app.eswaraj.fragments.CommentsFragment;
import com.eswaraj.app.eswaraj.fragments.ComplaintSummaryFragment;
import com.eswaraj.app.eswaraj.fragments.GoogleMapFragment;
import com.eswaraj.app.eswaraj.fragments.GooglePlacesListFragment;
import com.eswaraj.app.eswaraj.fragments.ImageFragment;
import com.eswaraj.app.eswaraj.fragments.MyComplaintsFragment;
import com.eswaraj.app.eswaraj.fragments.SelectTemplateFragment;
import com.eswaraj.app.eswaraj.fragments.SingleComplaintFragment;
import com.eswaraj.app.eswaraj.fragments.LoginFragment;
import com.eswaraj.app.eswaraj.fragments.UserSnapshotFragment;
import com.eswaraj.app.eswaraj.helpers.CameraHelper;
import com.eswaraj.app.eswaraj.helpers.DatabaseHelper;
import com.eswaraj.app.eswaraj.helpers.GoogleAnalyticsTracker;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.helpers.NotificationHelper;
import com.eswaraj.app.eswaraj.helpers.ReverseGeocodingTask;
import com.eswaraj.app.eswaraj.helpers.SharedPreferencesHelper;
import com.eswaraj.app.eswaraj.helpers.StorageCacheClearingTask;
import com.eswaraj.app.eswaraj.services.ComplaintPostService;
import com.eswaraj.app.eswaraj.services.GcmService;
import com.eswaraj.app.eswaraj.util.DeviceUtil;
import com.eswaraj.app.eswaraj.util.FacebookSharingUtil;
import com.eswaraj.app.eswaraj.util.GcmUtil;
import com.eswaraj.app.eswaraj.util.GlobalSessionUtil;
import com.eswaraj.app.eswaraj.util.GooglePlacesUtil;
import com.eswaraj.app.eswaraj.util.LocationUtil;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.util.FacebookLoginUtil;
import com.eswaraj.app.eswaraj.util.InternetServicesCheckUtil;
import com.eswaraj.app.eswaraj.util.LocationServicesCheckUtil;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.eswaraj.app.eswaraj.util.VolleyUtil;
import com.eswaraj.app.eswaraj.volley.CommentPostRequest;
import com.eswaraj.app.eswaraj.volley.CommentsRequest;
import com.eswaraj.app.eswaraj.volley.ComplaintCloseRequest;
import com.eswaraj.app.eswaraj.volley.GlobalSearchRequest;
import com.eswaraj.app.eswaraj.volley.LoadImageRequest;
import com.eswaraj.app.eswaraj.volley.ComplaintPostRequest;
import com.eswaraj.app.eswaraj.volley.LoadCategoriesDataRequest;
import com.eswaraj.app.eswaraj.volley.LoadCategoriesImagesRequest;
import com.eswaraj.app.eswaraj.volley.LoadLeaderByIdRequest;
import com.eswaraj.app.eswaraj.volley.LoadLeaderForLocationRequest;
import com.eswaraj.app.eswaraj.volley.LoadLeadersRequest;
import com.eswaraj.app.eswaraj.volley.LoadLocationRequest;
import com.eswaraj.app.eswaraj.volley.LoadProfileUpdateRequest;
import com.eswaraj.app.eswaraj.volley.LocationComplaintCountersRequest;
import com.eswaraj.app.eswaraj.volley.LocationComplaintsRequest;
import com.eswaraj.app.eswaraj.volley.ProfileUpdateRequest;
import com.eswaraj.app.eswaraj.volley.RegisterFacebookUserRequest;
import com.eswaraj.app.eswaraj.volley.RegisterGcmIdRequest;
import com.eswaraj.app.eswaraj.volley.RegisterUserAndDeviceRequest;
import com.eswaraj.app.eswaraj.volley.SingleComplaintRequest;
import com.eswaraj.app.eswaraj.volley.UserComplaintsRequest;
import com.eswaraj.app.eswaraj.widgets.CustomNetworkImageView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

@Module(
        injects = {
                LoginActivity.class,
                LoginFragment.class,
                SelectAmenityActivity.class,
                SelectAmenityFragment.class,
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
                SelectTemplateFragment.class,
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
                UserComplaintsActivity.class,
                MyComplaintsFragment.class,
                UserComplaintsRequest.class,
                SingleComplaintActivity.class,
                SingleComplaintFragment.class,
                ImageFragment.class,
                LoadImageRequest.class,
                CommentsFragment.class,
                CommentsRequest.class,
                CommentPostRequest.class,
                GooglePlacesListFragment.class,
                HomeActivity.class,
                UserSessionUtil.class,
                SplashActivity.class,
                GoogleMapFragment.class,
                ComplaintCloseRequest.class,
                StorageCacheClearingTask.class,
                ProfileUpdateRequest.class,
                LoadProfileUpdateRequest.class,
                MyProfileActivity.class,
                MyProfileFragment.class,
                ConstituencyComplaintsActivity.class,
                MyConstituencyFragment.class,
                LocationComplaintsRequest.class,
                LocationComplaintCountersRequest.class,
                VolleyUtil.class,
                FacebookSharingUtil.class,
                Database.class,
                ComplaintPostService.class,
                ComplaintSummaryOfflineFragment.class,
                GcmUtil.class,
                RegisterGcmIdRequest.class,
                SingleComplaintRequest.class,
                LoadLeadersRequest.class,
                LeaderActivity.class,
                LeaderFragment.class,
                GoogleAnalyticsTracker.class,
                GlobalSessionUtil.class,
                LoadLocationRequest.class,
                GcmService.class,
                CustomNetworkImageView.class,
                ContentActivity.class,
                ContentFragment.class,
                AnalyticsFragment.class,
                ComplaintsFragment.class,
                ComplaintListFragment.class,
                ComplaintFilterActivity.class,
                ConstituencyInfoFragment.class,
                ConstituencyFragment.class,
                ComplaintsMapFragment.class,
                BaseActivity.class,
                UserComplaintsFragment.class,
                ConstituencyComplaintsFragment.class,
                ConstituencySnapshotActivity.class,
                ConstituencySnapshotFragment.class,
                UserSnapshotActivity.class,
                UserSnapshotFragment.class,
                GlobalSearchRequest.class,
                SearchableActivity.class,
                LoadLeaderByIdRequest.class,
                LeaderForComplaintFragment.class,
                LeaderListActivity.class,
                LeaderListFragment.class,
                LocationListActivity.class,
                LocationListFragment.class,
                LoadLeaderForLocationRequest.class,
                MarkHomeActivity.class,
                PlaceSearchActivity.class
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
        return EventBus.builder().logNoSubscriberMessages(false).sendNoSubscriberEvent(false).build();
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
    LoadImageRequest provideComplaintImageRequest() {
        return new LoadImageRequest();
    }

    @Provides @Singleton
    CommentsRequest provideCommentsRequest() {
        return new CommentsRequest();
    }

    @Provides @Singleton
    CommentPostRequest provideCommentPostRequest() {
        return new CommentPostRequest();
    }

    @Provides @Singleton
    ComplaintCloseRequest provideComplaintCloseRequest() {
        return new ComplaintCloseRequest();
    }

    @Provides @Singleton
    StorageCache provideStorageCache() {
        return new StorageCache();
    }

    @Provides
    CameraHelper provideCameraHelper() {
        return new CameraHelper();
    }

    @Provides @Singleton
    ProfileUpdateRequest provideProfileUpdateRequest() {
        return new ProfileUpdateRequest();
    }

    @Provides @Singleton
    LoadProfileUpdateRequest provideLoadProfileUpdateRequest() {
        return new LoadProfileUpdateRequest();
    }

    @Provides @Singleton
    LocationComplaintsRequest provideLocationComplaintsRequest() {
        return new LocationComplaintsRequest();
    }

    @Provides @Singleton
    LocationComplaintCountersRequest provideLocationComplaintCountersRequest() {
        return new LocationComplaintCountersRequest();
    }

    @Provides @Singleton
    VolleyUtil provideVolleyUtil() {
        return new VolleyUtil();
    }

    @Provides @Singleton
    LruBitmapCache provideLruBitmapCache() {
        return new LruBitmapCache(this.applicationContext);
    }

    @Provides @Singleton
    FacebookSharingUtil provideFacebookSharingUtil() {
        return new FacebookSharingUtil();
    }

    @Provides @Singleton
    DatabaseHelper provideDatabaseHelper() {
        return new DatabaseHelper(applicationContext);
    }

    @Provides @Singleton
    Database provideDatabase() {
        return new Database();
    }

    @Provides @Singleton
    NotificationHelper provideNotificationHelper() {
        return new NotificationHelper();
    }

    @Provides @Singleton
    GcmUtil provideGcmUtil() {
        return new GcmUtil();
    }

    @Provides @Singleton
    RegisterGcmIdRequest provideRegisterGcmIdRequest() {
        return new RegisterGcmIdRequest();
    }

    @Provides @Singleton
    SingleComplaintRequest provideSingleComplaintRequest() {
        return new SingleComplaintRequest();
    }

    @Provides @Singleton
    LoadLeadersRequest provideLoadLeadersRequest() {
        return new LoadLeadersRequest();
    }

    @Provides @Singleton
    GoogleAnalyticsTracker provideGoogleAnalyticsTracker() {
        GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker();
        tracker.enableAll();
        tracker.setAsynchronous(true);
        return tracker;
    }

    @Provides @Singleton
    GlobalSessionUtil provideGlobalSessionUtil() {
        return new GlobalSessionUtil();
    }

    @Provides @Singleton
    LoadLocationRequest provideLoadLocationRequest() {
        return new LoadLocationRequest();
    }

    @Provides @Singleton
    GlobalSearchRequest provideGlobalSearchRequest() {
        return new GlobalSearchRequest();
    }

    @Provides @Singleton
    LoadLeaderByIdRequest provideLoadLeaderByIdRequest() {
        return new LoadLeaderByIdRequest();
    }

    @Provides @Singleton
    LoadLeaderForLocationRequest provideLoadLeaderForLocationRequest() {
        return new LoadLeaderForLocationRequest();
    }
}
