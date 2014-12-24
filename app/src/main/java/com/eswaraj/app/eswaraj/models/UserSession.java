package com.eswaraj.app.eswaraj.models;

import com.eswaraj.web.dto.UserDto;

/**
 * Created by Ravi on 24/12/2014.
 */
public class UserSession {
    private UserDto user;

    public boolean isUserLocationKnown(){
        if(user.getPerson().getPersonAddress() != null) {
            if(user.getPerson().getPersonAddress().getLongitude() != null) {
                return true;
            }
        }
        return false;
    }
    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

}
