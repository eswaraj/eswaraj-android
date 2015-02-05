package com.next.eswaraj.events;


import com.next.eswaraj.base.BaseEvent;
import com.next.eswaraj.models.ComplaintPostResponseDto;

public class ComplaintReportedEvent extends BaseEvent {

    private ComplaintPostResponseDto complaintPostResponseDto;

    public ComplaintPostResponseDto getComplaintPostResponseDto() {
        return complaintPostResponseDto;
    }

    public void setComplaintPostResponseDto(ComplaintPostResponseDto complaintPostResponseDto) {
        this.complaintPostResponseDto = complaintPostResponseDto;
    }
}
