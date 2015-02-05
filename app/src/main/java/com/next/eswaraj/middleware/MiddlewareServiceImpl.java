package com.next.eswaraj.middleware;

import android.content.Context;
import android.location.Location;

import com.next.eswaraj.base.BaseClass;
import com.next.eswaraj.datastore.CacheInterface;
import com.next.eswaraj.datastore.Database;
import com.next.eswaraj.datastore.ServerInterface;
import com.next.eswaraj.util.UserSessionUtil;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.next.eswaraj.models.ComplaintDto;
import com.eswaraj.web.dto.LocationDto;
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
    public void updateCategoriesImages(Context context, Boolean imageDownloadLaunchedBefore, Boolean success) {
        cache.updateCategoriesImages(context, imageDownloadLaunchedBefore, success);
    }

    @Override
    public Boolean wasImageDownloadLaunchedBefore(Context context) {
        return cache.wasImageDownloadLaunchedBefore(context);
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
    public void setUserDataStale(Context context) {
        cache.setUserDataStale(context);
    }

    public Boolean isUserDataStale(Context context) {
        return cache.isUserDataStale(context);
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
    public void updateProfile(Context context, String token, String name, String voterId, Double lat, Double lng) {
        server.updateProfile(context, token, name, voterId, lat, lng);
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
    public void registerGcmId(Context context, UserSessionUtil userSession) {
        server.registerGcmId(context, userSession);
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
    public void loadLeaders(Context context, UserSessionUtil userSession, Boolean dontGetFromCache) {
        if(dontGetFromCache) {
            server.loadLeaders(context, userSession);
        }
        else {
            loadLeaders(context, userSession);
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
    public void loadLeaders(Context context, UserSessionUtil userSession) {
        if(cache.isLeadersAvailable(context)) {
            cache.loadLeaders(context, userSession);
        }
        else {
            server.loadLeaders(context, userSession);
        }
    }

    @Override
    public void loadLocation(Context context, Long id, Boolean dontGetFromCache) {
        if(dontGetFromCache) {
            server.loadLocation(context, id);
        }
        else {
            loadLocation(context, id);
        }
    }

    @Override
    public Boolean isLocationAvailable(Context context, Long id) {
        return cache.isLocationAvailable(context, id);
    }

    @Override
    public void updateLocation(Context context, Long id, String json) {
        cache.updateLocation(context, id, json);
    }

    @Override
    public void loadLocation(Context context, Long id) {
        if(cache.isLocationAvailable(context, id)) {
            cache.loadLocation(context, id);
        }
        else {
            server.loadLocation(context, id);
        }
    }

    @Override
    public void globalSearch(Context context, String query, Boolean dontGetFromCache) {
        if(dontGetFromCache) {
            server.globalSearch(context, query);
        }
        else {
            globalSearch(context, query);
        }
    }

    @Override
    public Boolean isGlobalSearchAvailable(Context context, String query) {
        return cache.isGlobalSearchAvailable(context, query);
    }

    @Override
    public void updateGlobalSearch(Context context, String query, String json) {
        cache.updateGlobalSearch(context, query, json);
    }

    @Override
    public void globalSearch(Context context, String query) {
        if(cache.isGlobalSearchAvailable(context, query)) {
            cache.globalSearch(context, query);
        }
        else {
            server.globalSearch(context, query);
        }
    }

    @Override
    public void loadLeaderById(Context context, Long id, Boolean dontGetFromCache) {
        if(dontGetFromCache) {
            server.loadLeaderById(context, id);
        }
        else {
            loadLeaderById(context, id);
        }
    }

    @Override
    public Boolean isLeaderAvailable(Context context, Long id) {
        return cache.isLeaderAvailable(context, id);
    }

    @Override
    public void updateLeader(Context context, Long id, String json) {
        cache.updateLeader(context, id, json);
    }

    @Override
    public void loadLeaderById(Context context, Long id) {
        if(cache.isLeaderAvailable(context, id)) {
            cache.loadLeaderById(context, id);
        }
        else {
            server.loadLeaderById(context, id);
        }
    }

    @Override
    public void loadLeadersForLocation(Context context, LocationDto locationDto, Boolean dontGetFromCache) {
        if(dontGetFromCache) {
            server.loadLeadersForLocation(context, locationDto);
        }
        else {
            loadLeadersForLocation(context, locationDto);
        }
    }

    @Override
    public Boolean isLeadersForLocationAvailable(Context context, LocationDto locationDto) {
        return cache.isLeadersForLocationAvailable(context, locationDto);
    }

    @Override
    public void updateLeadersForLocation(Context context, LocationDto locationDto, String json) {
        cache.updateLeadersForLocation(context, locationDto, json);
    }

    @Override
    public void loadLeadersForLocation(Context context, LocationDto locationDto) {
        if(cache.isLeadersForLocationAvailable(context, locationDto)) {
            cache.loadLeadersForLocation(context, locationDto);
        }
        else {
            server.loadLeadersForLocation(context, locationDto);
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
