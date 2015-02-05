package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;

public class UserContinueEvent extends BaseEvent {

    private Boolean loggedIn;
    private Boolean another;

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public Boolean getAnother() {
        return another;
    }

    public void setAnother(Boolean another) {
        this.another = another;
    }
}
