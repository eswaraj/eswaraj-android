package com.eswaraj.app.eswaraj.events;


import com.eswaraj.app.eswaraj.base.BaseEvent;
import com.eswaraj.app.eswaraj.models.ComplaintDto;

import java.util.List;

public class GetLocationComplaintsEvent extends BaseEvent {

    private List<ComplaintDto> complaintDtoList;

    public void setComplaintDtoList(List<ComplaintDto> complaintDtoList) {
        this.complaintDtoList = complaintDtoList;
    }

    public List<ComplaintDto> getComplaintDtoList() {

        return complaintDtoList;
    }
}
