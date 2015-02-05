package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;


public class TemplateSelectEvent extends BaseEvent {

    private CategoryWithChildCategoryDto amenity;

    public CategoryWithChildCategoryDto getTemplate() {
        return template;
    }

    public void setTemplate(CategoryWithChildCategoryDto template) {
        this.template = template;
    }

    public CategoryWithChildCategoryDto getAmenity() {
        return amenity;
    }

    public void setAmenity(CategoryWithChildCategoryDto amenity) {
        this.amenity = amenity;
    }

    private CategoryWithChildCategoryDto template;
}
