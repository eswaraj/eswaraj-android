package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;
import com.next.eswaraj.models.PoliticalBodyAdminDto;

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
