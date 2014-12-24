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
        imageReqCount = categoriesList.size() * 2;//Double as each Category have one icon image and one header image
        Boolean icon = null;
        for(CategoryWithChildCategoryDto categoryDto : categoriesList) {
            downloadImage(context, categoryDto.getId(),categoryDto.getImageUrl(), true);
            downloadImage(context, categoryDto.getId(),categoryDto.getHeaderImageUrl(), false);
        }
    }
    private void downloadImage(Context context,Long id, String url, boolean icon){
        if(url != null && !"".equals(url.trim())) {
            Log.d("LoadCategoriesImages", url);
            ImageRequest request = new ImageRequest(url, createSuccessListener(context, id, icon), 0, 0, null, createErrorListener(context));
            this.networkAccessHelper.submitNetworkRequest("GetHeaderImage" + id, request);
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
                    cache.updateCategoriesImages(context);
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
