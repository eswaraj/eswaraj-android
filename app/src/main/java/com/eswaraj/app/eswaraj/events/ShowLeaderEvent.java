package com.eswaraj.app.eswaraj.events;

import com.eswaraj.app.eswaraj.base.BaseEvent;
import com.eswaraj.app.eswaraj.models.PoliticalBodyAdminDto;

import java.io.Serializable;


public class ShowLeaderEvent extends BaseEvent implements Serializable{

    private PoliticalBodyAdminDto politicalBodyAdminDto;

    public PoliticalBodyAdminDto getPoliticalBodyAdminDto() {
        return politicalBodyAdminDto;
    }

    public void setPoliticalBodyAdminDto(PoliticalBodyAdminDto politicalBodyAdminDto) {
        this.politicalBodyAdminDto = politicalBodyAdminDto;
    }

    @Override
    public String toString() {
        return "ShowLeaderEvent{" +
                "politicalBodyAdminDto=" + politicalBodyAdminDto +
                '}';
    }
}
