package com.eswaraj.app.eswaraj.events;


import com.eswaraj.app.eswaraj.base.BaseEvent;

import java.io.File;

public class GetComplaintImageEvent extends BaseEvent {

    private File image;

    public void setImage(File image) {
        this.image = image;
    }

    public File getImage() {

        return image;
    }
}
