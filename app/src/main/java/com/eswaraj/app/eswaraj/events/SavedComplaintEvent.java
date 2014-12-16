package com.eswaraj.app.eswaraj.events;


import com.eswaraj.web.dto.ComplaintDto;

public class SavedComplaintEvent extends BaseEvent {

    private ComplaintDto complaintDto;

    public void setComplaintDto(ComplaintDto complaintDto) {
        this.complaintDto = complaintDto;
    }

    public ComplaintDto getComplaintDto() {
        return complaintDto;
    }
}
