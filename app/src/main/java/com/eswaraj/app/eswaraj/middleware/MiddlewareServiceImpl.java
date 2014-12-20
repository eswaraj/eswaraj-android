package com.eswaraj.app.eswaraj.middleware;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.datastore.CacheInterface;
import com.eswaraj.app.eswaraj.datastore.Server;
import com.eswaraj.app.eswaraj.datastore.ServerInterface;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.web.dto.RegisterFacebookAccountRequest;
import com.eswaraj.web.dto.UserDto;
import com.facebook.Session;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

public class MiddlewareServiceImpl extends BaseClass implements MiddlewareService {
    @Inject
    CacheInterface cache;
    @Inject
    ServerInterface server;

    @Override
    public void loadCategoriesData(Context context) {
        if(cache.isCategoriesDataAvailable(context)) {
            cache.loadCategoriesData(context);
        }
        else {
            server.loadCategoriesData(context);
        }
    }

    @Override
    public void loadCategoriesData(Context context, Boolean dontGetFromCache) {
        if(dontGetFromCache) {
            server.loadCategoriesData(context);
        }
        else {
            if (cache.isCategoriesDataAvailable(context)) {
                cache.loadCategoriesData(context);
            }
            else {
                server.loadCategoriesData(context);
            }
        }
    }

    @Override
    public Boolean isCategoriesDataAvailable(Context context) {
        return cache.isCategoriesDataAvailable(context);
    }

    @Override
    public void updateCategoriesData(Context context, String json) {
        cache.updateCategoriesData(context, json);
    }

    @Override
    public void loadCategoriesImages(Context context, List<CategoryWithChildCategoryDto> categoriesList) {
        if(cache.isCategoriesImagesAvailable(context)) {
            cache.loadCategoriesImages(context, categoriesList);
        }
        else {
            server.loadCategoriesImages(context, categoriesList);
        }
    }

    @Override
    public void loadCategoriesImages(Context context, List<CategoryWithChildCategoryDto> categoriesList, Boolean dontGetFromCache) {
        if(dontGetFromCache) {
            server.loadCategoriesImages(context, categoriesList);
        }
        else {
            if (cache.isCategoriesImagesAvailable(context)) {
                cache.loadCategoriesImages(context, categoriesList);
            }
            else {
                server.loadCategoriesImages(context, categoriesList);
            }
        }
    }

    @Override
    public Boolean isCategoriesImagesAvailable(Context context) {
        return cache.isCategoriesImagesAvailable(context);
    }

    @Override
    public void updateCategoriesImages(Context context) {
        cache.updateCategoriesImages(context);
    }


    @Override
    public void loadUserData(Context context, Session session) {
        if(cache.isUserDataAvailable(context)) {
            cache.loadUserData(context, session);
        }
        else {
            server.loadUserData(context, session);
        }
    }

    @Override
    public void loadUserData(Context context, Session session, Boolean dontGetFromCache) {
        server.loadUserData(context, session);
    }

    @Override
    public Boolean isUserDataAvailable(Context context) {
        return cache.isUserDataAvailable(context);
    }

    @Override
    public void updateUserData(Context context, String json) {
        cache.updateUserData(context, json);
    }

    @Override
    public void loadUserComplaints(Context context, UserDto userDto, Boolean dontGetFromCache) {
        server.loadUserComplaints(context, userDto);
    }

    @Override
    public Boolean isUserComplaintsAvailable(Context context) {
        return cache.isUserComplaintsAvailable(context);
    }

    @Override
    public void updateUserComplaints(Context context, String json) {
        cache.updateUserComplaints(context, json);
    }

    @Override
    public void loadUserComplaints(Context context, UserDto userDto) {
        if(cache.isUserComplaintsAvailable(context)) {
            cache.loadUserComplaints(context, userDto);
        }
        else {
            server.loadUserComplaints(context, userDto);
        }
    }

    @Override
    public void registerDevice(Context context) {
        if(cache.isUserDataAvailable(context)) {
            //If user is already logged in then bypass RegisterUserEvent and directly launch GetUserEvent
            cache.loadUserData(context, null);
        }
        else {
            server.registerDevice(context);
        }
    }

    @Override
    public void saveUserLocation(Context context, UserDto userDto, double lat, double lng) {
        server.saveUserLocation(context, userDto, lat, lng);
    }

    @Override
    public void postComplaint(UserDto userDto, CategoryWithChildCategoryDto categoryDto, Location location, String description, File image) {
        server.postComplaint(userDto, categoryDto, location, description, image);
    }
}
