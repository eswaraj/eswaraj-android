package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;


public class StartAnotherActivityEvent extends BaseEvent {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
