package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;


public class DatabaseComplaintPostEvent extends BaseEvent {

    private Boolean end;

    public Boolean getEnd() {
        return end;
    }

    public void setEnd(Boolean end) {
        this.end = end;
    }
}
