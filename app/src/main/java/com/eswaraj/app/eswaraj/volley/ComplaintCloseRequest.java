package com.eswaraj.app.eswaraj.volley;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.events.ComplaintClosedEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.models.ComplaintDto;
import com.eswaraj.web.dto.ComplaintStatusChangeByPersonRequestDto;
import com.eswaraj.web.dto.ComplaintStatusChangeByPoliticalAdminRequestDto;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class ComplaintCloseRequest extends BaseClass {

    @Inject
    EventBus eventBus;
    @Inject
    NetworkAccessHelper networkAccessHelper;

    public void processRequest(ComplaintDto complaintDto)
    {
        ComplaintStatusChangeByPersonRequestDto complaintStatusChangeByPersonRequestDto = new ComplaintStatusChangeByPersonRequestDto();
        complaintStatusChangeByPersonRequestDto.setComplaintId(complaintDto.getId());
        complaintStatusChangeByPersonRequestDto.setPersonId(complaintDto.getPersonId());
        complaintStatusChangeByPersonRequestDto.setStatus("Closed");

        GenericPostVolleyRequest request = new GenericPostVolleyRequest(Constants.COMPLAINT_CLOSE_URL, createErrorListener(), createSuccessListener(), complaintStatusChangeByPersonRequestDto);
        networkAccessHelper.submitNetworkRequest("CloseComplaint", request);
    }

    private Response.Listener<String> createSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ComplaintClosedEvent event = new ComplaintClosedEvent();
                event.setSuccess(true);
                eventBus.post(event);
            }
        };
    }


    private Response.ErrorListener createErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ComplaintClosedEvent event = new ComplaintClosedEvent();
                event.setSuccess(false);
                eventBus.post(event);
            }
        };
    }
}
