package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;
import com.next.eswaraj.config.TimelineType;
import com.next.eswaraj.models.TimelineDto;

import java.util.List;


public class GetTimelineEvent extends BaseEvent {

    private List<TimelineDto> timelineDtoList;
    private TimelineType type;

    public List<TimelineDto> getTimelineDtoList() {
        return timelineDtoList;
    }

    public void setTimelineDtoList(List<TimelineDto> timelineDtoList) {
        this.timelineDtoList = timelineDtoList;
    }

    public TimelineType getType() {
        return type;
    }

    public void setType(TimelineType type) {
        this.type = type;
    }
}
