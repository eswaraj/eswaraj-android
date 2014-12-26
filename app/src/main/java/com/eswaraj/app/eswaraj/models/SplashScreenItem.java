package com.eswaraj.app.eswaraj.models;


import android.graphics.drawable.Drawable;

public class SplashScreenItem {

    private Drawable image;
    private String text;
    private String heading;


    public SplashScreenItem(Drawable image, String heading, String text) {
        this.image = image;
        this.heading = heading;

        this.text = text;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
