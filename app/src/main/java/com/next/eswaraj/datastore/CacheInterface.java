package com.next.eswaraj.datastore;

import android.content.Context;

import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.next.eswaraj.config.TimelineType;
import com.next.eswaraj.middleware.MiddlewareGetService;
import com.next.eswaraj.models.ComplaintDto;
import com.eswaraj.web.dto.LocationDto;

import java.util.List;

public interface CacheInterface extends MiddlewareGetService {

    public Boolean isUserDataAvailable(Context context);
    public void updateUserData(Context context, String json);
    public void setUserDataStale(Context context);
    public Boolean isUserDataStale(Context context);

    //Similar methods have to be defined for all methods in MiddlewareGetService interface
    public Boolean isCategoriesDataAvailable(Context context);
    public void updateCategoriesData(Context context, String json);
    public List<CategoryWithChildCategoryDto> getCategoriesDataFromCache(Context context);

    public Boolean isCategoriesImagesAvailable(Context context);
    public Boolean wasImageDownloadLaunchedBefore(Context context);
    public void updateCategoriesImages(Context context, Boolean imageDownloadLaunchedBefore, Boolean success);

    public Boolean isUserComplaintsAvailable(Context context, int start, int count);
    public void updateUserComplaints(Context context, int start, int count, String json);

    public Boolean isComplaintImageAvailable(Context context, String url, Long id, Boolean keep);
    public void updateComplaintImage(Context context);

    public Boolean isProfileImageAvailable(Context context, String url, Long id, Boolean keep);
    public void updateProfileImage(Context context);

    public Boolean isHeaderImageAvailable(Context context, String url, Long id, Boolean keep);
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

    public Boolean isLeadersAvailable(Context context);
    public void updateLeaders(Context context, String json);

    public Boolean isLocationAvailable(Context context, Long id);
    public void updateLocation(Context context, Long id, String json);

    public Boolean isGlobalSearchAvailable(Context context, String query);
    public void updateGlobalSearch(Context context, String query, String json);

    public Boolean isLeaderAvailable(Context context, Long id);
    public void updateLeader(Context context, Long id, String json);

    public Boolean isLeadersForLocationAvailable(Context context, LocationDto locationDto);
    public void updateLeadersForLocation(Context context, LocationDto locationDto, String json);

    public Boolean isPromisesByLeaderAvailable(Context context, Long id);
    public void updatePromisesByLeader(Context context, Long id, String json);

    public Boolean isTimelineAvailable(Context context, TimelineType type, Long id, int start, int count);
    public void updateTimeline(Context context, TimelineType type, Long id, int start, int count, String json);
}
