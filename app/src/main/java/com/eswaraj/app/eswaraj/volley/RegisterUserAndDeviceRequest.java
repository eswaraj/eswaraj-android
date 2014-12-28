package com.eswaraj.app.eswaraj.volley;


import android.app.Activity;
import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.events.RegisterDeviceEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.util.DeviceUtil;
import com.eswaraj.web.dto.RegisterDeviceRequest;
import com.eswaraj.web.dto.UserDto;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class RegisterUserAndDeviceRequest extends BaseClass {

    @Inject
    EventBus eventBus;
    @Inject
    NetworkAccessHelper networkAccessHelper;

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
            }
        };
    }


    private Response.ErrorListener createErrorListener(final Context context) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                RegisterDeviceEvent event = new RegisterDeviceEvent();
                event.setSuccess(false);
                event.setError(error.toString());
                eventBus.postSticky(event);
            }
        };
    }
}
