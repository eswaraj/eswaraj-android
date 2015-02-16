package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;
import com.next.eswaraj.models.PromiseDto;

public class PromiseSelectedEvent extends BaseEvent {
    private PromiseDto promiseDto;

    public PromiseDto getPromiseDto() {
        return promiseDto;
    }

    public void setPromiseDto(PromiseDto promiseDto) {
        this.promiseDto = promiseDto;
    }
}
