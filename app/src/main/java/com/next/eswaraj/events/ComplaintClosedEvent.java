package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;
import com.next.eswaraj.models.ComplaintDto;


public class ComplaintClosedEvent extends BaseEvent {

    ComplaintDto complaintDto;

    public void setComplaintDto(ComplaintDto complaintDto) {
        this.complaintDto = complaintDto;
    }

    public ComplaintDto getComplaintDto() {
        return complaintDto;
    }
}
