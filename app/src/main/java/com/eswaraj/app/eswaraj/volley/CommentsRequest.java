package com.eswaraj.app.eswaraj.volley;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.events.GetCommentsEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.CommentDto;
import com.eswaraj.app.eswaraj.models.ComplaintDto;
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
                GetCommentsEvent event = new GetCommentsEvent();
                event.setError(error.toString());
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
