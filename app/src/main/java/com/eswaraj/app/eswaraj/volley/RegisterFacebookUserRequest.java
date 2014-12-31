package com.eswaraj.app.eswaraj.volley;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.events.GetUserEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.util.DeviceUtil;
import com.eswaraj.web.dto.RegisterFacebookAccountRequest;
import com.eswaraj.web.dto.UserDto;
import com.facebook.Session;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class RegisterFacebookUserRequest extends BaseClass {

    @Inject
    EventBus eventBus;
    @Inject
    Cache cache;
    @Inject
    NetworkAccessHelper networkAccessHelper;

    public void processRequest(Context context, Session session) {
        String androidId = DeviceUtil.getDeviceid((Activity)context);
        String deviceTypeRef = DeviceUtil.getDeviceTypeRef();
        final RegisterFacebookAccountRequest registerFacebookAccountRequest = new RegisterFacebookAccountRequest();

        registerFacebookAccountRequest.setExpireTime(session.getExpirationDate());
        registerFacebookAccountRequest.setFacebookAppId(context.getString(R.string.facebook_app_id_old));
        registerFacebookAccountRequest.setToken(session.getAccessToken());
        registerFacebookAccountRequest.setDeviceId(androidId);
        registerFacebookAccountRequest.setDeviceTypeRef(deviceTypeRef);
        registerFacebookAccountRequest.setUserExternalId(null);

        RegisterFacebookUserVolleyRequest request = null;
        try {
            request = new RegisterFacebookUserVolleyRequest(createErrorListener(context), createSuccessListener(context, session), registerFacebookAccountRequest, Constants.SAVE_FACEBOOK_USER_URL);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        this.networkAccessHelper.submitNetworkRequest("RegisterUser", request);

    }

    private Response.Listener<String> createSuccessListener(final Context context, final Session session) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                UserDto userDto = new Gson().fromJson(response, UserDto.class);
                GetUserEvent event = new GetUserEvent();
                event.setSuccess(true);
                event.setUserDto(userDto);
                event.setToken(session.getAccessToken());
                eventBus.postSticky(event);
                cache.updateUserData(context, response);
            }
        };
    }


    private Response.ErrorListener createErrorListener(final Context context) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GetUserEvent event = new GetUserEvent();
                event.setSuccess(false);
                event.setError(error.toString());
                eventBus.postSticky(event);
            }
        };
    }
}
