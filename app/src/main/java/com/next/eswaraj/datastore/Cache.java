package com.next.eswaraj.datastore;

import android.app.Service;
import android.content.Context;

import com.next.eswaraj.base.BaseClass;
import com.next.eswaraj.config.Constants;
import com.next.eswaraj.config.ImageType;
import com.next.eswaraj.config.PreferenceConstants;
import com.next.eswaraj.events.GetCategoriesDataEvent;
import com.next.eswaraj.events.GetCategoriesImagesEvent;
import com.next.eswaraj.events.GetComplaintImageEvent;
import com.next.eswaraj.events.GetHeaderImageEvent;
import com.next.eswaraj.events.GetLeadersEvent;
import com.next.eswaraj.events.GetProfileImageEvent;
import com.next.eswaraj.events.GetUserComplaintsEvent;
import com.next.eswaraj.events.GetUserEvent;
import com.next.eswaraj.helpers.SharedPreferencesHelper;
import com.next.eswaraj.models.ComplaintDto;
import com.next.eswaraj.models.PoliticalBodyAdminDto;
import com.next.eswaraj.util.UserSessionUtil;
import com.eswaraj.web.dto.LocationDto;
import com.eswaraj.web.dto.UserDto;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.facebook.Session;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class Cache extends BaseClass implements CacheInterface {

    @Inject
    SharedPreferencesHelper sharedPreferencesHelper;
    @Inject
    EventBus eventBus;
    @Inject
    StorageCache storageCache;

    public void loadUserData(Context context, Session session) {
        JsonDeserializer<Date> deser = new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return json == null ? null : new Date(json.getAsLong());
            }
        };
        JsonSerializer<Date> ser = new JsonSerializer<Date>() {

            @Override
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                return src == null ? null : new JsonPrimitive(src.getTime());
            }
        };
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, ser).registerTypeAdapter(Date.class, deser).create();
        String json = sharedPreferencesHelper.getString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_DATA, null);
        try {
            UserDto userDto = gson.fromJson(json, UserDto.class);
            GetUserEvent event = new GetUserEvent();
            event.setSuccess(true);
            event.setUserDto(userDto);
            if(session != null) {
                event.setToken(session.getAccessToken());
            }
            event.setDownloadProfilePhoto(false);
            event.setDataUpdateNeeded(true);
            eventBus.post(event);
        } catch (JsonParseException e) {
            //This should never happen since json would only be stored in server if de-serialization was successful in Server class
            GetUserEvent event = new GetUserEvent();
            event.setSuccess(false);
            event.setError("Invalid json");
            eventBus.post(event);
        }
    }

    public void loadUserData(Service context, Session session) {
        JsonDeserializer<Date> deser = new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return json == null ? null : new Date(json.getAsLong());
            }
        };
        JsonSerializer<Date> ser = new JsonSerializer<Date>() {

            @Override
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                return src == null ? null : new JsonPrimitive(src.getTime());
            }
        };
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, ser).registerTypeAdapter(Date.class, deser).create();
        String json = sharedPreferencesHelper.getString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_DATA, null);
        try {
            UserDto userDto = gson.fromJson(json, UserDto.class);
            GetUserEvent event = new GetUserEvent();
            event.setSuccess(true);
            event.setUserDto(userDto);
            if(session != null) {
                event.setToken(session.getAccessToken());
            }
            event.setDownloadProfilePhoto(false);
            event.setDataUpdateNeeded(true);
            eventBus.post(event);
        } catch (JsonParseException e) {
            //This should never happen since json would only be stored in server if de-serialization was successful in Server class
            GetUserEvent event = new GetUserEvent();
            event.setSuccess(false);
            event.setError("Invalid json");
            eventBus.post(event);
        }
    }

    @Override
    public void updateUserData(Context context, String json) {
        if(json != null) {
            sharedPreferencesHelper.putString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_DATA, json);
            sharedPreferencesHelper.putLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_DATA_DOWNLOAD_TIME_IN_MS, new Date().getTime());
            sharedPreferencesHelper.putBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_DATA_AVAILABLE, true);
        }
        else {
            sharedPreferencesHelper.putBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_DATA_AVAILABLE, false);
            sharedPreferencesHelper.putString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_DATA, json);
        }
    }

    public Boolean isUserDataAvailable(Context context) {
        if(sharedPreferencesHelper.getBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_DATA_AVAILABLE, false)) {
            return true;
        }
        return false;
    }

    public void setUserDataStale(Context context) {
        sharedPreferencesHelper.putBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_DATA_UPDATE_NEEDED, true);
    }

    public Boolean isUserDataStale(Context context) {
        return sharedPreferencesHelper.getBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_DATA_UPDATE_NEEDED, false);
    }

    public Boolean isCategoriesDataAvailable(Context context) {
        if(sharedPreferencesHelper.getBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_DATA_AVAILABLE, false)) {
            if((new Date().getTime() - sharedPreferencesHelper.getLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_DATA_DOWNLOAD_TIME_IN_MS, 0L)) < Constants.SERVER_DATA_UPDATE_INTERVAL_IN_MS) {
                return true;
            }
        }
        return false;
    }


    public void loadCategoriesData(Context context) {
        Gson gson = new Gson();
        String json = sharedPreferencesHelper.getString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_DATA, null);
        try {
            List<CategoryWithChildCategoryDto> categoryDtoList;
            GetCategoriesDataEvent getCategoriesDataEvent = new GetCategoriesDataEvent();
            categoryDtoList = gson.fromJson(json, new TypeToken<ArrayList<CategoryWithChildCategoryDto>>(){}.getType());
            getCategoriesDataEvent.setSuccess(true);
            getCategoriesDataEvent.setCategoryList(categoryDtoList);
            eventBus.post(getCategoriesDataEvent);
        } catch (JsonParseException e) {
            //This should never happen since json would only be stored in server if de-serialization was successful in Server class
            GetCategoriesDataEvent getCategoriesDataEvent = new GetCategoriesDataEvent();
            getCategoriesDataEvent.setError("Invalid json");
            eventBus.post(getCategoriesDataEvent);
        }
    }

    public void loadCategoriesData(Service context) {
        Gson gson = new Gson();
        String json = sharedPreferencesHelper.getString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_DATA, null);
        try {
            List<CategoryWithChildCategoryDto> categoryDtoList;
            GetCategoriesDataEvent getCategoriesDataEvent = new GetCategoriesDataEvent();
            categoryDtoList = gson.fromJson(json, new TypeToken<ArrayList<CategoryWithChildCategoryDto>>(){}.getType());
            getCategoriesDataEvent.setSuccess(true);
            getCategoriesDataEvent.setCategoryList(categoryDtoList);
            eventBus.post(getCategoriesDataEvent);
        } catch (JsonParseException e) {
            //This should never happen since json would only be stored in server if de-serialization was successful in Server class
            GetCategoriesDataEvent getCategoriesDataEvent = new GetCategoriesDataEvent();
            getCategoriesDataEvent.setError("Invalid json");
            eventBus.post(getCategoriesDataEvent);
        }
    }

    @Override
    public void updateCategoriesData(Context context, String json) {
        sharedPreferencesHelper.putString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_DATA, json);
        sharedPreferencesHelper.putLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_DATA_DOWNLOAD_TIME_IN_MS, new Date().getTime());
        sharedPreferencesHelper.putBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_DATA_AVAILABLE, true);
    }

    @Override
    public void loadCategoriesImages(Context context, List<CategoryWithChildCategoryDto> categoriesList) {
        //No data needs to be put here. The image file names will be inferred from the categoryId. Just put success=True in event object
        GetCategoriesImagesEvent getCategoriesImagesEvent = new GetCategoriesImagesEvent();
        getCategoriesImagesEvent.setSuccess(true);
        eventBus.post(getCategoriesImagesEvent);
    }

    @Override
    public Boolean isCategoriesImagesAvailable(Context context) {
        if(sharedPreferencesHelper.getBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_IMAGES_AVAILABLE, false)) {
            if((new Date().getTime() - sharedPreferencesHelper.getLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_IMAGES_DOWNLOAD_TIME_IN_MS, 0L)) < Constants.SERVER_DATA_UPDATE_INTERVAL_IN_MS) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateCategoriesImages(Context context, Boolean imageDownloadLaunchedBefore, Boolean success) {
        sharedPreferencesHelper.putBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_IMAGES_AVAILABLE, success);
        sharedPreferencesHelper.putBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_IMAGES_DOWNLOAD_LAUNCHED_BEFORE, imageDownloadLaunchedBefore);
        sharedPreferencesHelper.putLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_IMAGES_DOWNLOAD_TIME_IN_MS, new Date().getTime());
    }

    @Override
    public Boolean wasImageDownloadLaunchedBefore(Context context) {
        return sharedPreferencesHelper.getBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_IMAGES_DOWNLOAD_LAUNCHED_BEFORE, false);
    }

    @Override
    public Boolean isUserComplaintsAvailable(Context context) {
        if(sharedPreferencesHelper.getBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_COMPLAINTS_AVAILABLE, false)) {
            if((new Date().getTime() - sharedPreferencesHelper.getLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_COMPLAINTS_DOWNLOAD_TIME_IN_MS, 0L)) < Constants.SERVER_DATA_UPDATE_INTERVAL_IN_MS) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateUserComplaints(Context context, String json) {
        sharedPreferencesHelper.putString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_COMPLAINTS, json);
        sharedPreferencesHelper.putBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_COMPLAINTS_AVAILABLE, true);
        sharedPreferencesHelper.putLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_COMPLAINTS_DOWNLOAD_TIME_IN_MS, new Date().getTime());
    }

    @Override
    public void loadUserComplaints(Context context, UserDto userDto) {
        Gson gson = new Gson();
        String json = sharedPreferencesHelper.getString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_COMPLAINTS, null);
        try {
            List<ComplaintDto> complaintDtoList;
            GetUserComplaintsEvent event = new GetUserComplaintsEvent();
            complaintDtoList = gson.fromJson(json, new TypeToken<List<ComplaintDto>>(){}.getType());
            event.setSuccess(true);
            event.setComplaintDtoList(complaintDtoList);
            eventBus.post(event);
        } catch (JsonParseException e) {
            //This should never happen since json would only be stored in server if de-serialization was successful in Server class
            GetUserComplaintsEvent event = new GetUserComplaintsEvent();
            event.setError("Invalid json");
            eventBus.post(event);
        }
    }

    @Override
    public Boolean isComplaintImageAvailable(Context context, String url, Long id, Boolean keep) {
        return storageCache.isBitmapAvailable(context, id, ImageType.COMPLAINT, keep);
    }

    @Override
    public void updateComplaintImage(Context context) {
        //Nothing to do here. Keeping the method for consistency
    }

    @Override
    public void loadComplaintImage(Context context, String url, Long id, Boolean keep) {
        GetComplaintImageEvent event = new GetComplaintImageEvent();
        event.setSuccess(true);
        event.setBitmap(storageCache.getBitmap(id, context, ImageType.COMPLAINT, keep));
        event.setId(id);
        eventBus.post(event);
    }

    @Override
    public Boolean isCommentsAvailable(Context context, ComplaintDto complaintDto, int start, int count) {
        return false;
    }

    @Override
    public void updateComments(Context context, String json, ComplaintDto complaintDto, int start, int count) {
        //Nothing to do here right now. Might update later
    }

    @Override
    public void loadComments(Context context, ComplaintDto complaintDto, int start, int count) {
        assert false;
    }

    @Override
    public Boolean isProfileImageAvailable(Context context, String url, Long id, Boolean keep) {
        return storageCache.isBitmapAvailable(context, id, ImageType.PROFILE, keep);
    }

    @Override
    public void updateProfileImage(Context context) {
        //Nothing to do here
    }

    @Override
    public void loadProfileImage(Context context, String url, Long id, Boolean keep) {
        GetProfileImageEvent event = new GetProfileImageEvent();
        event.setSuccess(true);
        event.setBitmap(storageCache.getBitmap(id, context, ImageType.PROFILE, keep));
        event.setId(id);
        eventBus.post(event);
    }

    @Override
    public Boolean isHeaderImageAvailable(Context context, String url, Long id, Boolean keep) {
        return storageCache.isBitmapAvailable(context, id, ImageType.HEADER, keep);
    }

    @Override
    public void updateHeaderImage(Context context) {
        //Nothing to do here
    }

    @Override
    public void loadHeaderImage(Context context, String url, Long id, Boolean keep) {
        GetHeaderImageEvent event = new GetHeaderImageEvent();
        event.setSuccess(true);
        event.setBitmap(storageCache.getBitmap(id, context, ImageType.HEADER, keep));
        event.setId(id);
        eventBus.post(event);
    }

    @Override
    public Boolean isProfileUpdateAvailable(Context context) {
        return false;
    }

    @Override
    public void updateProfileUpdate(Context context, String token) {
        assert false;
    }

    @Override
    public void loadProfileUpdates(Context context, String token) {
        assert false;
    }

    @Override
    public Boolean isLocationComplaintsAvailable(Context context) {
        return false;
    }

    @Override
    public void updateLocationComplaints(Context context, LocationDto locationDto, int start, int count, String json) {
        //Nothing to do here
    }

    @Override
    public void loadLocationComplaints(Context context, LocationDto locationDto, int start, int count) {
        assert false;
    }

    @Override
    public Boolean isLocationComplaintCountersAvailable(Context context) {
        return false;
    }

    @Override
    public void updateLocationComplaintCounters(Context context, LocationDto locationDto, String json) {
        //Nothing to do here
    }

    @Override
    public void loadLocationComplaintCounters(Context context, LocationDto locationDto) {
        assert false;
    }

    @Override
    public Boolean isSingleComplaintAvailable(Context context, Long id) {
        return false;
    }

    @Override
    public void updateSingleComplaint(Context context, Long id, String json) {
        //Nothing to do here
    }

    @Override
    public void loadSingleComplaint(Context context, Long id) {
        assert false;
    }

    @Override
    public Boolean isLeadersAvailable(Context context) {
        if(sharedPreferencesHelper.getBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.LEADERS_AVAILABLE, false)) {
            if((new Date().getTime() - sharedPreferencesHelper.getLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.LEADERS_DOWNLOAD_TIME_IN_MS, 0L)) < Constants.SERVER_DATA_UPDATE_INTERVAL_IN_MS) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateLeaders(Context context, String json) {
        if(json == null) {
            sharedPreferencesHelper.putBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.LEADERS_AVAILABLE, false);
        }
        else {
            sharedPreferencesHelper.putString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.LEADERS, json);
            sharedPreferencesHelper.putLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.LEADERS_DOWNLOAD_TIME_IN_MS, new Date().getTime());
            sharedPreferencesHelper.putBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.LEADERS_AVAILABLE, true);
        }
    }

    @Override
    public void loadLeaders(Context context, UserSessionUtil userSession) {
        JsonDeserializer<Date> deser = new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return json == null ? null : new Date(json.getAsLong());
            }
        };
        JsonSerializer<Date> ser = new JsonSerializer<Date>() {

            @Override
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                return src == null ? null : new JsonPrimitive(src.getTime());
            }
        };
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, ser).registerTypeAdapter(Date.class, deser).create();
        String json = sharedPreferencesHelper.getString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.LEADERS, null);
        try {
            List<PoliticalBodyAdminDto> politicalBodyAdminDtos;
            politicalBodyAdminDtos = gson.fromJson(json, new TypeToken<List<PoliticalBodyAdminDto>>(){}.getType());
            GetLeadersEvent event = new GetLeadersEvent();
            event.setSuccess(true);
            event.setLoadProfilePhotos(false);
            event.setPoliticalBodyAdminDtos(politicalBodyAdminDtos);
            eventBus.post(event);
        } catch (JsonParseException e) {
            //This should never happen since json would only be stored in server if de-serialization was successful in Server class
            GetLeadersEvent event = new GetLeadersEvent();
            event.setSuccess(false);
            event.setError("Invalid json");
            eventBus.post(event);
        }
    }

    @Override
    public Boolean isLocationAvailable(Context context, Long id) {
        return false;
    }

    @Override
    public void updateLocation(Context context, Long id, String json) {
        //No caching for now
    }

    @Override
    public void loadLocation(Context context, Long id) {
        assert false;
    }

    @Override
    public Boolean isGlobalSearchAvailable(Context context, String query) {
        return false;
    }

    @Override
    public void updateGlobalSearch(Context context, String query, String json) {
        //Nothing to do here
    }

    @Override
    public void globalSearch(Context context, String query) {
        assert false;
    }

    @Override
    public Boolean isLeaderAvailable(Context context, Long id) {
        return false;
    }

    @Override
    public void updateLeader(Context context, Long id, String json) {
        //Nothing to do here
    }

    @Override
    public void loadLeaderById(Context context, Long id) {
        assert false;
    }

    @Override
    public Boolean isLeadersForLocationAvailable(Context context, LocationDto locationDto) {
        return false;
    }

    @Override
    public void updateLeadersForLocation(Context context, LocationDto locationDto, String json) {
        //Nothing to do here
    }

    @Override
    public void loadLeadersForLocation(Context context, LocationDto locationDto) {
        assert false;
    }
}
