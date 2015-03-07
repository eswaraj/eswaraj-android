package com.next.eswaraj.volley;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.next.eswaraj.base.BaseClass;
import com.next.eswaraj.config.Constants;
import com.next.eswaraj.datastore.Cache;
import com.next.eswaraj.events.GetSingleComplaintEvent;
import com.next.eswaraj.helpers.NetworkAccessHelper;
import com.next.eswaraj.models.ComplaintDto;
import com.next.eswaraj.models.ErrorDto;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class SingleComplaintRequest extends BaseClass {

    @Inject
    EventBus eventBus;
    @Inject
    Cache cache;
    @Inject
    NetworkAccessHelper networkAccessHelper;

    public void processRequest(Context context, Long id) {
        StringRequest request = new StringRequest(Constants.GET_SINGLE_COMPLAINT_URL + "/" + id, createSuccessListener(context, id), createErrorListener(context));
        networkAccessHelper.submitNetworkRequest("GetSingleComplaint", request);
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
                GetSingleComplaintEvent event = new GetSingleComplaintEvent();
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

    private Response.Listener<String> createSuccessListener(final Context context, final Long id) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                Gson gson = new Gson();
                try {
                    ComplaintDto complaintDto;
                    GetSingleComplaintEvent event = new GetSingleComplaintEvent();
                    complaintDto = gson.fromJson(json, ComplaintDto.class);
                    if(complaintDto == null) {
                        event.setSuccess(false);
                        eventBus.postSticky(event);
                        return;
                    }
                    event.setSuccess(true);
                    event.setComplaintDto(complaintDto);
                    eventBus.postSticky(event);
                    //Update the cache
                    cache.updateSingleComplaint(context, id, json);
                } catch (JsonParseException e) {
                    GetSingleComplaintEvent event = new GetSingleComplaintEvent();
                    event.setError("Invalid json");
                    eventBus.postSticky(event);
                }
            }
        };
    }
}
