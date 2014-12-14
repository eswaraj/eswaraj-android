package com.eswaraj.app.eswaraj.middleware;


import android.content.Context;

import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.facebook.Session;

import java.util.List;

public interface MiddlewareService extends MiddlewareGetService, MiddlewarePostService{

    //Add these for each method in MiddlewareGetService
    public void loadCategoriesData(Context context, Boolean dontGetFromCache);
    public Boolean isCategoriesDataAvailable(Context context);

    public void loadCategoriesImages(Context context, List<CategoryWithChildCategoryDto> categoriesList, Boolean dontGetFromCache);
    public Boolean isCategoriesImagesAvailable(Context context);

    public void loadUserData(Context context, Session session, String userExternalId, Boolean dontGetFromCache);
    public Boolean isUserDataAvailable(Context context);
}
