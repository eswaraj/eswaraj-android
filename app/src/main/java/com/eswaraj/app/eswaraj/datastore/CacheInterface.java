package com.eswaraj.app.eswaraj.datastore;

import android.content.Context;

import com.eswaraj.app.eswaraj.middleware.MiddlewareGetService;
import com.eswaraj.app.eswaraj.models.ComplaintDto;
import com.eswaraj.web.dto.LocationDto;
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

    public Boolean isProfileImageAvailable(Context context, String url, Long id);
    public void updateProfileImage(Context context);

    public Boolean isHeaderImageAvailable(Context context, String url, Long id);
    public void updateHeaderImage(Context context);

    public Boolean isCommentsAvailable(Context context, ComplaintDto complaintDto, int start, int count);
    public void updateComments(Context context, String json, ComplaintDto complaintDto, int start, int count);

    public Boolean isProfileUpdateAvailable(Context context);
    public void updateProfileUpdate(Context context, String token);

    public Boolean isLocationComplaintsAvailable(Context context);
    public void updateLocationComplaints(Context context, LocationDto locationDto, int start, int count, String json);

    public Boolean isLocationComplaintCountersAvailable(Context context);
    public void updateLocationComplaintCounters(Context context, LocationDto locationDto, String json);

    public Boolean isSingleComplaintAvailable(Context context, Long id);
    public void updateSingleComplaint(Context context, Long id, String json);
}
