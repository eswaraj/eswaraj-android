package com.next.eswaraj.middleware;


import android.content.Context;

import com.next.eswaraj.util.UserSessionUtil;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.next.eswaraj.models.ComplaintDto;
import com.eswaraj.web.dto.LocationDto;
import com.eswaraj.web.dto.UserDto;
import com.facebook.Session;

import java.util.List;

public interface MiddlewareGetService {

    public void loadCategoriesData(Context context);
    public void loadCategoriesImages(Context context, List<CategoryWithChildCategoryDto> categoriesList);
    public void loadUserData(Context context, Session session);
    public void loadUserComplaints(Context context, UserDto userDto);
    public void loadComplaintImage(Context context, String url, Long id, Boolean keep);
    public void loadProfileImage(Context context, String url, Long id, Boolean keep);
    public void loadHeaderImage(Context context, String url, Long id, Boolean keep);
    public void loadComments(Context context, ComplaintDto complaintDto, int start, int count);
    public void loadProfileUpdates(Context context, String token);
    public void loadLocationComplaints(Context context, LocationDto locationDto, int start, int count);
    public void loadLocationComplaintCounters(Context context, LocationDto locationDto);
    public void loadSingleComplaint(Context context, Long id);
    public void loadLeaders(Context context, UserSessionUtil userSession);
    public void loadLocation(Context context, Long id);
    public void globalSearch(Context context, String query);
    public void loadLeaderById(Context context, Long id);
    public void loadLeadersForLocation(Context context, LocationDto locationDto);
}
