package com.next.eswaraj.volley;


import android.app.Activity;
import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.next.eswaraj.base.BaseClass;
import com.next.eswaraj.config.Constants;
import com.next.eswaraj.datastore.Cache;
import com.next.eswaraj.events.RegisterDeviceEvent;
import com.next.eswaraj.helpers.NetworkAccessHelper;
import com.next.eswaraj.models.ErrorDto;
import com.next.eswaraj.util.DeviceUtil;
import com.eswaraj.web.dto.RegisterDeviceRequest;
import com.eswaraj.web.dto.UserDto;
import com.google.gson.Gson;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class RegisterUserAndDeviceRequest extends BaseClass {

    @Inject
    EventBus eventBus;
    @Inject
    NetworkAccessHelper networkAccessHelper;
    @Inject
    Cache cache;

    public void processRequest(Context context) {
        String androidId = DeviceUtil.getDeviceid((Activity) context);
        String deviceTypeRef = DeviceUtil.getDeviceTypeRef();

        final RegisterDeviceRequest registerDeviceRequest = new RegisterDeviceRequest();
        registerDeviceRequest.setDeviceId(androidId);
        registerDeviceRequest.setDeviceTypeRef(deviceTypeRef);

        GenericPostVolleyRequest request = new GenericPostVolleyRequest(Constants.SAVE_DEVICE_ANONYMOUS_USER_URL, createErrorListener(context), createSuccessListener(context), registerDeviceRequest);
        this.networkAccessHelper.submitNetworkRequest("RegisterDevice", request);

    }

    private Response.Listener<String> createSuccessListener(final Context context) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                UserDto userDto = new Gson().fromJson(response, UserDto.class);
                RegisterDeviceEvent event = new RegisterDeviceEvent();
                event.setSuccess(true);
                event.setUserDto(userDto);
                eventBus.postSticky(event);
                cache.updateUserData(context, response);
            }
        };
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
                RegisterDeviceEvent event = new RegisterDeviceEvent();
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
}
