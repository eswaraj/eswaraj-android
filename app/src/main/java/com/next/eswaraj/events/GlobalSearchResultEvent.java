package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;
import com.next.eswaraj.models.GlobalSearchResponseDto;

import java.util.List;


public class GlobalSearchResultEvent extends BaseEvent {

    private List<GlobalSearchResponseDto> globalSearchResponseDtoList;

    public List<GlobalSearchResponseDto> getGlobalSearchResponseDtoList() {
        return globalSearchResponseDtoList;
    }

    public void setGlobalSearchResponseDtoList(List<GlobalSearchResponseDto> globalSearchResponseDtoList) {
        this.globalSearchResponseDtoList = globalSearchResponseDtoList;
    }
}
