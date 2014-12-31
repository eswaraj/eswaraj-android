package com.eswaraj.app.eswaraj.volley;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.events.ProfileUpdateEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.web.dto.UpdateMobileUserRequestDto;
import com.eswaraj.web.dto.UserDto;
import com.google.gson.Gson;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class ProfileUpdateRequest extends BaseClass {
    @Inject
    EventBus eventBus;
    @Inject
    NetworkAccessHelper networkAccessHelper;

    public void processRequest(String token, String name, Double latitude, Double longitude)
    {
        UpdateMobileUserRequestDto updateMobileUserRequestDto = new UpdateMobileUserRequestDto();
        updateMobileUserRequestDto.setToken(token);
        updateMobileUserRequestDto.setName(name);
        updateMobileUserRequestDto.setLattitude(latitude);
        updateMobileUserRequestDto.setLongitude(longitude);

        GenericPostVolleyRequest request = new GenericPostVolleyRequest(Constants.UPDATE_PROFILE_URL, createErrorListener(), createSuccessListener(), updateMobileUserRequestDto);
        networkAccessHelper.submitNetworkRequest("UpdateProfile", request);
    }

    private Response.Listener<String> createSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                UserDto userDto = new Gson().fromJson(response, UserDto.class);
                ProfileUpdateEvent event = new ProfileUpdateEvent();
                event.setSuccess(true);
                event.setUserDto(userDto);
                eventBus.post(event);
            }
        };
    }


    private Response.ErrorListener createErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ProfileUpdateEvent event = new ProfileUpdateEvent();
                event.setSuccess(false);
                event.setError(error.toString());
                eventBus.post(event);
            }
        };
    }
}
