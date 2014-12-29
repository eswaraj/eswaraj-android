package com.eswaraj.app.eswaraj.events;

import com.eswaraj.app.eswaraj.base.BaseEvent;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;


public class TemplateSelectEvent extends BaseEvent {

    CategoryWithChildCategoryDto categoryWithChildCategoryDto;

    public CategoryWithChildCategoryDto getCategoryWithChildCategoryDto() {
        return categoryWithChildCategoryDto;
    }

    public void setCategoryWithChildCategoryDto(CategoryWithChildCategoryDto categoryWithChildCategoryDto) {
        this.categoryWithChildCategoryDto = categoryWithChildCategoryDto;
    }
}
