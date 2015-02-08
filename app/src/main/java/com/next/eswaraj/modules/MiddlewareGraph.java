package com.next.eswaraj.modules;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.next.eswaraj.activities.AddDetailsActivity;
import com.next.eswaraj.activities.ComplaintFilterActivity;
import com.next.eswaraj.activities.ComplaintSummaryActivity;
import com.next.eswaraj.activities.ConstituencyComplaintsActivity;
import com.next.eswaraj.activities.ConstituencySnapshotActivity;
import com.next.eswaraj.activities.LeaderListActivity;
import com.next.eswaraj.activities.LocationListActivity;
import com.next.eswaraj.activities.MarkHomeActivity;
import com.next.eswaraj.activities.PlaceSearchActivity;
import com.next.eswaraj.activities.SearchableActivity;
import com.next.eswaraj.activities.UserComplaintsActivity;
import com.next.eswaraj.activities.ContentActivity;
import com.next.eswaraj.activities.HomeActivity;
import com.next.eswaraj.activities.LeaderActivity;
import com.next.eswaraj.activities.LoginActivity;
import com.next.eswaraj.activities.MyProfileActivity;
import com.next.eswaraj.activities.SelectAmenityActivity;
import com.next.eswaraj.activities.SelectTemplateActivity;
import com.next.eswaraj.activities.SingleComplaintActivity;
import com.next.eswaraj.activities.SplashActivity;
import com.next.eswaraj.activities.UserSnapshotActivity;
import com.next.eswaraj.activities.YoutubeActivity;
import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.datastore.Cache;
import com.next.eswaraj.datastore.CacheInterface;
import com.next.eswaraj.datastore.Database;
import com.next.eswaraj.datastore.LruBitmapCache;
import com.next.eswaraj.datastore.Server;
import com.next.eswaraj.datastore.StorageCache;
import com.next.eswaraj.fragments.AddDetailsFragment;
import com.next.eswaraj.fragments.AnalyticsFragment;
import com.next.eswaraj.fragments.ComplaintListFragment;
import com.next.eswaraj.fragments.ComplaintSummaryOfflineFragment;
import com.next.eswaraj.fragments.ConstituencyComplaintsFragment;
import com.next.eswaraj.fragments.ConstituencySnapshotFragment;
import com.next.eswaraj.fragments.LeaderForComplaintFragment;
import com.next.eswaraj.fragments.LeaderListFragment;
import com.next.eswaraj.fragments.LocationListFragment;
import com.next.eswaraj.fragments.UserComplaintsFragment;
import com.next.eswaraj.fragments.ComplaintsMapFragment;
import com.next.eswaraj.fragments.ConstituencyInfoFragment;
import com.next.eswaraj.fragments.ContentFragment;
import com.next.eswaraj.fragments.LeaderFragment;
import com.next.eswaraj.fragments.MyProfileFragment;
import com.next.eswaraj.fragments.SelectAmenityFragment;
import com.next.eswaraj.fragments.AmenityBannerFragment;
import com.next.eswaraj.fragments.CommentsFragment;
import com.next.eswaraj.fragments.ComplaintSummaryFragment;
import com.next.eswaraj.fragments.GoogleMapFragment;
import com.next.eswaraj.fragments.GooglePlacesListFragment;
import com.next.eswaraj.fragments.ImageFragment;
import com.next.eswaraj.fragments.SelectTemplateFragment;
import com.next.eswaraj.fragments.SingleComplaintFragment;
import com.next.eswaraj.fragments.LoginFragment;
import com.next.eswaraj.fragments.UserSnapshotFragment;
import com.next.eswaraj.helpers.CameraHelper;
import com.next.eswaraj.helpers.DatabaseHelper;
import com.next.eswaraj.helpers.GoogleAnalyticsTracker;
import com.next.eswaraj.helpers.NetworkAccessHelper;
import com.next.eswaraj.helpers.NotificationHelper;
import com.next.eswaraj.helpers.ReverseGeocodingTask;
import com.next.eswaraj.helpers.SharedPreferencesHelper;
import com.next.eswaraj.helpers.StorageCacheClearingTask;
import com.next.eswaraj.services.ComplaintPostService;
import com.next.eswaraj.services.GcmService;
import com.next.eswaraj.util.DeviceUtil;
import com.next.eswaraj.util.FacebookSharingUtil;
import com.next.eswaraj.util.GcmUtil;
import com.next.eswaraj.util.GlobalSessionUtil;
import com.next.eswaraj.util.GooglePlacesUtil;
import com.next.eswaraj.util.LocationUtil;
import com.next.eswaraj.middleware.MiddlewareServiceImpl;
import com.next.eswaraj.util.FacebookLoginUtil;
import com.next.eswaraj.util.InternetServicesCheckUtil;
import com.next.eswaraj.util.LocationServicesCheckUtil;
import com.next.eswaraj.util.UserSessionUtil;
import com.next.eswaraj.util.VolleyUtil;
import com.next.eswaraj.volley.CommentPostRequest;
import com.next.eswaraj.volley.CommentsRequest;
import com.next.eswaraj.volley.ComplaintCloseRequest;
import com.next.eswaraj.volley.GlobalSearchRequest;
import com.next.eswaraj.volley.LoadImageRequest;
import com.next.eswaraj.volley.ComplaintPostRequest;
import com.next.eswaraj.volley.LoadCategoriesDataRequest;
import com.next.eswaraj.volley.LoadCategoriesImagesRequest;
import com.next.eswaraj.volley.LoadLeaderByIdRequest;
import com.next.eswaraj.volley.LoadLeaderForLocationRequest;
import com.next.eswaraj.volley.LoadLeadersRequest;
import com.next.eswaraj.volley.LoadLocationRequest;
import com.next.eswaraj.volley.LoadProfileUpdateRequest;
import com.next.eswaraj.volley.LocationComplaintCountersRequest;
import com.next.eswaraj.volley.LocationComplaintsRequest;
import com.next.eswaraj.volley.ProfileUpdateRequest;
import com.next.eswaraj.volley.RegisterFacebookUserRequest;
import com.next.eswaraj.volley.RegisterGcmIdRequest;
import com.next.eswaraj.volley.RegisterUserAndDeviceRequest;
import com.next.eswaraj.volley.SingleComplaintRequest;
import com.next.eswaraj.volley.UserComplaintsRequest;
import com.next.eswaraj.widgets.CustomNetworkImageView;

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
                ComplaintSummaryFragment.class,
                ComplaintSummaryActivity.class,
                UserComplaintsActivity.class,
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
                ComplaintListFragment.class,
                ComplaintFilterActivity.class,
                ConstituencyInfoFragment.class,
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
