package com.next.eswaraj.events;


import com.next.eswaraj.base.BaseEvent;
import com.next.eswaraj.models.ComplaintDto;

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
