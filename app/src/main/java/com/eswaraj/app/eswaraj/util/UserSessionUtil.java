package com.eswaraj.app.eswaraj.util;


import com.eswaraj.web.dto.UserDto;

public class UserSessionUtil {

    private UserDto user;

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public Boolean isUserLocationKnown() {
        if(user.getPerson().getPersonAddress() != null) {
            if(user.getPerson().getPersonAddress().getLongitude() != null) {
                 return true;
            }
        }
        return false;
    }
}
