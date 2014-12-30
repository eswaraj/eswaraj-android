package com.eswaraj.app.eswaraj.models;


import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

public class ComplaintDto extends com.eswaraj.web.dto.ComplaintDto {
    private CategoryWithChildCategoryDto rootCategory;
    private String locationString;

    public String getLocationString() {
        return locationString;
    }

    public void setLocationString(String locationString) {
        this.locationString = locationString;
    }

    public CategoryWithChildCategoryDto getRootCategory() {
        return rootCategory;
    }

    public void setRootCategory(CategoryWithChildCategoryDto rootCategory) {
        this.rootCategory = rootCategory;
    }
}
