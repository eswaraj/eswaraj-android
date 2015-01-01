package com.eswaraj.app.eswaraj.events;

import android.graphics.Bitmap;

import com.eswaraj.app.eswaraj.base.BaseEvent;


public class GetHeaderImageEvent extends BaseEvent {

    private Bitmap bitmap;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
