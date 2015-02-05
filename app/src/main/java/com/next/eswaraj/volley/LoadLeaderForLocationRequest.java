package com.next.eswaraj.volley;


import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.next.eswaraj.base.BaseClass;
import com.next.eswaraj.config.Constants;
import com.next.eswaraj.events.GetLeadersEvent;
import com.next.eswaraj.helpers.NetworkAccessHelper;
import com.next.eswaraj.models.ErrorDto;
import com.next.eswaraj.models.PoliticalBodyAdminDto;
import com.eswaraj.web.dto.LocationDto;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class LoadLeaderForLocationRequest extends BaseClass {

    @Inject
    EventBus eventBus;
    @Inject
    NetworkAccessHelper networkAccessHelper;

    public void processRequest(Context context, LocationDto locationDto) {
        StringRequest request = new StringRequest(Constants.GET_LEADERS_FOR_LOCATION + "/" + locationDto.getId(), createSuccessListener(context, locationDto), createErrorListener(context));
        networkAccessHelper.submitNetworkRequest("GetLocationLeaders", request);
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
                GetLeadersEvent event = new GetLeadersEvent();
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

    private Response.Listener<String> createSuccessListener(final Context context, final LocationDto locationDto) {

        return new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                Gson gson = new Gson();
                try {
                    List<PoliticalBodyAdminDto> politicalBodyAdminDtoList;
                    GetLeadersEvent event = new GetLeadersEvent();
                    politicalBodyAdminDtoList = gson.fromJson(json, new TypeToken<List<PoliticalBodyAdminDto>>(){}.getType());
                    event.setSuccess(true);
                    event.setPoliticalBodyAdminDtos(politicalBodyAdminDtoList);
                    eventBus.postSticky(event);
                } catch (JsonParseException e) {
                    GetLeadersEvent event = new GetLeadersEvent();
                    event.setError("Invalid json");
                    eventBus.postSticky(event);
                }
            }
        };
    }
}
