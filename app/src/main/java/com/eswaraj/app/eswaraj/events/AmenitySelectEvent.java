package com.eswaraj.app.eswaraj.events;

import com.eswaraj.app.eswaraj.base.BaseEvent;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;


public class AmenitySelectEvent extends BaseEvent {

    private CategoryWithChildCategoryDto amenity;

    public CategoryWithChildCategoryDto getAmenity() {
        return amenity;
    }

    public void setAmenity(CategoryWithChildCategoryDto amenity) {
        this.amenity = amenity;
    }
}
