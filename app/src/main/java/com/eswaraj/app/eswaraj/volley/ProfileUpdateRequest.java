package com.eswaraj.app.eswaraj.volley;


import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.events.ProfileUpdateEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.models.ErrorDto;
import com.eswaraj.web.dto.UpdateMobileUserRequestDto;
import com.eswaraj.web.dto.UserDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Date;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class ProfileUpdateRequest extends BaseClass {
    @Inject
    EventBus eventBus;
    @Inject
    NetworkAccessHelper networkAccessHelper;
    @Inject
    Cache cache;

    public void processRequest(Context context, String token, String name, String voterId, Double latitude, Double longitude)
    {
        UpdateMobileUserRequestDto updateMobileUserRequestDto = new UpdateMobileUserRequestDto();
        updateMobileUserRequestDto.setToken(token);
        updateMobileUserRequestDto.setName(name);
        updateMobileUserRequestDto.setLattitude(latitude);
        updateMobileUserRequestDto.setLongitude(longitude);
        updateMobileUserRequestDto.setVoterId(voterId);

        GenericPostVolleyRequest request = new GenericPostVolleyRequest(Constants.UPDATE_PROFILE_URL, createErrorListener(), createSuccessListener(context), updateMobileUserRequestDto);
        networkAccessHelper.submitNetworkRequest("UpdateProfile", request);
    }

    private Response.Listener<String> createSuccessListener(final Context context) {
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
                    ProfileUpdateEvent event = new ProfileUpdateEvent();
                    event.setSuccess(true);
                    event.setUserDto(userDto);
                    eventBus.postSticky(event);
                    cache.updateUserData(context, response);
                }
                else {
                    ProfileUpdateEvent event = new ProfileUpdateEvent();
                    event.setSuccess(false);
                    event.setError("Invalid json");
                    eventBus.postSticky(event);
                }
            }
        };
    }


    private Response.ErrorListener createErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorDto errorDto = null;
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null) {
                    errorDto = new Gson().fromJson(new String(response.data), ErrorDto.class);
                }
                ProfileUpdateEvent event = new ProfileUpdateEvent();
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
