package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;
import com.next.eswaraj.models.ComplaintDto;


public class MarkerClickEvent extends BaseEvent {

    private ComplaintDto complaintDto;

    public ComplaintDto getComplaintDto() {
        return complaintDto;
    }

    public void setComplaintDto(ComplaintDto complaintDto) {
        this.complaintDto = complaintDto;
    }
}
