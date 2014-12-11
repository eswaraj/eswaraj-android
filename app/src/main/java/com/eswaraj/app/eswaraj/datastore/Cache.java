package com.eswaraj.app.eswaraj.datastore;

import android.content.Context;
import android.util.Log;

import com.eswaraj.app.eswaraj.application.EswarajApplication;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.config.PreferenceConstants;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesImagesEvent;
import com.eswaraj.app.eswaraj.helpers.SharedPreferencesHelper;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class Cache extends BaseClass implements CacheInterface {

    @Inject
    SharedPreferencesHelper sharedPreferencesHelper;
    @Inject
    EventBus eventBus;


    public Boolean isCategoriesDataAvailable(Context context) {
        if(sharedPreferencesHelper.getBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_DATA_AVAILABLE, false)) {
            if((new Date().getTime() - sharedPreferencesHelper.getLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_DATA_DOWNLOAD_TIME_IN_MS, 0L)) < Constants.SERVER_DATA_UPDATE_INTERVAL_IN_MS) {
                return true;
            }
        }
        return false;
    }


    public void loadCategoriesData(Context context) {
        Gson gson = new Gson();
        String json = sharedPreferencesHelper.getString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_DATA, null);
        try {
            List<CategoryWithChildCategoryDto> categoryDtoList;
            GetCategoriesDataEvent getCategoriesDataEvent = new GetCategoriesDataEvent();
            categoryDtoList = gson.fromJson(json, new TypeToken<ArrayList<CategoryWithChildCategoryDto>>(){}.getType());
            getCategoriesDataEvent.setSuccess(true);
            getCategoriesDataEvent.setCategoryList(categoryDtoList);
            eventBus.post(getCategoriesDataEvent);
        } catch (JsonParseException e) {
            //This should never happen since json would only be stored in server if de-serialization was successful in Server class
            GetCategoriesDataEvent getCategoriesDataEvent = new GetCategoriesDataEvent();
            getCategoriesDataEvent.setError("Invalid json");
            eventBus.post(getCategoriesDataEvent);
        }
    }

    @Override
    public void updateCategoriesData(Context context, String json) {
        sharedPreferencesHelper.putString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_DATA, json);
        sharedPreferencesHelper.putLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_DATA_DOWNLOAD_TIME_IN_MS, new Date().getTime());
        sharedPreferencesHelper.putBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_DATA_AVAILABLE, true);
    }

    @Override
    public void loadCategoriesImages(Context context, List<CategoryWithChildCategoryDto> categoriesList) {
        //No data needs to be put here. The image file names will be inferred from the categoryId. Just put success=True in event object
        GetCategoriesImagesEvent getCategoriesImagesEvent = new GetCategoriesImagesEvent();
        getCategoriesImagesEvent.setSuccess(true);
        eventBus.post(getCategoriesImagesEvent);
    }

    @Override
    public Boolean isCategoriesImagesAvailable(Context context) {
        if(sharedPreferencesHelper.getBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_IMAGES_AVAILABLE, false)) {
            if((new Date().getTime() - sharedPreferencesHelper.getLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_IMAGES_DOWNLOAD_TIME_IN_MS, 0L)) < Constants.SERVER_DATA_UPDATE_INTERVAL_IN_MS) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateCategoriesImages(Context context) {
        sharedPreferencesHelper.putBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_IMAGES_AVAILABLE, true);
        sharedPreferencesHelper.putLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_IMAGES_DOWNLOAD_TIME_IN_MS, new Date().getTime());
    }
}
