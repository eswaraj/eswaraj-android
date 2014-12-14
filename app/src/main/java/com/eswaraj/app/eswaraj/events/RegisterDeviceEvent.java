package com.eswaraj.app.eswaraj.events;

import com.eswaraj.web.dto.UserDto;


public class RegisterDeviceEvent extends BaseEvent {

    private UserDto userDto;

    public RegisterDeviceEvent() {
        super();
        this.userDto = null;
    }

    public UserDto getUserDto() {
        return this.userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }
}
