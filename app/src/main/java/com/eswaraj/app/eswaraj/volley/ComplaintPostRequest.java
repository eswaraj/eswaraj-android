package com.eswaraj.app.eswaraj.volley;



import android.location.Address;
import android.location.Location;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;

import com.eswaraj.app.eswaraj.events.ComplaintReportedEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.models.ComplaintPostResponseDto;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
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

    public void processRequest(UserDto userDto, CategoryWithChildCategoryDto amenity, CategoryWithChildCategoryDto template, Location location, String description, File image, Boolean anonymous, String userGoogleLocation) {
        SaveComplaintRequestDto saveComplaintRequestDto = new SaveComplaintRequestDto();
        saveComplaintRequestDto.setUserExternalid(userDto.getExternalId());
        saveComplaintRequestDto.setCategoryId(template.getId());
        saveComplaintRequestDto.setDescription(description);
        saveComplaintRequestDto.setTitle(description);
        saveComplaintRequestDto.setLattitude(location.getLatitude());
        saveComplaintRequestDto.setLongitude(location.getLongitude());
        saveComplaintRequestDto.setAnonymous(anonymous);
        saveComplaintRequestDto.setGoogleLocationJson(userGoogleLocation);

        ComplaintPostVolleyRequest request = null;
        try {
            request = new ComplaintPostVolleyRequest(createErrorListener(), createSuccessListener(amenity, template, userGoogleLocation, description), saveComplaintRequestDto, image, Constants.POST_COMPLAINT_URL);
            request.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 3));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        this.networkAccessHelper.submitNetworkRequest("PostComplaint", request);

    }

    private Response.Listener<String> createSuccessListener(final CategoryWithChildCategoryDto amenity, final CategoryWithChildCategoryDto template, final String userGoogleLocation, final String description) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Address bestMatch = new Gson().fromJson(userGoogleLocation, Address.class);
                String userLocationString = null;
                if(bestMatch != null) {
                    userLocationString = bestMatch.getAddressLine(1) + ", " + bestMatch.getAddressLine(2);
                }
                ComplaintPostResponseDto complaintPostResponseDto = new Gson().fromJson(response, ComplaintPostResponseDto.class);
                complaintPostResponseDto.setAmenity(amenity);
                complaintPostResponseDto.setTemplate(template);
                complaintPostResponseDto.getComplaintDto().setDescription(description);
                complaintPostResponseDto.getComplaintDto().setLocationString(userLocationString);
                ComplaintReportedEvent event = new ComplaintReportedEvent();
                event.setSuccess(true);
                event.setComplaintPostResponseDto(complaintPostResponseDto);
                eventBus.post(event);
            }
        };
    }


    private Response.ErrorListener createErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ComplaintReportedEvent event = new ComplaintReportedEvent();
                event.setSuccess(false);
                event.setError(error.toString());
                eventBus.post(event);
            }
        };
    }
}
