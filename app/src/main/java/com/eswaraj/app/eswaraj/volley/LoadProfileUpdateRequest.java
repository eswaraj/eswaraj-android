package com.eswaraj.app.eswaraj.volley;


import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.GetProfileEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
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
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class LoadProfileUpdateRequest extends BaseClass {

    @Inject
    EventBus eventBus;
    @Inject
    Cache cache;
    @Inject
    NetworkAccessHelper networkAccessHelper;

    public void processRequest(Context context, String token) {
        StringRequest request = new StringRequest(Constants.GET_PROFILE_URL + "/" +token, createSuccessListener(context, token), createErrorListener(context));
        this.networkAccessHelper.submitNetworkRequest("GetProfile", request);
    }

    private Response.ErrorListener createErrorListener(final Context context) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GetProfileEvent event = new GetProfileEvent();
                event.setSuccess(false);
                event.setError(error.toString());
                eventBus.post(event);
            }
        };
    }

    private Response.Listener<String> createSuccessListener(final Context context, final String token) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
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
                try {
                    UserDto userDto = gson.fromJson(json, UserDto.class);
                    GetProfileEvent event = new GetProfileEvent();
                    event.setSuccess(true);
                    event.setUserDto(userDto);
                    event.setToken(token);
                    eventBus.post(event);
                    cache.updateUserData(context, json);
                } catch (JsonParseException e) {
                    GetProfileEvent event = new GetProfileEvent();
                    event.setSuccess(false);
                    event.setError("Invalid json");
                    eventBus.postSticky(event);
                }
            }
        };
    }
}
