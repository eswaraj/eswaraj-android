package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;
import com.next.eswaraj.models.PoliticalBodyAdminDto;

public class GetLeaderEvent extends BaseEvent {

    private PoliticalBodyAdminDto politicalBodyAdminDto;

    public PoliticalBodyAdminDto getPoliticalBodyAdminDto() {
        return politicalBodyAdminDto;
    }

    public void setPoliticalBodyAdminDto(PoliticalBodyAdminDto politicalBodyAdminDto) {
        this.politicalBodyAdminDto = politicalBodyAdminDto;
    }
}
