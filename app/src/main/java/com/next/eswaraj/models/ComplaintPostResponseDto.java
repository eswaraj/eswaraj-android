package com.next.eswaraj.models;

import com.eswaraj.web.dto.BaseDto;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import java.util.ArrayList;
import java.util.List;


public class ComplaintPostResponseDto extends BaseDto {

    private List<PoliticalBodyAdminDto> politicalbodyadmin;
    private ComplaintDto complaint;
    private CategoryWithChildCategoryDto amenity;
    private CategoryWithChildCategoryDto template;

    public ComplaintPostResponseDto() {
        politicalbodyadmin = new ArrayList<PoliticalBodyAdminDto>();
        complaint = new ComplaintDto();
    }

    public CategoryWithChildCategoryDto getAmenity() {
        return amenity;
    }

    public void setAmenity(CategoryWithChildCategoryDto amenity) {
        this.amenity = amenity;
    }

    public CategoryWithChildCategoryDto getTemplate() {
        return template;
    }

    public void setTemplate(CategoryWithChildCategoryDto template) {
        this.template = template;
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
