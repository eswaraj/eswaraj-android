package com.next.eswaraj.middleware;


import android.content.Context;
import android.location.Location;

import com.next.eswaraj.util.UserSessionUtil;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.next.eswaraj.models.ComplaintDto;
import com.eswaraj.web.dto.UserDto;

import java.io.File;

public interface MiddlewarePostService {

    public void registerDevice(Context context);
    public void updateProfile(Context context, String token, String name, String voterId, Double lat, Double lng);
    public void postComplaint(UserDto userDto, CategoryWithChildCategoryDto amenity, CategoryWithChildCategoryDto template, Location location, String description, File image, Boolean anonymous, String userGoogleLocation);
    public void postComment(UserDto userDto, ComplaintDto complaintDto, String comment);
    public void closeComplaint(ComplaintDto complaintDto);
    public void registerGcmId(Context context, UserSessionUtil userSession);
}
