package com.eswaraj.app.eswaraj.datastore;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesImagesEvent;
import com.eswaraj.app.eswaraj.events.GetUserEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.web.dto.AddressDto;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.web.dto.PersonDto;
import com.eswaraj.web.dto.RegisterFacebookAccountRequest;
import com.eswaraj.web.dto.UserDto;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class Server extends BaseClass implements ServerInterface {

    @Inject
    NetworkAccessHelper networkAccessHelper;
    @Inject
    CacheInterface cache;
    @Inject
    EventBus eventBus;

    private int imageReqCount;
    private int imageResCount;
    private Boolean successImages;
    private String errorImages;

    public Server() {
        super();
        imageReqCount = 0;
        imageResCount = 0;
        successImages = true;
        errorImages = null;
    }


    public void loadCategoriesData(Context context) {
        StringRequest request = new StringRequest(Constants.GET_CATEGORIES_URL, createCategoriesDataReqSuccessListener(context), createCategoriesDataReqErrorListener(context));
        this.networkAccessHelper.submitNetworkRequest("GetCategories", request);
    }


    private Response.ErrorListener createCategoriesDataReqErrorListener(final Context context) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GetCategoriesDataEvent getCategoriesDataEvent = new GetCategoriesDataEvent();
                getCategoriesDataEvent.setError(error.toString());
                eventBus.post(getCategoriesDataEvent);
            }
        };
    }

    private Response.Listener<String> createCategoriesDataReqSuccessListener(final Context context) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                Gson gson = new Gson();
                try {
                    List<CategoryWithChildCategoryDto> categoryDtoList;
                    GetCategoriesDataEvent getCategoriesDataEvent = new GetCategoriesDataEvent();
                    categoryDtoList = gson.fromJson(json, new TypeToken<ArrayList<CategoryWithChildCategoryDto>>(){}.getType());
                    getCategoriesDataEvent.setSuccess(true);
                    getCategoriesDataEvent.setCategoryList(categoryDtoList);
                    eventBus.post(getCategoriesDataEvent);
                    //Update the cache
                    cache.updateCategoriesData(context, json);
                } catch (JsonParseException e) {
                    GetCategoriesDataEvent getCategoriesDataEvent = new GetCategoriesDataEvent();
                    getCategoriesDataEvent.setError("Invalid json");
                    eventBus.post(getCategoriesDataEvent);
                }
            }
        };
    }

    @Override
    public void loadCategoriesImages(Context context, List<CategoryWithChildCategoryDto> categoriesList) {
        imageReqCount = categoriesList.size();
        for(CategoryWithChildCategoryDto categoryDto : categoriesList) {
            String url = categoryDto.getImageUrl();
            Log.d("LoadCategoriesImages", url);
            Long id = categoryDto.getId();
            ImageRequest request = new ImageRequest(url, createCategoriesImagesReqSuccessListener(context, id), 0, 0, null, createCategoriesImagesReqErrorListener(context));
            this.networkAccessHelper.submitNetworkRequest("GetImage" + id, request);
        }
    }

    private Response.ErrorListener createCategoriesImagesReqErrorListener(final Context context) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                successImages = false;
                errorImages = error.toString();
                synchronized (this) {
                    imageResCount++;
                }
                if(imageResCount == imageReqCount) {
                    //All image download requests completed. Publish an event now
                    GetCategoriesImagesEvent getCategoriesImagesEvent = new GetCategoriesImagesEvent();
                    getCategoriesImagesEvent.setSuccess(successImages);
                    getCategoriesImagesEvent.setError(errorImages);
                    eventBus.post(getCategoriesImagesEvent);
                }
            }
        };
    }

    private Response.Listener<Bitmap> createCategoriesImagesReqSuccessListener(final Context context, final Long id) {
        return new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                saveBitmapToFile(bitmap, id, context);
                //TODO: Make imageResCount object of a class which is synchronised because right now success and error listeners are not synchronised
                synchronized (this) {
                    imageResCount++;
                }
                if(imageResCount == imageReqCount) {
                    //All image download requests completed. Publish an event now
                    GetCategoriesImagesEvent getCategoriesImagesEvent = new GetCategoriesImagesEvent();
                    getCategoriesImagesEvent.setSuccess(successImages);
                    eventBus.post(getCategoriesImagesEvent);
                }
            }
        };
    }

    @Override
    public void registerFacebookUser(Context context, RegisterFacebookAccountRequest request) {
        //TODO: Implement logic. This is for testing only
        UserDto userDto = new UserDto();
        PersonDto personDto = new PersonDto();
        AddressDto addressDto = new AddressDto();
        addressDto.setLattitude(100.0);
        addressDto.setLongitude(25.0);
        personDto.setPersonAddress(addressDto);
        userDto.setPerson(personDto);

        GetUserEvent event = new GetUserEvent();
        event.setSuccess(true);
        event.setUserDto(userDto);
        eventBus.postSticky(event);
    }

    //Utility functions
    private void saveBitmapToFile(Bitmap bitmap, Long id, Context context) {
        FileOutputStream fileOutput = null;
        try {
            //out = new FileOutputStream("eSwaraj_" + id + ".png");
            String filename = "eSwaraj_" + id + ".png";
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
