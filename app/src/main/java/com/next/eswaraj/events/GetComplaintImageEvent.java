package com.next.eswaraj.events;


import android.graphics.Bitmap;

import com.next.eswaraj.base.BaseEvent;

public class GetComplaintImageEvent extends BaseEvent {

    private Bitmap bitmap;
    private Long id;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
