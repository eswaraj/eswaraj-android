package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;


public class LoginStatusEvent extends BaseEvent {

    private Boolean loggedIn;

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
