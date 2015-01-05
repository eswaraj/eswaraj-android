package com.eswaraj.app.eswaraj.events;

import com.eswaraj.app.eswaraj.base.BaseEvent;
import com.eswaraj.app.eswaraj.models.PoliticalBodyAdminDto;

import java.util.List;


public class GetLeadersEvent extends BaseEvent {

    private Boolean loadProfilePhotos;
    private List<PoliticalBodyAdminDto> politicalBodyAdminDtos;

    public Boolean getLoadProfilePhotos() {
        return loadProfilePhotos;
    }

    public void setLoadProfilePhotos(Boolean loadProfilePhotos) {
        this.loadProfilePhotos = loadProfilePhotos;
    }

    public List<PoliticalBodyAdminDto> getPoliticalBodyAdminDtos() {
        return politicalBodyAdminDtos;
    }

    public void setPoliticalBodyAdminDtos(List<PoliticalBodyAdminDto> politicalBodyAdminDtos) {
        this.politicalBodyAdminDtos = politicalBodyAdminDtos;
    }

    @Override
    public String toString() {
        return "GetLeadersEvent{" +
                "loadProfilePhotos=" + loadProfilePhotos +
                ", politicalBodyAdminDtos=" + politicalBodyAdminDtos +
                '}';
    }
}
