package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;


public class MenuItemSelectedEvent extends BaseEvent {

    private int itemId;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}
