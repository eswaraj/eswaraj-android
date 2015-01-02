package com.eswaraj.app.eswaraj.events;

import com.eswaraj.app.eswaraj.base.BaseEvent;
import com.eswaraj.app.eswaraj.models.ComplaintCounter;

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
