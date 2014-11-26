package com.eswaraj.app.eswaraj.datastore;

import android.content.Context;

import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.config.PreferenceConstants;
import com.eswaraj.app.eswaraj.helpers.SharedPreferencesHelper;

import java.util.Date;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class Cache implements CacheInterface {

    @Inject
    private SharedPreferencesHelper sharedPreferencesHelper;
    @Inject
    private EventBus eventBus;


    public Boolean isCategoriesDataAvailable(Context context) {
        if(sharedPreferencesHelper.getBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.DATA_AVAILABLE, false)) {
            if((new Date().getTime() - sharedPreferencesHelper.getLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.DATA_DOWNLOAD_TIME_IN_MS, 0L)) < Constants.SERVER_DATA_UPDATE_INTERVAL_IN_MS) {
                return true;
            }
        }
        return false;
    }


    public void loadCategoriesData(Context context) {
        //TODO: Create the CategoryDto by loading data from SharedPreferences and emitting a LoadCategoryEvent on eventBus
    }


    @Override
    public void updateCategoriesData(Context context, String json) {
        sharedPreferencesHelper.putString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.SERVER_CATEGORY_DATA, json);
        sharedPreferencesHelper.putLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.DATA_DOWNLOAD_TIME_IN_MS, new Date().getTime());
        sharedPreferencesHelper.putBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.DATA_AVAILABLE, true);
    }
}
