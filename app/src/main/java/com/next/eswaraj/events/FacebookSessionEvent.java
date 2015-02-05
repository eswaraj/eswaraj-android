package com.next.eswaraj.events;


import com.next.eswaraj.base.BaseEvent;
import com.facebook.Session;

public class FacebookSessionEvent extends BaseEvent {

    private Session session;
    private Boolean login;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Boolean getLogin() {
        return login;
    }

    public void setLogin(Boolean login) {
        this.login = login;
    }
}
