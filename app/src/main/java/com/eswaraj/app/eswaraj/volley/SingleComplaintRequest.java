package com.eswaraj.app.eswaraj.volley;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.events.GetSingleComplaintEvent;
import com.eswaraj.app.eswaraj.events.GetUserComplaintsEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.models.ComplaintDto;
import com.eswaraj.web.dto.UserDto;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.util.List;

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
        StringRequest request = new StringRequest(Constants.GET_SINGLE_COMPLAINT_URL + "/" + id, createSuccessListener(context), createErrorListener(context));
        networkAccessHelper.submitNetworkRequest("GetSingleComplaint", request);
    }

    private Response.ErrorListener createErrorListener(final Context context) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GetSingleComplaintEvent event = new GetSingleComplaintEvent();
                event.setError(error.toString());
                eventBus.post(event);
            }
        };
    }

    private Response.Listener<String> createSuccessListener(final Context context) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                Gson gson = new Gson();
                try {
                    ComplaintDto complaintDto;
                    GetSingleComplaintEvent event = new GetSingleComplaintEvent();
                    complaintDto = gson.fromJson(json, ComplaintDto.class);
                    event.setSuccess(true);
                    event.setComplaintDto(complaintDto);
                    eventBus.post(event);
                    //Update the cache
                    cache.updateUserComplaints(context, json);
                } catch (JsonParseException e) {
                    GetSingleComplaintEvent event = new GetSingleComplaintEvent();
                    event.setError("Invalid json");
                    eventBus.post(event);
                }
            }
        };
    }
}
