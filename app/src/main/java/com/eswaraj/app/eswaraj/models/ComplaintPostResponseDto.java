package com.eswaraj.app.eswaraj.models;

import com.eswaraj.web.dto.BaseDto;
import com.eswaraj.web.dto.ComplaintDto;

import java.util.ArrayList;
import java.util.List;


public class ComplaintPostResponseDto extends BaseDto {

    private List<PoliticalBodyAdminDto> politicalbodyadmin;
    private ComplaintDto complaint;

    public ComplaintPostResponseDto() {
        politicalbodyadmin = new ArrayList<PoliticalBodyAdminDto>();
        complaint = new ComplaintDto();
    }

    public List<PoliticalBodyAdminDto> getPoliticalBodyAdminDtoList() {
        return politicalbodyadmin;
    }

    public void setPoliticalBodyAdminDtoList(List<PoliticalBodyAdminDto> politicalBodyAdminDtoList) {
        this.politicalbodyadmin = politicalBodyAdminDtoList;
    }

    public ComplaintDto getComplaintDto() {
        return complaint;
    }

    public void setComplaintDto(ComplaintDto complaintDto) {
        this.complaint = complaintDto;
    }

    @Override
    public String toString() {
        return "ComplaintPostResponseDto{" +
                "politicalBodyAdminDtoList=" + politicalbodyadmin +
                ", complaintDto=" + complaint +
                '}';
    }
}
