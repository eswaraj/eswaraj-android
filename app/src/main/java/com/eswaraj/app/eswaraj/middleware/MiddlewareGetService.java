package com.eswaraj.app.eswaraj.middleware;


import android.content.Context;

import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.facebook.Session;

import java.util.List;

public interface MiddlewareGetService {

    public void loadCategoriesData(Context context);
    public void loadCategoriesImages(Context context, List<CategoryWithChildCategoryDto> categoriesList);
    public void loadUserData(Context context, Session session);
}
