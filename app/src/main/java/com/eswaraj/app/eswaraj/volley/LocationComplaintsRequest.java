package com.eswaraj.app.eswaraj.volley;


import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.events.GetLocationComplaintsEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.models.ComplaintDto;
import com.eswaraj.web.dto.LocationDto;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class LocationComplaintsRequest extends BaseClass {

    @Inject
    EventBus eventBus;
    @Inject
    Cache cache;
    @Inject
    NetworkAccessHelper networkAccessHelper;

    public void processRequest(Context context, LocationDto locationDto, int start, int count) {
        StringRequest request = new StringRequest(Constants.getLocationComplaintsUrl(locationDto.getId(), start, count), createSuccessListener(context, locationDto, start, count), createErrorListener(context));
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 3));
        networkAccessHelper.submitNetworkRequest("GetLocationComplaints", request);
    }

    private Response.ErrorListener createErrorListener(final Context context) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GetLocationComplaintsEvent event = new GetLocationComplaintsEvent();
                event.setError(error.toString());
                eventBus.postSticky(event);
            }
        };
    }

    private Response.Listener<String> createSuccessListener(final Context context, final LocationDto locationDto, final int start, final int count) {

        return new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                Gson gson = new Gson();
                try {
                    List<ComplaintDto> complaintDtoList;
                    GetLocationComplaintsEvent event = new GetLocationComplaintsEvent();
                    complaintDtoList = gson.fromJson(json, new TypeToken<List<ComplaintDto>>(){}.getType());
                    event.setSuccess(true);
                    event.setComplaintDtoList(complaintDtoList);
                    eventBus.postSticky(event);
                    //Update the cache
                    cache.updateLocationComplaints(context, locationDto, start, count, json);
                } catch (JsonParseException e) {
                    GetLocationComplaintsEvent event = new GetLocationComplaintsEvent();
                    event.setError("Invalid json");
                    eventBus.postSticky(event);
                }
            }
        };
    }
}
