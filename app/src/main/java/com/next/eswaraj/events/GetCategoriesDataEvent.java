package com.next.eswaraj.events;


import com.next.eswaraj.base.BaseEvent;
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
