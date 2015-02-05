package com.next.eswaraj.util;


import android.content.Context;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseClass;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class GlobalSessionUtil extends BaseClass {

    @Inject
    Context applicationContext;

    private List<CategoryWithChildCategoryDto> categoryDtoList;
    private Map<Long, Integer> colorMap = new HashMap<Long, Integer>();

    public List<CategoryWithChildCategoryDto> getCategoryDtoList() {
        return categoryDtoList;
    }

    public void setCategoryDtoList(List<CategoryWithChildCategoryDto> categoryDtoList) {
        this.categoryDtoList = categoryDtoList;
        int[] colors = applicationContext.getResources().getIntArray(R.array.rainbow);
        int count = 0;
        for(CategoryWithChildCategoryDto categoryDto : categoryDtoList) {
            colorMap.put(categoryDto.getId(), colors[count]);
            count++;
        }
    }

    public Map<Long, Integer> getColorMap() {
        return colorMap;
    }

    public int getColorForCategory(Long id) {
        return colorMap.get(id);
    }
}
