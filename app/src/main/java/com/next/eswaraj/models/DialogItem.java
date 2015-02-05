package com.next.eswaraj.models;


import android.graphics.Bitmap;

import com.eswaraj.web.dto.LocationDto;

public class DialogItem {

    private Long id;
    private Long type;
    private Bitmap icon;
    private String name;
    private String title;
    private Class target;
    private LocationDto locationDto;
    private PoliticalBodyAdminDto politicalBodyAdminDto;

    public LocationDto getLocationDto() {
        return locationDto;
    }

    public void setLocationDto(LocationDto locationDto) {
        this.locationDto = locationDto;
    }

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

    public PoliticalBodyAdminDto getPoliticalBodyAdminDto() {
        return politicalBodyAdminDto;
    }

    public void setPoliticalBodyAdminDto(PoliticalBodyAdminDto politicalBodyAdminDto) {
        this.politicalBodyAdminDto = politicalBodyAdminDto;
    }
}
