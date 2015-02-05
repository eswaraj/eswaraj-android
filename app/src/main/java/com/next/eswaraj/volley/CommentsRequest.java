package com.next.eswaraj.volley;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.next.eswaraj.base.BaseClass;
import com.next.eswaraj.config.Constants;
import com.next.eswaraj.datastore.Cache;
import com.next.eswaraj.events.GetCommentsEvent;
import com.next.eswaraj.helpers.NetworkAccessHelper;
import com.next.eswaraj.models.CommentDto;
import com.next.eswaraj.models.ComplaintDto;
import com.next.eswaraj.models.ErrorDto;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class CommentsRequest extends BaseClass {

    @Inject
    EventBus eventBus;
    @Inject
    Cache cache;
    @Inject
    NetworkAccessHelper networkAccessHelper;

    public void processRequest(Context context, ComplaintDto complaintDto, int start, int count) {
        StringRequest request = new StringRequest(Constants.getCommentsUrl(complaintDto.getId(), start, count), createSuccessListener(context, complaintDto, start, count), createErrorListener(context));
        this.networkAccessHelper.submitNetworkRequest("GetComments" + complaintDto.getId(), request);
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
                GetCommentsEvent event = new GetCommentsEvent();
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

    private Response.Listener<String> createSuccessListener(final Context context, final ComplaintDto complaintDto, final int start, final int count) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                Gson gson = new Gson();
                try {
                    List<CommentDto> commentDtos;
                    GetCommentsEvent event = new GetCommentsEvent();
                    commentDtos = gson.fromJson(json, new TypeToken<List<CommentDto>>(){}.getType());
                    event.setSuccess(true);
                    event.setCommentDtos(commentDtos);
                    eventBus.postSticky(event);
                    //Update the cache
                    cache.updateComments(context, json, complaintDto, start, count);
                } catch (JsonParseException e) {
                    GetCommentsEvent event = new GetCommentsEvent();
                    event.setError("Invalid json");
                    eventBus.postSticky(event);
                }
            }
        };
    }
}
