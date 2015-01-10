package com.eswaraj.app.eswaraj.events;

import com.eswaraj.app.eswaraj.base.BaseEvent;
import com.eswaraj.app.eswaraj.models.ComplaintDto;


public class ComplaintClosedEvent extends BaseEvent {

    ComplaintDto complaintDto;

    public void setComplaintDto(ComplaintDto complaintDto) {
        this.complaintDto = complaintDto;
    }

    public ComplaintDto getComplaintDto() {
        return complaintDto;
    }
}
