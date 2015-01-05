package com.eswaraj.app.eswaraj.middleware;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.datastore.CacheInterface;
import com.eswaraj.app.eswaraj.datastore.Database;
import com.eswaraj.app.eswaraj.datastore.Server;
import com.eswaraj.app.eswaraj.datastore.ServerInterface;
import com.eswaraj.app.eswaraj.helpers.DatabaseHelper;
import com.eswaraj.app.eswaraj.models.ComplaintRequestDBItem;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.app.eswaraj.models.ComplaintDto;
import com.eswaraj.web.dto.LocationDto;
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
    @Inject
    Database database;

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
            loadCategoriesData(context);
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
            loadCategoriesImages(context, categoriesList);
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
        if(dontGetFromCache) {
            server.loadUserData(context, session);
        }
        else {
            loadUserData(context, session);
        }
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
        if(dontGetFromCache) {
            server.loadUserComplaints(context, userDto);
        }
        else {
            loadUserComplaints(context, userDto);
        }
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
    public void loadComplaintImage(Context context, String url, Long id, Boolean keep, Boolean dontGetFromCache) {
        if(dontGetFromCache) {
            server.loadComplaintImage(context, url, id, keep);
        }
        else {
            loadComplaintImage(context, url, id, keep);
        }
    }

    @Override
    public Boolean isComplaintImageAvailable(Context context, String url, Long id, Boolean keep) {
        return cache.isComplaintImageAvailable(context, url, id, keep);
    }

    @Override
    public void updateComplaintImage(Context context) {
        cache.updateComplaintImage(context);
    }

    @Override
    public void loadComplaintImage(Context context, String url, Long id, Boolean keep) {
        if(cache.isComplaintImageAvailable(context, url, id, keep)) {
            cache.loadComplaintImage(context, url, id, keep);
        }
        else {
            server.loadComplaintImage(context, url, id, keep);
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
    public void updateProfile(Context context, String token, String name, Double lat, Double lng) {
        server.updateProfile(context, token, name, lat, lng);
    }

    @Override
    public void postComplaint(UserDto userDto, CategoryWithChildCategoryDto amenity, CategoryWithChildCategoryDto template, Location location, String description, File image, Boolean anonymous, String userGoogleLocation) {
        server.postComplaint(userDto, amenity, template, location, description, image, anonymous, userGoogleLocation);
    }

    @Override
    public void loadComments(Context context, ComplaintDto complaintDto, int start, int count, Boolean dontGetFromCache) {
        if(dontGetFromCache) {
            server.loadComments(context, complaintDto, start, count);
        }
        else {
            loadComments(context, complaintDto, start, count);
        }
    }

    @Override
    public Boolean isCommentsAvailable(Context context, ComplaintDto complaintDto, int start, int count) {
        return cache.isCommentsAvailable(context, complaintDto, start, count);
    }

    @Override
    public void updateComments(Context context, String json, ComplaintDto complaintDto, int start, int count) {
        cache.updateComments(context, json, complaintDto, start, count);
    }

    @Override
    public void loadComments(Context context, ComplaintDto complaintDto, int start, int count) {
        if(cache.isCommentsAvailable(context, complaintDto, start, count)) {
            cache.loadComments(context, complaintDto, start, count);
        }
        else {
            server.loadComments(context, complaintDto, start, count);
        }
    }

    @Override
    public void postComment(UserDto userDto, ComplaintDto complaintDto, String comment) {
        server.postComment(userDto, complaintDto, comment);
    }

    @Override
    public void closeComplaint(ComplaintDto complaintDto) {
        server.closeComplaint(complaintDto);
    }

    @Override
    public void loadProfileImage(Context context, String url, Long id, Boolean keep, Boolean dontGetFromCache) {
        if(dontGetFromCache) {
            server.loadProfileImage(context, url, id, keep);
        }
        else {
            loadProfileImage(context, url, id, keep);
        }
    }

    @Override
    public Boolean isProfileImageAvailable(Context context, String url, Long id, Boolean keep) {
        return cache.isProfileImageAvailable(context, url, id, keep);
    }

    @Override
    public void updateProfileImage(Context context) {
        cache.updateProfileImage(context);
    }

    @Override
    public void loadProfileImage(Context context, String url, Long id, Boolean keep) {
        if(cache.isProfileImageAvailable(context, url, id, keep)) {
            cache.loadProfileImage(context, url, id, keep);
        }
        else {
            server.loadProfileImage(context, url, id, keep);
        }
    }

    @Override
    public void loadHeaderImage(Context context, String url, Long id, Boolean keep, Boolean dontGetFromCache) {
        if(dontGetFromCache) {
            server.loadHeaderImage(context, url, id, keep);
        }
        else {
            loadHeaderImage(context, url, id, keep);
        }
    }

    @Override
    public Boolean isHeaderImageAvailable(Context context, String url, Long id, Boolean keep) {
        return cache.isHeaderImageAvailable(context, url, id, keep);
    }

    @Override
    public void updateHeaderImage(Context context) {
        cache.updateHeaderImage(context);
    }

    @Override
    public void loadHeaderImage(Context context, String url, Long id, Boolean keep) {
        if(cache.isHeaderImageAvailable(context, url, id, keep)) {
            cache.loadHeaderImage(context, url, id, keep);
        }
        else {
            server.loadHeaderImage(context, url, id, keep);
        }
    }

    @Override
    public void loadProfileUpdates(Context context, String token, Boolean dontGetFromCache) {
        if(dontGetFromCache) {
            assert false;
            server.loadProfileUpdates(context, token);
        }
        else {
            loadProfileUpdates(context, token);
        }
    }

    @Override
    public Boolean isProfileUpdateAvailable(Context context) {
        return false;
    }

    @Override
    public void updateProfileUpdate(Context context, String token) {
        assert false;
    }

    @Override
    public void loadProfileUpdates(Context context, String token) {
        server.loadProfileUpdates(context, token);
    }

    @Override
    public void loadLocationComplaints(Context context, LocationDto locationDto, int start, int count, Boolean dontGetFromCache) {
        if(dontGetFromCache) {
            server.loadLocationComplaints(context, locationDto, start, count);
        }
        else {
            loadLocationComplaints(context, locationDto, start, count);
        }
    }

    @Override
    public Boolean isLocationComplaintsAvailable(Context context) {
        return cache.isLocationComplaintsAvailable(context);
    }

    @Override
    public void updateLocationComplaints(Context context, LocationDto locationDto, int start, int count, String json) {
        cache.updateLocationComplaints(context, locationDto, start, count, json);
    }

    @Override
    public void loadLocationComplaints(Context context, LocationDto locationDto, int start, int count) {
        if(cache.isLocationComplaintsAvailable(context)) {
            cache.loadLocationComplaints(context, locationDto, start, count);
        }
        else {
            server.loadLocationComplaints(context, locationDto, start, count);
        }
    }

    @Override
    public void loadLocationComplaintCounters(Context context, LocationDto locationDto, Boolean dontGetFromCache) {
        if(dontGetFromCache) {
            server.loadLocationComplaintCounters(context, locationDto);
        }
        else {
            loadLocationComplaintCounters(context, locationDto);
        }
    }

    @Override
    public Boolean isLocationComplaintCountersAvailable(Context context) {
        return cache.isLocationComplaintCountersAvailable(context);
    }

    @Override
    public void updateLocationComplaintCounters(Context context, LocationDto locationDto, String json) {
        cache.updateLocationComplaintCounters(context, locationDto, json);
    }

    @Override
    public void loadLocationComplaintCounters(Context context, LocationDto locationDto) {
        if(cache.isLocationComplaintCountersAvailable(context)) {
            cache.loadLocationComplaintCounters(context, locationDto);
        }
        else {
            server.loadLocationComplaintCounters(context, locationDto);
        }
    }

    @Override
    public void registerGcmId(Context context) {
        server.registerGcmId(context);
    }

    @Override
    public void loadSingleComplaint(Context context, Long id, Boolean dontGetFromCache) {
        if(dontGetFromCache) {
            server.loadSingleComplaint(context, id);
        }
        else {
            loadSingleComplaint(context, id);
        }
    }

    @Override
    public Boolean isSingleComplaintAvailable(Context context, Long id) {
        return cache.isSingleComplaintAvailable(context, id);
    }

    @Override
    public void updateSingleComplaint(Context context, Long id, String json) {
        cache.updateSingleComplaint(context, id, json);
    }

    @Override
    public void loadSingleComplaint(Context context, Long id) {
        if(cache.isSingleComplaintAvailable(context, id)) {
            cache.loadSingleComplaint(context, id);
        }
        else {
            server.loadSingleComplaint(context, id);
        }
    }

    @Override
    public void loadLeaders(Context context, Boolean dontGetFromCache) {
        if(dontGetFromCache) {
            server.loadLeaders(context);
        }
        else {
            loadLeaders(context);
        }
    }

    @Override
    public Boolean isLeadersAvailable(Context context) {
        return cache.isLeadersAvailable(context);
    }

    @Override
    public void updateLeaders(Context context, String json) {
        cache.updateLeaders(context, json);
    }

    @Override
    public void loadLeaders(Context context) {
        if(cache.isLeadersAvailable(context)) {
            cache.loadLeaders(context);
        }
        else {
            server.loadLeaders(context);
        }
    }
//Database

    @Override
    public void postOneComplaint() {
        database.postOneComplaint();
    }

    @Override
    public void saveComplaint(UserDto userDto, CategoryWithChildCategoryDto amenity, CategoryWithChildCategoryDto template, Location location, String description, File image, Boolean anonymous, String userGoogleLocation) {
        database.saveComplaint(userDto, amenity, template, location, description, image, anonymous, userGoogleLocation);
    }
}
