package com.eswaraj.app.eswaraj.middleware;


import android.content.Context;

import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.app.eswaraj.models.ComplaintDto;
import com.eswaraj.web.dto.UserDto;
import com.facebook.Session;

import java.util.List;

public interface MiddlewareGetService {

    public void loadCategoriesData(Context context);
    public void loadCategoriesImages(Context context, List<CategoryWithChildCategoryDto> categoriesList);
    public void loadUserData(Context context, Session session);
    public void loadUserComplaints(Context context, UserDto userDto);
    public void loadComplaintImage(Context context, String url, Long id);
    public void loadProfileImage(Context context, String url, Long id);
    public void loadComments(Context context, ComplaintDto complaintDto, int count);
}
