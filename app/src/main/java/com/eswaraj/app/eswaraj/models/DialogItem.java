package com.eswaraj.app.eswaraj.models;


import android.graphics.Bitmap;

public class DialogItem {

    private Long id;
    private Long type;
    private Bitmap icon;
    private String name;
    private String title;
    private Class target;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Class getTarget() {
        return target;
    }

    public void setTarget(Class target) {
        this.target = target;
    }
}
