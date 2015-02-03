package com.eswaraj.app.eswaraj.volley;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.events.GetLocationEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.models.ErrorDto;
import com.eswaraj.web.dto.LocationDto;
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


public class LoadLocationRequest extends BaseClass {

    @Inject
    EventBus eventBus;
    @Inject
    Cache cache;
    @Inject
    NetworkAccessHelper networkAccessHelper;


    public void processRequest(Context context, Long id) {
        StringRequest request = new StringRequest(Constants.GET_LOCATION_URL + "/" + id, createSuccessListener(context, id), createErrorListener(context, id));
        this.networkAccessHelper.submitNetworkRequest("GetLocation", request);
    }

    private Response.ErrorListener createErrorListener(final Context context, final Long id) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorDto errorDto = null;
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null) {
                    errorDto = new Gson().fromJson(new String(response.data), ErrorDto.class);
                }
                GetLocationEvent event = new GetLocationEvent();
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

    private Response.Listener<String> createSuccessListener(final Context context, final Long id) {
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
                    LocationDto locationDto = gson.fromJson(json, LocationDto.class);
                    locationDto.setId(id);
                    GetLocationEvent event = new GetLocationEvent();
                    event.setSuccess(true);
                    event.setLocationDto(locationDto);
                    eventBus.postSticky(event);
                    cache.updateLocation(context, id, json);
                } catch (JsonParseException e) {
                    GetLocationEvent event = new GetLocationEvent();
                    event.setSuccess(false);
                    event.setError("Invalid json");
                    eventBus.postSticky(event);
                }
            }
        };
    }
}
