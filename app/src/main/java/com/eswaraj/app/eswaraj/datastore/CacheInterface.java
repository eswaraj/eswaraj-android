package com.eswaraj.app.eswaraj.datastore;

import android.content.Context;

import com.eswaraj.app.eswaraj.middleware.MiddlewareGetService;

public interface CacheInterface extends MiddlewareGetService {

    //Similar methods have to be defined for all methods in MiddlewareGetService interface
    public Boolean isCategoriesDataAvailable(Context context);
    public void updateCategoriesData(Context context, String json);
}
