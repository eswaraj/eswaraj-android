package com.eswaraj.app.eswaraj.volley;



import android.location.Location;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;

import com.eswaraj.app.eswaraj.events.SavedComplaintEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.web.dto.ComplaintDto;
import com.eswaraj.web.dto.SaveComplaintRequestDto;
import com.eswaraj.web.dto.UserDto;
import com.google.gson.Gson;


import java.io.File;
import java.io.UnsupportedEncodingException;


import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class ComplaintPostRequest extends BaseClass {

    @Inject
    EventBus eventBus;
    @Inject
    NetworkAccessHelper networkAccessHelper;

    public void processRequest(UserDto userDto, CategoryWithChildCategoryDto categoryDto, Location location, String description, File image, Boolean anonymous, String userGoogleLocation) {
        Log.e("processrequest: ", userDto.toString());
        SaveComplaintRequestDto saveComplaintRequestDto = new SaveComplaintRequestDto();
        saveComplaintRequestDto.setUserExternalid(userDto.getExternalId());
        saveComplaintRequestDto.setCategoryId(categoryDto.getId());
        saveComplaintRequestDto.setDescription(description);
        saveComplaintRequestDto.setTitle(description);
        saveComplaintRequestDto.setLattitude(location.getLatitude());
        saveComplaintRequestDto.setLongitude(location.getLongitude());
        saveComplaintRequestDto.setAnonymous(anonymous);
        saveComplaintRequestDto.setGoogleLocationJson(userGoogleLocation);

        ComplaintPostVolleyRequest request = null;
        try {
            request = new ComplaintPostVolleyRequest(createErrorListener(), createSuccessListener(), saveComplaintRequestDto, image, Constants.POST_COMPLAINT_URL);
            request.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 3));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        this.networkAccessHelper.submitNetworkRequest("PostComplaint", request);

    }

    private Response.Listener<String> createSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ComplaintDto complaintDto = new Gson().fromJson(response, ComplaintDto.class);
                SavedComplaintEvent event = new SavedComplaintEvent();
                event.setSuccess(true);
                event.setComplaintDto(complaintDto);
                eventBus.post(event);
            }
        };
    }


    private Response.ErrorListener createErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SavedComplaintEvent event = new SavedComplaintEvent();
                event.setSuccess(false);
                event.setError(error.toString());
                eventBus.post(event);
            }
        };
    }
}
