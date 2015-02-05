package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;
import com.eswaraj.web.dto.UserDto;


public class ProfileUpdateEvent extends BaseEvent {

    private UserDto userDto;

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }
}
