package com.next.eswaraj.volley;


import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.next.eswaraj.base.BaseClass;
import com.next.eswaraj.config.Constants;
import com.next.eswaraj.datastore.Cache;
import com.next.eswaraj.events.GetCategoriesDataEvent;
import com.next.eswaraj.helpers.NetworkAccessHelper;
import com.next.eswaraj.models.ErrorDto;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class LoadCategoriesDataRequest extends BaseClass {

    @Inject
    EventBus eventBus;
    @Inject
    Cache cache;
    @Inject
    NetworkAccessHelper networkAccessHelper;

    public void processRequest(Context context) {
        StringRequest request = new StringRequest(Constants.GET_CATEGORIES_URL, createSuccessListener(context), createErrorListener(context));
        this.networkAccessHelper.submitNetworkRequest("GetCategories", request);
    }

    private Response.ErrorListener createErrorListener(final Context context) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorDto errorDto = null;
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null) {
                    errorDto = new Gson().fromJson(new String(response.data), ErrorDto.class);
                }
                GetCategoriesDataEvent event = new GetCategoriesDataEvent();
                event.setSuccess(false);
                if(errorDto == null) {
                    event.setError(error.toString());
                }
                else {
                    event.setError(errorDto.getMessage());
                }
                eventBus.postSticky(event);
            }
        };
    }

    private Response.Listener<String> createSuccessListener(final Context context) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                Gson gson = new Gson();
                try {
                    List<CategoryWithChildCategoryDto> categoryDtoList;
                    GetCategoriesDataEvent event = new GetCategoriesDataEvent();
                    categoryDtoList = gson.fromJson(json, new TypeToken<ArrayList<CategoryWithChildCategoryDto>>(){}.getType());
                    if(categoryDtoList == null || categoryDtoList.size() == 0) {
                        event.setSuccess(false);
                        eventBus.postSticky(event);
                        return;
                    }
                    event.setSuccess(true);
                    event.setCategoryList(categoryDtoList);
                    eventBus.postSticky(event);
                    //Update the cache
                    cache.updateCategoriesData(context, json);
                } catch (JsonParseException e) {
                    GetCategoriesDataEvent event = new GetCategoriesDataEvent();
                    event.setSuccess(false);
                    event.setError("Invalid data from server");
                    eventBus.postSticky(event);
                }
            }
        };
    }
}
