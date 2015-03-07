package com.next.eswaraj.volley;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.next.eswaraj.base.BaseClass;
import com.next.eswaraj.config.Constants;
import com.next.eswaraj.datastore.Cache;
import com.next.eswaraj.events.GetLeadersEvent;
import com.next.eswaraj.helpers.NetworkAccessHelper;
import com.next.eswaraj.models.ErrorDto;
import com.next.eswaraj.models.PoliticalBodyAdminDto;
import com.next.eswaraj.util.UserSessionUtil;
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
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class LoadLeadersRequest extends BaseClass {

    @Inject
    EventBus eventBus;
    @Inject
    Cache cache;
    @Inject
    NetworkAccessHelper networkAccessHelper;

    public void processRequest(Context context, UserSessionUtil userSession) {
        StringRequest request = new StringRequest(Constants.getLeadersUrl(userSession.getUser().getPerson().getPersonAddress().getLattitude(), userSession.getUser().getPerson().getPersonAddress().getLongitude()), createSuccessListener(context), createErrorListener(context));
        this.networkAccessHelper.submitNetworkRequest("GetLeaders", request);
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

    private Response.Listener<String> createSuccessListener(final Context context) {
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
                    List<PoliticalBodyAdminDto> politicalBodyAdminDtos;
                    politicalBodyAdminDtos = gson.fromJson(json, new TypeToken<List<PoliticalBodyAdminDto>>(){}.getType());
                    GetLeadersEvent event = new GetLeadersEvent();
                    if(politicalBodyAdminDtos == null) {
                        event.setSuccess(false);
                        eventBus.postSticky(event);
                        return;
                    }
                    event.setSuccess(true);
                    event.setLoadProfilePhotos(false);
                    event.setPoliticalBodyAdminDtos(politicalBodyAdminDtos);
                    eventBus.postSticky(event);
                    cache.updateLeaders(context, json);
                } catch (JsonParseException e) {
                    GetLeadersEvent event = new GetLeadersEvent();
                    event.setSuccess(false);
                    event.setError("Invalid json");
                    eventBus.postSticky(event);
                }
            }
        };
    }
}
