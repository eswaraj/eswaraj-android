package com.eswaraj.app.eswaraj.middleware;


import android.content.Context;
import android.location.Location;

import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.web.dto.RegisterFacebookAccountRequest;
import com.eswaraj.web.dto.UserDto;
import com.facebook.Session;

import java.io.File;

public interface MiddlewarePostService {

    public void registerDevice(Context context);
    public void saveUserLocation(Context context, UserDto userDto, double lat, double lng);
    public void postComplaint(UserDto userDto, CategoryWithChildCategoryDto categoryDto, Location location, String description, File image);
}
