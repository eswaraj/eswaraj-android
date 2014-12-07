package com.eswaraj.app.eswaraj.events;


import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import java.util.List;

public class GetCategoriesDataEvent extends BaseEvent {

    private List<CategoryWithChildCategoryDto> categoryList;

    public GetCategoriesDataEvent() {
        super();
        this.categoryList = null;
    }

    public void setCategoryList(List<CategoryWithChildCategoryDto> categoryList) {
        this.categoryList = categoryList;
    }

    public List<CategoryWithChildCategoryDto> getCategoryList() {
        return this.categoryList;
    }

}
