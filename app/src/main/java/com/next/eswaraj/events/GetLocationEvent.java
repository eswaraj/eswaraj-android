package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;
import com.eswaraj.web.dto.LocationDto;


public class GetLocationEvent extends BaseEvent {

    private LocationDto locationDto;

    public LocationDto getLocationDto() {
        return locationDto;
    }

    public void setLocationDto(LocationDto locationDto) {
        this.locationDto = locationDto;
    }
}
