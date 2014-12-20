package com.eswaraj.app.eswaraj.volley;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.events.GetCategoriesImagesEvent;
import com.eswaraj.app.eswaraj.events.GetComplaintImageEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class ComplaintImageRequest extends BaseClass {

    @Inject
    NetworkAccessHelper networkAccessHelper;
    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;

    public void processRequest(Context context, String url, Long id) {
        if(url != "") {
            ImageRequest request = new ImageRequest(url, createSuccessListener(context, id), 0, 0, null, createErrorListener(context));
            this.networkAccessHelper.submitNetworkRequest("GetComplaintImage" + id, request);
        }
    }

    private Response.ErrorListener createErrorListener(final Context context) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    GetComplaintImageEvent event = new GetComplaintImageEvent();
                    event.setSuccess(false);
                    event.setError(error.toString());
                    eventBus.post(event);
            }
        };
    }

    private Response.Listener<Bitmap> createSuccessListener(final Context context, final Long id) {
        return new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                saveBitmapToFile(bitmap, id, context);
                GetComplaintImageEvent event = new GetComplaintImageEvent();
                event.setSuccess(true);
                eventBus.post(event);
                middlewareService.updateComplaintImage(context);
            }
        };
    }

    private void saveBitmapToFile(Bitmap bitmap, Long id, Context context) {
        FileOutputStream fileOutput = null;
        try {
            String filename = "eSwaraj_complaint_" + id + ".png";
            fileOutput = context.openFileOutput(filename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutput);
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutput != null) {
                    fileOutput.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
