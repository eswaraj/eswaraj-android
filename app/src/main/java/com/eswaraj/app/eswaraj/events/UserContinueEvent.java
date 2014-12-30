package com.eswaraj.app.eswaraj.events;

import com.eswaraj.app.eswaraj.base.BaseEvent;

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
