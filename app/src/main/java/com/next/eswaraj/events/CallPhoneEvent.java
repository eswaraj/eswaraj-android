package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;


public class CallPhoneEvent extends BaseEvent {
    private String number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
