package com.eswaraj.app.eswaraj.middleware;


import android.content.Context;

public interface MiddlewareService extends MiddlewareGetService, MiddlewarePostService{

    //Add these for each method in MiddlewareGetService
    public void loadCategoriesData(Context context, Boolean dontGetFromCache);
}
