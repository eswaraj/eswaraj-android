package com.eswaraj.app.eswaraj.volley;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.events.GetCategoriesImagesEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class LoadCategoriesImagesRequest extends BaseClass {

    @Inject
    EventBus eventBus;
    @Inject
    Cache cache;
    @Inject
    NetworkAccessHelper networkAccessHelper;

    private int imageReqCount;
    private AtomicInteger imageResCount;
    private Boolean successImages;
    private String errorImages;

    public LoadCategoriesImagesRequest () {
        super();
        imageReqCount = 0;
        imageResCount = new AtomicInteger(0);
        successImages = true;
        errorImages = null;
    }

    public void processRequest(Context context, List<CategoryWithChildCategoryDto> categoriesList) {
        imageReqCount = categoriesList.size()*2;
        imageResCount.set(0);
        for(CategoryWithChildCategoryDto categoryDto : categoriesList) {
            launchRequest(context, categoryDto.getImageUrl(), true, categoryDto.getId());
            launchRequest(context, categoryDto.getHeaderImageUrl(), false, categoryDto.getId());
        }
    }

    private void launchRequest(Context context, String url, Boolean icon, Long id) {
        if(!url.equals("")) {
            Log.d("LoadCategoriesImages", url);
            String tag = icon ? "GetImage" + id : "GetHeaderImage" + id;
            ImageRequest request = new ImageRequest(url, createSuccessListener(context, id, icon), 0, 0, null, createErrorListener(context));
            this.networkAccessHelper.submitNetworkRequest(tag, request);
        }
        else {
            imageResCount.incrementAndGet();
        }
    }

    private Response.ErrorListener createErrorListener(final Context context) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                successImages = false;
                errorImages = error.toString();
                imageResCount.incrementAndGet();
                if(imageResCount.get() == imageReqCount) {
                    GetCategoriesImagesEvent getCategoriesImagesEvent = new GetCategoriesImagesEvent();
                    getCategoriesImagesEvent.setSuccess(successImages);
                    getCategoriesImagesEvent.setError(errorImages);
                    eventBus.post(getCategoriesImagesEvent);
                    cache.updateCategoriesImages(context, true, successImages);
                }
            }
        };
    }

    private Response.Listener<Bitmap> createSuccessListener(final Context context, final Long id, final Boolean icon) {
        return new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                saveBitmapToFile(bitmap, id, context, icon);
                imageResCount.incrementAndGet();
                if(imageResCount.get() == imageReqCount) {
                    GetCategoriesImagesEvent getCategoriesImagesEvent = new GetCategoriesImagesEvent();
                    getCategoriesImagesEvent.setSuccess(successImages);
                    eventBus.post(getCategoriesImagesEvent);
                    cache.updateCategoriesImages(context, true, successImages);
                }
            }
        };
    }

    private void saveBitmapToFile(Bitmap bitmap, Long id, Context context, Boolean icon) {
        FileOutputStream fileOutput = null;
        try {
            String filename;
            if(icon) {
                filename = "eSwaraj_" + id + ".png";
            }
            else {
                filename = "eSwaraj_banner_" + id + ".png";
            }
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
