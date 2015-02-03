package com.eswaraj.app.eswaraj.volley;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.events.GetUserEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.models.ErrorDto;
import com.eswaraj.app.eswaraj.util.DeviceUtil;
import com.eswaraj.web.dto.RegisterFacebookAccountRequest;
import com.eswaraj.web.dto.UserDto;
import com.facebook.Session;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Date;

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
        String androidId = DeviceUtil.getDeviceid(context);
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
                JsonDeserializer<Date> deser = new JsonDeserializer<Date>() {
                    @Override
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return json == null ? null : new Date(json.getAsLong());
                    }
                };
                JsonSerializer<Date> ser = new JsonSerializer<Date>() {

                    @Override
                    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                        return src == null ? null : new JsonPrimitive(src.getTime());
                    }
                };
                Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, ser).registerTypeAdapter(Date.class, deser).create();
                UserDto userDto = gson.fromJson(response, UserDto.class);
                if(userDto != null) {
                    GetUserEvent event = new GetUserEvent();
                    event.setSuccess(true);
                    event.setUserDto(userDto);
                    event.setToken(session.getAccessToken());
                    event.setDownloadProfilePhoto(true);
                    event.setDataUpdateNeeded(false);
                    eventBus.postSticky(event);
                    cache.updateUserData(context, response);
                }
                else {
                    GetUserEvent event = new GetUserEvent();
                    event.setSuccess(false);
                    event.setError("Invalid json");
                    eventBus.postSticky(event);
                    Log.e("RegisterFacebookUser:Error: json = ", response);
                }
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
                GetUserEvent event = new GetUserEvent();
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
