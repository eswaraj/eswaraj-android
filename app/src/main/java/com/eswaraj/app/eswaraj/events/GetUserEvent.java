package com.eswaraj.app.eswaraj.events;


import com.eswaraj.app.eswaraj.base.BaseEvent;
import com.eswaraj.web.dto.UserDto;

public class GetUserEvent extends BaseEvent {

    private UserDto userDto;
    private String token;

    public GetUserEvent() {
        super();
        this.userDto = null;
    }

    public UserDto getUserDto() {
        return this.userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
