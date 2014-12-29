package com.eswaraj.app.eswaraj.datastore;

import android.content.Context;
import android.location.Location;

import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.ImageType;
import com.eswaraj.app.eswaraj.volley.CommentPostRequest;
import com.eswaraj.app.eswaraj.volley.CommentsRequest;
import com.eswaraj.app.eswaraj.volley.ComplaintCloseRequest;
import com.eswaraj.app.eswaraj.volley.LoadImageRequest;
import com.eswaraj.app.eswaraj.volley.ComplaintPostRequest;
import com.eswaraj.app.eswaraj.volley.LoadCategoriesDataRequest;
import com.eswaraj.app.eswaraj.volley.LoadCategoriesImagesRequest;
import com.eswaraj.app.eswaraj.volley.RegisterFacebookUserRequest;
import com.eswaraj.app.eswaraj.volley.RegisterUserAndDeviceRequest;
import com.eswaraj.app.eswaraj.volley.UserComplaintsRequest;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.web.dto.ComplaintDto;
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

        //TODO: Uncomment above line and delete the mock code once the server side fixes are done

        /*
        UserDto userDto = new UserDto();
        PersonDto personDto = new PersonDto();
        AddressDto addressDto = new AddressDto();
        addressDto.setLattitude(28.64);
        addressDto.setLongitude(77.22);
        personDto.setPersonAddress(addressDto);
        userDto.setPerson(personDto);
        userDto.setId((long) 84076);
        userDto.setExternalId("47cf0992-42f4-49ec-9ae0-d12ee91d4bae");

        GetUserEvent event = new GetUserEvent();
        event.setSuccess(true);
        event.setUserDto(userDto);
        eventBus.postSticky(event);
        */

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
    public void loadComments(Context context, ComplaintDto complaintDto, int count) {
        commentsRequest.processRequest(context, complaintDto, count);
    }

    @Override
    public void registerDevice(Context context) {
        registerUserAndDeviceRequest.processRequest(context);
    }

    @Override
    public void saveUserLocation(Context context, UserDto userDto, double lat, double lng) {
        //TODO: Implement post request here
    }

    @Override
    public void postComplaint(UserDto userDto, CategoryWithChildCategoryDto categoryDto, Location location, String description, File image, Boolean anonymous, String userGoogleLocation) {
        complaintPostRequest.processRequest(userDto, categoryDto, location, description, image, anonymous, userGoogleLocation);
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
}
