package com.next.eswaraj.events;


import com.next.eswaraj.base.BaseEvent;
import com.eswaraj.web.dto.UserDto;

public class GetUserEvent extends BaseEvent {

    private UserDto userDto;
    private String token;
    private Boolean downloadProfilePhoto;
    private Boolean dataUpdateNeeded;

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

    public Boolean getDownloadProfilePhoto() {
        return downloadProfilePhoto;
    }

    public void setDownloadProfilePhoto(Boolean downloadProfilePhoto) {
        this.downloadProfilePhoto = downloadProfilePhoto;
    }

    public Boolean getDataUpdateNeeded() {
        return dataUpdateNeeded;
    }

    public void setDataUpdateNeeded(Boolean dataUpdateNeeded) {
        this.dataUpdateNeeded = dataUpdateNeeded;
    }
}
