package com.next.eswaraj.datastore;

import android.content.Context;
import android.location.Location;

import com.next.eswaraj.base.BaseClass;
import com.next.eswaraj.config.ImageType;
import com.next.eswaraj.util.UserSessionUtil;
import com.next.eswaraj.volley.CommentPostRequest;
import com.next.eswaraj.volley.CommentsRequest;
import com.next.eswaraj.volley.ComplaintCloseRequest;
import com.next.eswaraj.volley.GlobalSearchRequest;
import com.next.eswaraj.volley.LoadImageRequest;
import com.next.eswaraj.volley.ComplaintPostRequest;
import com.next.eswaraj.volley.LoadCategoriesDataRequest;
import com.next.eswaraj.volley.LoadCategoriesImagesRequest;
import com.next.eswaraj.volley.LoadLeaderByIdRequest;
import com.next.eswaraj.volley.LoadLeaderForLocationRequest;
import com.next.eswaraj.volley.LoadLeadersRequest;
import com.next.eswaraj.volley.LoadLocationRequest;
import com.next.eswaraj.volley.LoadProfileUpdateRequest;
import com.next.eswaraj.volley.LocationComplaintCountersRequest;
import com.next.eswaraj.volley.LocationComplaintsRequest;
import com.next.eswaraj.volley.ProfileUpdateRequest;
import com.next.eswaraj.volley.RegisterFacebookUserRequest;
import com.next.eswaraj.volley.RegisterGcmIdRequest;
import com.next.eswaraj.volley.RegisterUserAndDeviceRequest;
import com.next.eswaraj.volley.SingleComplaintRequest;
import com.next.eswaraj.volley.UserComplaintsRequest;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.next.eswaraj.models.ComplaintDto;
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
    @Inject
    LoadLeadersRequest loadLeadersRequest;
    @Inject
    LoadLocationRequest loadLocationRequest;
    @Inject
    GlobalSearchRequest globalSearchRequest;
    @Inject
    LoadLeaderByIdRequest loadLeaderByIdRequest;
    @Inject
    LoadLeaderForLocationRequest loadLeaderForLocationRequest;


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
    public void loadComplaintImage(Context context, String url, Long id, Boolean keep) {
        loadImageRequest.processRequest(context, url, id, ImageType.COMPLAINT, keep);
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
    public void updateProfile(Context context, String token, String name, String voterId, Double lat, Double lng) {
        profileUpdateRequest.processRequest(context, token, name, voterId, lat, lng);
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
    public void loadProfileImage(Context context, String url, Long id, Boolean keep) {
        loadImageRequest.processRequest(context, url, id, ImageType.PROFILE, keep);
    }

    @Override
    public void loadHeaderImage(Context context, String url, Long id, Boolean keep) {
        loadImageRequest.processRequest(context, url, id, ImageType.HEADER, keep);
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
    public void registerGcmId(Context context, UserSessionUtil userSession) {
        registerGcmIdRequest.processRequest(context, userSession);
    }

    @Override
    public void loadSingleComplaint(Context context, Long id) {
        singleComplaintRequest.processRequest(context, id);
    }

    @Override
    public void loadLeaders(Context context, UserSessionUtil userSession) {
        loadLeadersRequest.processRequest(context, userSession);
    }

    @Override
    public void loadLocation(Context context, Long id) {
        loadLocationRequest.processRequest(context, id);
    }

    @Override
    public void globalSearch(Context context, String query) {
        globalSearchRequest.processRequest(context, query);
    }

    @Override
    public void loadLeaderById(Context context, Long id) {
        loadLeaderByIdRequest.processRequest(context, id);
    }

    @Override
    public void loadLeadersForLocation(Context context, LocationDto locationDto) {
        loadLeaderForLocationRequest.processRequest(context, locationDto);
    }
}
