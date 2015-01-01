package com.eswaraj.app.eswaraj.volley;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.ImageType;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.datastore.StorageCache;
import com.eswaraj.app.eswaraj.events.GetCategoriesImagesEvent;
import com.eswaraj.app.eswaraj.events.GetComplaintImageEvent;
import com.eswaraj.app.eswaraj.events.GetProfileImageEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;

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

    public void processRequest(Context context, String url, Long id, ImageType type) {
        if(url != null) {
            if (!url.equals("")) {
                ImageRequest request = new ImageRequest(url, createSuccessListener(context, id, type), 0, 0, null, createErrorListener(context, id, type));
                this.networkAccessHelper.submitNetworkRequest("GetImage" + type + "_" + id, request);
            }
        }
    }

    private Response.ErrorListener createErrorListener(final Context context, final Long id, final ImageType type) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(type == ImageType.COMPLAINT) {
                    GetComplaintImageEvent event = new GetComplaintImageEvent();
                    event.setSuccess(false);
                    event.setError(error.toString());
                    event.setId(id);
                    eventBus.post(event);
                }
                else if(type == ImageType.PROFILE) {
                    GetProfileImageEvent event = new GetProfileImageEvent();
                    event.setSuccess(false);
                    event.setError(error.toString());
                    event.setId(id);
                    eventBus.post(event);
                }
            }
        };
    }

    private Response.Listener<Bitmap> createSuccessListener(final Context context, final Long id, final ImageType type) {
        return new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                storageCache.saveBitmap(bitmap, id, context, type);
                if(type == ImageType.COMPLAINT) {
                    GetComplaintImageEvent event = new GetComplaintImageEvent();
                    event.setSuccess(true);
                    event.setBitmap(bitmap);
                    event.setId(id);
                    eventBus.post(event);
                    cache.updateComplaintImage(context);
                }
                else if(type == ImageType.PROFILE) {
                    GetProfileImageEvent event = new GetProfileImageEvent();
                    event.setSuccess(true);
                    event.setBitmap(bitmap);
                    event.setId(id);
                    eventBus.post(event);
                    cache.updateProfileImage(context);
                }
            }
        };
    }
}
