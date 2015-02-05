package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;
import com.next.eswaraj.models.ComplaintCounter;

import java.util.List;


public class GetLocationComplaintCountersEvent extends BaseEvent {

    private List<ComplaintCounter> complaintCounters;

    public List<ComplaintCounter> getComplaintCounters() {
        return complaintCounters;
    }

    public void setComplaintCounters(List<ComplaintCounter> complaintCounters) {
        this.complaintCounters = complaintCounters;
    }
}
