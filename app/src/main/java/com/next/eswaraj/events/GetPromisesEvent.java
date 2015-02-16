package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;
import com.next.eswaraj.models.PromiseDto;

import java.util.List;


public class GetPromisesEvent extends BaseEvent {

    private List<PromiseDto> promiseDtoList;

    public List<PromiseDto> getPromiseDtoList() {
        return promiseDtoList;
    }

    public void setPromiseDtoList(List<PromiseDto> promiseDtoList) {
        this.promiseDtoList = promiseDtoList;
    }
}
