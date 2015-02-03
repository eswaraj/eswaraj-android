package com.eswaraj.app.eswaraj.volley;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.events.GlobalSearchResultEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.models.ErrorDto;
import com.eswaraj.app.eswaraj.models.GlobalSearchResponseDto;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class GlobalSearchRequest extends BaseClass {

    @Inject
    EventBus eventBus;
    @Inject
    NetworkAccessHelper networkAccessHelper;

    public void processRequest(Context context, String query) {
        StringRequest request = null;
        try {
            request = new StringRequest(Constants.getGlobalSearchUrl(URLEncoder.encode(query, "UTF-8")), createSuccessListener(context), createErrorListener(context));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.networkAccessHelper.submitNetworkRequest("GlobalSearch", request);
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
                GlobalSearchResultEvent event = new GlobalSearchResultEvent();
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
                    List<GlobalSearchResponseDto> globalSearchResponseDtoList;
                    GlobalSearchResultEvent event = new GlobalSearchResultEvent();
                    globalSearchResponseDtoList = gson.fromJson(json, new TypeToken<ArrayList<GlobalSearchResponseDto>>(){}.getType());
                    event.setSuccess(true);
                    event.setGlobalSearchResponseDtoList(globalSearchResponseDtoList);
                    eventBus.postSticky(event);
                } catch (JsonParseException e) {
                    GlobalSearchResultEvent event = new GlobalSearchResultEvent();
                    event.setSuccess(false);
                    event.setError("Invalid json");
                    eventBus.postSticky(event);
                }
            }
        };
    }
}
