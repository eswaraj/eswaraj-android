package com.eswaraj.app.eswaraj.datastore;

import android.content.Context;
import android.support.annotation.NonNull;

import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.config.PreferenceConstants;
import com.eswaraj.app.eswaraj.events.GetCategoriesEvent;
import com.eswaraj.app.eswaraj.helpers.SharedPreferencesHelper;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
        Gson gson = new Gson();
        String json = sharedPreferencesHelper.getString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.SERVER_CATEGORY_DATA, null);
        try {
            List<CategoryWithChildCategoryDto> categoryDtoList;
            GetCategoriesEvent getCategoriesEvent = new GetCategoriesEvent();
            categoryDtoList = gson.fromJson(json, new TypeToken<ArrayList<CategoryWithChildCategoryDto>>(){}.getType());
            getCategoriesEvent.setSuccess(true);
            getCategoriesEvent.setCategoryList(categoryDtoList);
            eventBus.postSticky(getCategoriesEvent);
        } catch (JsonParseException e) {
            //This should never happen since json would only be stored in server if de-serialization was successful in Server class
            GetCategoriesEvent getCategoriesEvent = new GetCategoriesEvent();
            getCategoriesEvent.setError("Invalid json");
            eventBus.postSticky(getCategoriesEvent);
        }
    }


    @Override
    public void updateCategoriesData(Context context, String json) {
        sharedPreferencesHelper.putString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.SERVER_CATEGORY_DATA, json);
        sharedPreferencesHelper.putLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.DATA_DOWNLOAD_TIME_IN_MS, new Date().getTime());
        sharedPreferencesHelper.putBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.DATA_AVAILABLE, true);
    }
}
