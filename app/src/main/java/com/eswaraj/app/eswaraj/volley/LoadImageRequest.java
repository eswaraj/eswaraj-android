package com.eswaraj.app.eswaraj.volley;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.ImageType;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.datastore.StorageCache;
import com.eswaraj.app.eswaraj.events.GetCategoriesImagesEvent;
import com.eswaraj.app.eswaraj.events.GetComplaintImageEvent;
import com.eswaraj.app.eswaraj.events.GetHeaderImageEvent;
import com.eswaraj.app.eswaraj.events.GetProfileImageEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.ErrorDto;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class LoadImageRequest extends BaseClass {

    @Inject
    NetworkAccessHelper networkAccessHelper;
    @Inject
    EventBus eventBus;
    @Inject
    Cache cache;
    @Inject
    StorageCache storageCache;

    public void processRequest(Context context, String url, Long id, ImageType type, Boolean keep) {
        if(url != null) {
            if (!url.equals("")) {
                ImageRequest request = new ImageRequest(url + "?type=normal", createSuccessListener(context, id, type, keep), 0, 0, null, createErrorListener(context, id, type, keep));
                request.setShouldCache(false);
                this.networkAccessHelper.submitNetworkRequest("GetImage" + type + "_" + id, request);
            }
        }
    }

    private Response.ErrorListener createErrorListener(final Context context, final Long id, final ImageType type, final Boolean keep) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorDto errorDto = null;
                String errorString;
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null) {
                    errorDto = new Gson().fromJson(new String(response.data), ErrorDto.class);
                }
                if(errorDto == null) {
                    errorString = error.toString();
                }
                else {
                    errorString = errorDto.getMessage();
                }
                if(type == ImageType.COMPLAINT) {
                    GetComplaintImageEvent event = new GetComplaintImageEvent();
                    event.setSuccess(false);
                    event.setError(errorString);
                    event.setId(id);
                    eventBus.postSticky(event);
                }
                else if(type == ImageType.PROFILE) {
                    GetProfileImageEvent event = new GetProfileImageEvent();
                    event.setSuccess(false);
                    event.setError(errorString);
                    event.setId(id);
                    eventBus.postSticky(event);
                }
                else if(type == ImageType.HEADER) {
                    GetHeaderImageEvent event = new GetHeaderImageEvent();
                    event.setSuccess(false);
                    event.setError(errorString);
                    event.setId(id);
                    eventBus.postSticky(event);
                }
            }
        };
    }

    private Response.Listener<Bitmap> createSuccessListener(final Context context, final Long id, final ImageType type, final Boolean keep) {
        return new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                storageCache.saveBitmap(bitmap, id, context, type, keep);
                if(type == ImageType.COMPLAINT) {
                    GetComplaintImageEvent event = new GetComplaintImageEvent();
                    event.setSuccess(true);
                    event.setBitmap(bitmap);
                    event.setId(id);
                    eventBus.postSticky(event);
                    cache.updateComplaintImage(context);
                }
                else if(type == ImageType.PROFILE) {
                    GetProfileImageEvent event = new GetProfileImageEvent();
                    event.setSuccess(true);
                    event.setBitmap(bitmap);
                    event.setId(id);
                    eventBus.postSticky(event);
                    cache.updateProfileImage(context);
                }
                else if(type == ImageType.HEADER) {
                    GetHeaderImageEvent event = new GetHeaderImageEvent();
                    event.setSuccess(true);
                    event.setBitmap(bitmap);
                    event.setId(id);
                    eventBus.postSticky(event);
                    cache.updateProfileImage(context);
                }
            }
        };
    }
}
