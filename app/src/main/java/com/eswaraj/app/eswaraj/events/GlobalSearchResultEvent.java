package com.eswaraj.app.eswaraj.events;

import com.eswaraj.app.eswaraj.base.BaseEvent;
import com.eswaraj.app.eswaraj.models.GlobalSearchResponseDto;

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
