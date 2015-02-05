package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;
import com.eswaraj.web.dto.LocationDto;

import java.io.Serializable;


public class ShowLocationEvent extends BaseEvent implements Serializable {

    private LocationDto locationDto;

    public LocationDto getLocationDto() {
        return locationDto;
    }

    public void setLocationDto(LocationDto locationDto) {
        this.locationDto = locationDto;
    }

    @Override
    public String toString() {
        return "ShowLocationEvent{" +
                "locationDto=" + locationDto +
                '}';
    }
}
