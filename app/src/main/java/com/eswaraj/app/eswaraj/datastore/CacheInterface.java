package com.eswaraj.app.eswaraj.datastore;

import android.content.Context;

import com.eswaraj.app.eswaraj.middleware.MiddlewareGetService;
import com.eswaraj.web.dto.UserDto;

public interface CacheInterface extends MiddlewareGetService {

    public Boolean isUserDataAvailable(Context context);
    public void updateUserData(Context context, String json);

    //Similar methods have to be defined for all methods in MiddlewareGetService interface
    public Boolean isCategoriesDataAvailable(Context context);
    public void updateCategoriesData(Context context, String json);

    public Boolean isCategoriesImagesAvailable(Context context);
    public void updateCategoriesImages(Context context);

    public Boolean isUserComplaintsAvailable(Context context);
    public void updateUserComplaints(Context context, String json);

    public Boolean isComplaintImageAvailable(Context context, String url, Long id);
    public void updateComplaintImage(Context context);
}
