package com.next.eswaraj.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.next.eswaraj.R;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import java.util.List;

public class TemplateListAdapter extends ArrayAdapter<CategoryWithChildCategoryDto>{

    private Context context;
    private int layoutResourceId;
    private List<CategoryWithChildCategoryDto> templateList;

    public TemplateListAdapter(Context context, int layoutResourceId, List<CategoryWithChildCategoryDto> templateList) {
        super(context, layoutResourceId, templateList);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.templateList = templateList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CategoryDtoHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new CategoryDtoHolder();
            holder.stTitle = (TextView)row.findViewById(R.id.subcatitem);

            row.setTag(holder);
        }
        else
        {
            holder = (CategoryDtoHolder)row.getTag();
        }

        CategoryWithChildCategoryDto categoryDto = templateList.get(position);
        holder.stTitle.setText(categoryDto.getName());

        return row;
    }

    static class CategoryDtoHolder
    {
        TextView stTitle;
    }
}
