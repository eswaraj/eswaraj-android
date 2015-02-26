package com.next.eswaraj.events;

import com.eswaraj.web.dto.LocationDto;
import com.next.eswaraj.base.BaseEvent;


public class ShowConstituencyTimelineEvent extends BaseEvent{

    private LocationDto locationDto;

    public LocationDto getLocationDto() {
        return locationDto;
    }

    public void setLocationDto(LocationDto locationDto) {
        this.locationDto = locationDto;
    }
}
