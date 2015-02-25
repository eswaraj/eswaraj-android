package com.next.eswaraj.volley;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import com.next.eswaraj.base.BaseClass;
import com.next.eswaraj.config.Constants;
import com.next.eswaraj.config.TimelineType;
import com.next.eswaraj.datastore.Cache;
import com.next.eswaraj.events.GetTimelineEvent;
import com.next.eswaraj.helpers.NetworkAccessHelper;
import com.next.eswaraj.models.ErrorDto;
import com.next.eswaraj.models.TimelineDto;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class LoadTimelineRequest extends BaseClass {

    @Inject
    EventBus eventBus;
    @Inject
    Cache cache;
    @Inject
    NetworkAccessHelper networkAccessHelper;

    public void processRequest(Context context, TimelineType type, Long id, int start, int count) {
        String url = "";
        switch (type) {
            case PROMISE:
                url = Constants.getPromiseTimelineUrl(id, start, count);
                break;
            case LEADER:
                url = Constants.getLeaderTimelineUrl(id, start, count);
                break;
            case LOCATION:
                url = Constants.getLocationTimelineUrl(id, start, count);
                break;
        }
        StringRequest request = new StringRequest(url, createSuccessListener(context, type, id, start, count), createErrorListener(context, type));
        this.networkAccessHelper.submitNetworkRequest("GetTimeline" + type, request);
    }

    private Response.ErrorListener createErrorListener(final Context context, final TimelineType type) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorDto errorDto = null;
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null) {
                    errorDto = new Gson().fromJson(new String(response.data), ErrorDto.class);
                }
                GetTimelineEvent event = new GetTimelineEvent();
                event.setSuccess(false);
                event.setType(type);
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

    private Response.Listener<String> createSuccessListener(final Context context, final TimelineType type, final Long id, final int start, final int count) {
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
                    List<TimelineDto> timelineDtoList;
                    timelineDtoList = gson.fromJson(json, new TypeToken<List<TimelineDto>>(){}.getType());
                    GetTimelineEvent event = new GetTimelineEvent();
                    event.setSuccess(true);
                    event.setType(type);
                    event.setTimelineDtoList(timelineDtoList);
                    eventBus.postSticky(event);
                    cache.updateTimeline(context, type, id, start, count, json);
                } catch (JsonParseException e) {
                    GetTimelineEvent event = new GetTimelineEvent();
                    event.setSuccess(false);
                    event.setError("Invalid json");
                    eventBus.postSticky(event);
                }
            }
        };
    }
}
