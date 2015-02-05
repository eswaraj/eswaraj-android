package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;


public class BannerClickEvent extends BaseEvent {

    String video;

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
