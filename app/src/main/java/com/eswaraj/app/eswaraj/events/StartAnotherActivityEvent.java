package com.eswaraj.app.eswaraj.events;

import com.eswaraj.app.eswaraj.base.BaseEvent;


public class StartAnotherActivityEvent extends BaseEvent {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
