package com.next.eswaraj.datastore;


import android.location.Location;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.next.eswaraj.base.BaseClass;
import com.next.eswaraj.config.Constants;
import com.next.eswaraj.events.DatabaseComplaintPostEvent;
import com.next.eswaraj.helpers.DatabaseHelper;
import com.next.eswaraj.helpers.NetworkAccessHelper;
import com.next.eswaraj.models.ComplaintRequestDBItem;
import com.next.eswaraj.volley.ComplaintPostVolleyRequest;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.web.dto.SaveComplaintRequestDto;
import com.eswaraj.web.dto.UserDto;
import com.google.gson.Gson;

import java.io.File;
import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class Database extends BaseClass implements DatabaseInterface {

    @Inject
    DatabaseHelper databaseHelper;
    @Inject
    EventBus eventBus;
    @Inject
    NetworkAccessHelper networkAccessHelper;

    private ComplaintRequestDBItem complaintRequestDBItem;

    @Override
    public void postOneComplaint() {
        complaintRequestDBItem = databaseHelper.getOneComplaint();

        if(complaintRequestDBItem != null) {
            SaveComplaintRequestDto saveComplaintRequestDto = new Gson().fromJson(complaintRequestDBItem.getRequest(), SaveComplaintRequestDto.class);
            File image = null;
            if(complaintRequestDBItem.getFile() != null) {
                image = new File(complaintRequestDBItem.getFile());
            }
            ComplaintPostVolleyRequest request = null;
            try {
                request = new ComplaintPostVolleyRequest(createErrorListener(), createSuccessListener(), saveComplaintRequestDto, image, Constants.POST_COMPLAINT_URL);
                request.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 3));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            networkAccessHelper.submitNetworkRequest("PostComplaint", request);
        }
        else {
            databaseHelper.markAllValid();
            DatabaseComplaintPostEvent event = new DatabaseComplaintPostEvent();
            event.setSuccess(true);
            event.setEnd(true);
            eventBus.post(event);
        }
    }

    public void saveComplaint(UserDto userDto, CategoryWithChildCategoryDto amenity, CategoryWithChildCategoryDto template, Location location, String description, File image, Boolean anonymous, String userGoogleLocation) {
        SaveComplaintRequestDto saveComplaintRequestDto = new SaveComplaintRequestDto();
        saveComplaintRequestDto.setUserExternalid(userDto.getExternalId());
        saveComplaintRequestDto.setCategoryId(template.getId());
        saveComplaintRequestDto.setDescription(description);
        saveComplaintRequestDto.setTitle(description);
        saveComplaintRequestDto.setLattitude(location.getLatitude());
        saveComplaintRequestDto.setLongitude(location.getLongitude());
        saveComplaintRequestDto.setAnonymous(anonymous);
        saveComplaintRequestDto.setGoogleLocationJson(userGoogleLocation);

        ComplaintRequestDBItem complaintRequestDBItem = new ComplaintRequestDBItem();
        complaintRequestDBItem.setRequest(new Gson().toJson(saveComplaintRequestDto));
        if(image != null) {
            complaintRequestDBItem.setFile(image.getAbsolutePath());
        }

        databaseHelper.addComplaint(complaintRequestDBItem);
    }

    private Response.Listener<String> createSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                databaseHelper.deleteComplaint(complaintRequestDBItem);
                DatabaseComplaintPostEvent event = new DatabaseComplaintPostEvent();
                event.setSuccess(true);
                event.setEnd(false);
                eventBus.post(event);
            }
        };
    }


    private Response.ErrorListener createErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                databaseHelper.markInvalid(complaintRequestDBItem);
                DatabaseComplaintPostEvent event = new DatabaseComplaintPostEvent();
                event.setSuccess(false);
                eventBus.post(event);
            }
        };
    }
}
