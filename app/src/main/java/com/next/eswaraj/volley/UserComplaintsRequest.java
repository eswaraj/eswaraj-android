package com.next.eswaraj.volley;


import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.next.eswaraj.base.BaseClass;
import com.next.eswaraj.config.Constants;
import com.next.eswaraj.datastore.Cache;
import com.next.eswaraj.events.GetUserComplaintsEvent;
import com.next.eswaraj.helpers.NetworkAccessHelper;
import com.next.eswaraj.models.ComplaintDto;
import com.next.eswaraj.models.ErrorDto;
import com.eswaraj.web.dto.UserDto;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class UserComplaintsRequest extends BaseClass {
    @Inject
    EventBus eventBus;
    @Inject
    Cache cache;
    @Inject
    NetworkAccessHelper networkAccessHelper;

    public void processRequest(Context context, UserDto userDto, int start, int count) {
        Log.d("UserComplaintsRequest", Constants.getUserComplaintsUrl(userDto.getId(), start, count));
        StringRequest request = new StringRequest(Constants.getUserComplaintsUrl(userDto.getId(), start, count), createSuccessListener(context, start, count), createErrorListener(context));
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 3));
        networkAccessHelper.submitNetworkRequest("GetUserComplaints", request);
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
                GetUserComplaintsEvent event = new GetUserComplaintsEvent();
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

    private Response.Listener<String> createSuccessListener(final Context context, final int start, final int count) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                Gson gson = new Gson();
                try {
                    List<ComplaintDto> complaintDtoList;
                    GetUserComplaintsEvent event = new GetUserComplaintsEvent();
                    complaintDtoList = gson.fromJson(json, new TypeToken<List<ComplaintDto>>(){}.getType());
                    if(complaintDtoList == null) {
                        event.setSuccess(false);
                        eventBus.postSticky(event);
                        return;
                    }
                    event.setSuccess(true);
                    event.setComplaintDtoList(complaintDtoList);
                    eventBus.postSticky(event);
                    //Update the cache
                    cache.updateUserComplaints(context, start, count, json);
                } catch (JsonParseException e) {
                    GetUserComplaintsEvent event = new GetUserComplaintsEvent();
                    event.setError("Invalid json");
                    eventBus.postSticky(event);
                }
            }
        };
    }
}
