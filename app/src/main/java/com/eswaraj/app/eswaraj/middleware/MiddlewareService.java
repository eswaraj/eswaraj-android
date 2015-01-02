package com.eswaraj.app.eswaraj.middleware;


import android.content.Context;

import com.eswaraj.app.eswaraj.datastore.CacheInterface;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.app.eswaraj.models.ComplaintDto;
import com.eswaraj.web.dto.LocationDto;
import com.eswaraj.web.dto.UserDto;
import com.facebook.Session;

import java.util.List;

public interface MiddlewareService extends MiddlewareGetService, MiddlewarePostService, CacheInterface {

    //Add these for each GET services
    public void loadCategoriesData(Context context, Boolean dontGetFromCache);
    public void loadCategoriesImages(Context context, List<CategoryWithChildCategoryDto> categoriesList, Boolean dontGetFromCache);
    public void loadUserData(Context context, Session session, Boolean dontGetFromCache);
    public void loadUserComplaints(Context context, UserDto userDto, Boolean dontGetFromCache);
    public void loadComplaintImage(Context context, String url, Long id, Boolean dontGetFromCache);
    public void loadProfileImage(Context context, String url, Long id, Boolean dontGetFromCache);
    public void loadHeaderImage(Context context, String url, Long id, Boolean dontGetFromCache);
    public void loadComments(Context context, ComplaintDto complaintDto, int start, int count, Boolean dontGetFromCache);
    public void loadProfileUpdates(Context context, String token, Boolean dontGetFromCache);
    public void loadLocationComplaints(Context context, LocationDto locationDto, int start, int count, Boolean dontGetFromCache);
    public void loadLocationComplaintCounters(Context context, LocationDto locationDto, Boolean dontGetFromCache);
}
