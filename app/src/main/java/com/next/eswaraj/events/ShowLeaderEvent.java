package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;
import com.next.eswaraj.models.PoliticalBodyAdminDto;

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
