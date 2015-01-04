package com.eswaraj.app.eswaraj.datastore;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.ImageType;
import com.eswaraj.app.eswaraj.volley.CommentPostRequest;
import com.eswaraj.app.eswaraj.volley.CommentsRequest;
import com.eswaraj.app.eswaraj.volley.ComplaintCloseRequest;
import com.eswaraj.app.eswaraj.volley.LoadImageRequest;
import com.eswaraj.app.eswaraj.volley.ComplaintPostRequest;
import com.eswaraj.app.eswaraj.volley.LoadCategoriesDataRequest;
import com.eswaraj.app.eswaraj.volley.LoadCategoriesImagesRequest;
import com.eswaraj.app.eswaraj.volley.LoadProfileUpdateRequest;
import com.eswaraj.app.eswaraj.volley.LocationComplaintCountersRequest;
import com.eswaraj.app.eswaraj.volley.LocationComplaintsRequest;
import com.eswaraj.app.eswaraj.volley.ProfileUpdateRequest;
import com.eswaraj.app.eswaraj.volley.RegisterFacebookUserRequest;
import com.eswaraj.app.eswaraj.volley.RegisterGcmIdRequest;
import com.eswaraj.app.eswaraj.volley.RegisterUserAndDeviceRequest;
import com.eswaraj.app.eswaraj.volley.SingleComplaintRequest;
import com.eswaraj.app.eswaraj.volley.UserComplaintsRequest;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.app.eswaraj.models.ComplaintDto;
import com.eswaraj.web.dto.LocationDto;
import com.eswaraj.web.dto.UserDto;
import com.facebook.Session;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class Server extends BaseClass implements ServerInterface {

    @Inject
    EventBus eventBus;
    @Inject
    LoadCategoriesDataRequest loadCategoriesDataRequest;
    @Inject
    LoadCategoriesImagesRequest loadCategoriesImagesRequest;
    @Inject
    RegisterFacebookUserRequest registerFacebookUserRequest;
    @Inject
    RegisterUserAndDeviceRequest registerUserAndDeviceRequest;
    @Inject
    ComplaintPostRequest complaintPostRequest;
    @Inject
    UserComplaintsRequest userComplaintsRequest;
    @Inject
    LoadImageRequest loadImageRequest;
    @Inject
    CommentsRequest commentsRequest;
    @Inject
    CommentPostRequest commentPostRequest;
    @Inject
    ComplaintCloseRequest complaintCloseRequest;
    @Inject
    ProfileUpdateRequest profileUpdateRequest;
    @Inject
    LoadProfileUpdateRequest loadProfileUpdateRequest;
    @Inject
    LocationComplaintsRequest locationComplaintsRequest;
    @Inject
    LocationComplaintCountersRequest locationComplaintCountersRequest;
    @Inject
    RegisterGcmIdRequest registerGcmIdRequest;
    @Inject
    SingleComplaintRequest singleComplaintRequest;


    public void loadCategoriesData(Context context) {
        loadCategoriesDataRequest.processRequest(context);
    }


    @Override
    public void loadCategoriesImages(Context context, List<CategoryWithChildCategoryDto> categoriesList) {
        loadCategoriesImagesRequest.processRequest(context, categoriesList);
    }


    @Override
    public void loadUserData(Context context, Session session) {
        registerFacebookUserRequest.processRequest(context, session);
    }

    @Override
    public void loadUserComplaints(Context context, UserDto userDto) {
        userComplaintsRequest.processRequest(context, userDto);
    }

    @Override
    public void loadComplaintImage(Context context, String url, Long id) {
        loadImageRequest.processRequest(context, url, id, ImageType.COMPLAINT);
    }

    @Override
    public void loadComments(Context context, ComplaintDto complaintDto, int start, int count) {
        commentsRequest.processRequest(context, complaintDto, start, count);
    }

    @Override
    public void registerDevice(Context context) {
        registerUserAndDeviceRequest.processRequest(context);
    }

    @Override
    public void updateProfile(Context context, String token, String name, Double lat, Double lng) {
        profileUpdateRequest.processRequest(context, token, name, lat, lng);
    }

    @Override
    public void postComplaint(UserDto userDto, CategoryWithChildCategoryDto amenity, CategoryWithChildCategoryDto template, Location location, String description, File image, Boolean anonymous, String userGoogleLocation) {
        complaintPostRequest.processRequest(userDto, amenity, template, location, description, image, anonymous, userGoogleLocation);
    }

    @Override
    public void postComment(UserDto userDto, ComplaintDto complaintDto, String comment) {
        commentPostRequest.processRequest(userDto, complaintDto, comment);
    }

    @Override
    public void closeComplaint(ComplaintDto complaintDto) {
        complaintCloseRequest.processRequest(complaintDto);
    }

    @Override
    public void loadProfileImage(Context context, String url, Long id) {
        loadImageRequest.processRequest(context, url, id, ImageType.PROFILE);
    }

    @Override
    public void loadHeaderImage(Context context, String url, Long id) {
        loadImageRequest.processRequest(context, url, id, ImageType.HEADER);
    }

    @Override
    public void loadProfileUpdates(Context context, String token) {
        loadProfileUpdateRequest.processRequest(context, token);
    }

    @Override
    public void loadLocationComplaints(Context context, LocationDto locationDto, int start, int count) {
        locationComplaintsRequest.processRequest(context, locationDto, start, count);
    }

    @Override
    public void loadLocationComplaintCounters(Context context, LocationDto locationDto) {
        locationComplaintCountersRequest.processRequest(context, locationDto);
    }

    @Override
    public void registerGcmId(Context context) {
        registerGcmIdRequest.processRequest(context);
    }

    @Override
    public void loadSingleComplaint(Context context, Long id) {
        singleComplaintRequest.processRequest(context, id);
    }
}
