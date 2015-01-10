package com.eswaraj.app.eswaraj.util;


import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import java.util.List;

public class GlobalSessionUtil extends BaseClass {

    private List<CategoryWithChildCategoryDto> categoryDtoList;

    public List<CategoryWithChildCategoryDto> getCategoryDtoList() {
        return categoryDtoList;
    }

    public void setCategoryDtoList(List<CategoryWithChildCategoryDto> categoryDtoList) {
        this.categoryDtoList = categoryDtoList;
    }
}
