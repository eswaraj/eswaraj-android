package com.next.eswaraj.models;


import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

public class ComplaintDto extends com.eswaraj.web.dto.ComplaintDto {
    private CategoryWithChildCategoryDto rootCategory;
    private String locationAddress;

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public CategoryWithChildCategoryDto getRootCategory() {
        return rootCategory;
    }

    public void setRootCategory(CategoryWithChildCategoryDto rootCategory) {
        this.rootCategory = rootCategory;
    }
}
