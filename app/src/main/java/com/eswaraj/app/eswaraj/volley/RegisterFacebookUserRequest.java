package com.eswaraj.app.eswaraj.volley;

import android.app.Activity;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.events.GetCategoriesImagesEvent;
import com.eswaraj.app.eswaraj.events.GetUserEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.util.DeviceUtil;
import com.eswaraj.web.dto.RegisterFacebookAccountRequest;
import com.eswaraj.web.dto.UserDto;
import com.facebook.Session;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class RegisterFacebookUserRequest extends BaseClass {

    @Inject
    EventBus eventBus;
    @Inject
    Cache cache;
    @Inject
    NetworkAccessHelper networkAccessHelper;

    public void processRequest(Context context, Session session, String userExternalId) {
        String androidId = DeviceUtil.getDeviceid((Activity)context);
        String deviceTypeRef = DeviceUtil.getDeviceTypeRef();
        final RegisterFacebookAccountRequest registerFacebookAccountRequest = new RegisterFacebookAccountRequest();

        registerFacebookAccountRequest.setExpireTime(session.getExpirationDate());
        registerFacebookAccountRequest.setFacebookAppId(context.getString(R.string.facebook_app_id));
        registerFacebookAccountRequest.setToken(session.getAccessToken());
        registerFacebookAccountRequest.setDeviceId(androidId);
        registerFacebookAccountRequest.setDeviceTypeRef(deviceTypeRef);
        registerFacebookAccountRequest.setUserExternalId(userExternalId);

        String json = new Gson().toJson(registerFacebookAccountRequest);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RegisterFacebookUserVolleyRequest request = null;
        try {
            request = new RegisterFacebookUserVolleyRequest(createErrorListener(context), createSuccessListener(context), registerFacebookAccountRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        /*
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.SAVE_FACEBOOK_USER_URL, jsonObject, createSuccessListener(context), createErrorListener(context)){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", "My useragent");
                return headers;
            }
        };

        StringRequest request = new StringRequest(Request.Method.POST, Constants.SAVE_FACEBOOK_USER_URL, createSuccessListener(context), createErrorListener(context)){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", "My useragent");
                return headers;
            }

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("expireTime", registerFacebookAccountRequest.getExpireTime().toString());
                params.put("facebookAppId", registerFacebookAccountRequest.getFacebookAppId());
                params.put("token", registerFacebookAccountRequest.getToken());
                params.put("deviceId", registerFacebookAccountRequest.getDeviceId());
                params.put("deviceTypeRef", registerFacebookAccountRequest.getDeviceTypeRef());
                params.put("userExternalId", registerFacebookAccountRequest.getUserExternalId());
                return params;
            }
        };
        */
        this.networkAccessHelper.submitNetworkRequest("RegisterUser", request);

    }

    private Response.Listener<String> createSuccessListener(final Context context) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                UserDto userDto = new Gson().fromJson(response, UserDto.class);
                GetUserEvent event = new GetUserEvent();
                event.setSuccess(true);
                event.setUserDto(userDto);
                eventBus.postSticky(event);
                cache.updateUserData(context, response);
            }
        };
    }

    /*
    private Response.Listener<JSONObject> createSuccessListener(final Context context) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                UserDto userDto = new Gson().fromJson(response.toString(), UserDto.class);
                GetUserEvent event = new GetUserEvent();
                event.setSuccess(true);
                event.setUserDto(userDto);
                eventBus.postSticky(event);
                cache.updateUserData(context, new Gson().toJson(userDto));
            }
        };
    }
    */

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
