package com.next.eswaraj.datastore;


import android.location.Location;

import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.web.dto.UserDto;

import java.io.File;

public interface DatabaseInterface {

    public void saveComplaint(UserDto userDto, CategoryWithChildCategoryDto amenity, CategoryWithChildCategoryDto template, Location location, String description, File image, Boolean anonymous, String userGoogleLocation);
    public void postOneComplaint();
}
