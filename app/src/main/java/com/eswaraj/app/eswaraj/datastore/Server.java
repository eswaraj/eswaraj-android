package com.eswaraj.app.eswaraj.datastore;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.events.GetCategoriesEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.web.dto.RegisterFacebookAccountRequest;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class Server implements ServerInterface {

    @Inject
    private NetworkAccessHelper networkAccessHelper;
    @Inject
    private CacheInterface cache;
    @Inject
    private EventBus eventBus;


    public void loadCategoriesData(Context context) {
        StringRequest request = new StringRequest(Constants.GET_CATEGORIES_URL, createCategoriesDataReqSuccessListener(context), createCategoriesDataReqErrorListener(context));
        this.networkAccessHelper.submitNetworkRequest("GetCategories", request);
    }


    private Response.ErrorListener createCategoriesDataReqErrorListener(final Context context) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GetCategoriesEvent getCategoriesEvent = new GetCategoriesEvent();
                getCategoriesEvent.setError(error.toString());
                eventBus.postSticky(getCategoriesEvent);
            }
        };
    }

    private Response.Listener<String> createCategoriesDataReqSuccessListener(final Context context) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                Gson gson = new Gson();
                try {
                    List<CategoryWithChildCategoryDto> categoryDtoList;
                    GetCategoriesEvent getCategoriesEvent = new GetCategoriesEvent();
                    categoryDtoList = gson.fromJson(json, new TypeToken<ArrayList<CategoryWithChildCategoryDto>>(){}.getType());
                    getCategoriesEvent.setSuccess(true);
                    getCategoriesEvent.setCategoryList(categoryDtoList);
                    eventBus.postSticky(getCategoriesEvent);
                    //Update the cache
                    cache.updateCategoriesData(context, json);
                } catch (JsonParseException e) {
                    GetCategoriesEvent getCategoriesEvent = new GetCategoriesEvent();
                    getCategoriesEvent.setError("Invalid json");
                    eventBus.postSticky(getCategoriesEvent);
                }
            }
        };
    }


    @Override
    public void registerFacebookUser(Context context, RegisterFacebookAccountRequest request) {
        //TODO: Implement logic
    }
}
