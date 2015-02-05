package com.next.eswaraj.models;


import com.eswaraj.web.dto.CategoryDto;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import java.io.Serializable;

public class ComplaintSavedResponseDto implements Serializable {

    CategoryWithChildCategoryDto amenity;
    CategoryDto template;
    String description;
    String locationString;
    Boolean anonymous;

    public CategoryWithChildCategoryDto getAmenity() {
        return amenity;
    }

    public void setAmenity(CategoryWithChildCategoryDto amenity) {
        this.amenity = amenity;
    }

    public CategoryDto getTemplate() {
        return template;
    }

    public void setTemplate(CategoryDto template) {
        this.template = template;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocationString() {
        return locationString;
    }

    public void setLocationString(String locationString) {
        this.locationString = locationString;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }

    @Override
    public String toString() {
        return "ComplaintSavedResponseDto{" +
                "amenity=" + amenity +
                ", template=" + template +
                ", description='" + description + '\'' +
                ", locationString='" + locationString + '\'' +
                ", anonymous=" + anonymous +
                '}';
    }
}
