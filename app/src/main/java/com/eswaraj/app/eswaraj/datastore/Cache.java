package com.eswaraj.app.eswaraj.datastore;

import android.content.Context;
import android.os.Bundle;

import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.config.PreferenceConstants;
import com.eswaraj.app.eswaraj.config.ServerAccessEnums;
import com.eswaraj.app.eswaraj.helpers.SharedPreferencesHelper;
import com.eswaraj.app.eswaraj.interfaces.CacheInterface;
import com.eswaraj.app.eswaraj.interfaces.DatastoreInterface;

import java.util.Date;

public class Cache implements CacheInterface {

    private static Cache cache = new Cache();

    private Cache(){ }

    public static Cache getInstance( ) {
        return cache;
    }

    public Boolean isDataInCache(DatastoreInterface context, ServerAccessEnums resource) {
        switch (resource) {
            case GET_CATEGORIES:
                return isCategoriesDataInCache(context);
            case GET_LOGGED_IN_USER_DTO:
                return isLoggedInUserDtoInCache(context);
            default:
                return false;
        }
    }

    public void getDataInCache(DatastoreInterface context, ServerAccessEnums resource) {
        switch (resource) {
            case GET_CATEGORIES:
                getCategoriesDataInCache(context);
                break;
            case GET_LOGGED_IN_USER_DTO:
                getLoggedInUserDtoInCache(context);
                break;
            default:
                break;
        }
    }

    private Boolean isCategoriesDataInCache(DatastoreInterface context) {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper((Context) context);
        if(sharedPreferencesHelper.getBoolean(PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.DATA_AVAILABLE, false)) {
            if((new Date().getTime() - sharedPreferencesHelper.getLong(PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.DATA_DOWNLOAD_TIME_IN_MS, 0L)) < Constants.SERVER_DATA_UPDATE_INTERVAL_IN_MS) {
                return true;
            }
        }
        return false;
    }

    private Boolean isLoggedInUserDtoInCache(DatastoreInterface context) {
        //TODO: Implement Logic
        return false;
    }

    private void getCategoriesDataInCache(DatastoreInterface context) {
        Bundle bundle = new Bundle();
        //TODO: Populate bundle with SharedPreferences data here
        context.onDataAvailable(ServerAccessEnums.GET_CATEGORIES, bundle);
    }

    private void getLoggedInUserDtoInCache(DatastoreInterface context) {
        Bundle bundle = new Bundle();
        //TODO: Populate bundle with SharedPreferences data here
        context.onDataAvailable(ServerAccessEnums.GET_LOGGED_IN_USER_DTO, bundle);
    }

    @Override
    public void onNewDataFetched(Context context, ServerAccessEnums resource, Bundle bundle) {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
        switch (resource) {
            case GET_CATEGORIES:
                sharedPreferencesHelper.putString(PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.SERVER_CATEGORY_DATA, bundle.getString("data"));
                sharedPreferencesHelper.putLong(PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.DATA_DOWNLOAD_TIME_IN_MS, new Date().getTime());
                sharedPreferencesHelper.putBoolean(PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.DATA_AVAILABLE, true);
                break;
            case GET_LOGGED_IN_USER_DTO:
                //TODO:Implement
                break;
            default:
                break;
        }
    }
}
