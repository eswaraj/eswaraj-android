package com.eswaraj.app.eswaraj.adapters;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import java.util.List;

public class AmenityListAdapter extends ArrayAdapter<CategoryWithChildCategoryDto>{

    private Context context;
    private int layoutResourceId;
    private List<CategoryWithChildCategoryDto> categoryList;

    public AmenityListAdapter(Context context, int layoutResourceId, List<CategoryWithChildCategoryDto> categoryList) {
        super(context, layoutResourceId, categoryList);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.categoryList = categoryList;
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
            holder.saIcon = (ImageView)row.findViewById(R.id.saIcon);
            holder.saTitle = (TextView)row.findViewById(R.id.saTitle);

            row.setTag(holder);
        }
        else
        {
            holder = (CategoryDtoHolder)row.getTag();
        }

        CategoryWithChildCategoryDto categoryDto = categoryList.get(position);
        holder.saTitle.setText(categoryDto.getName());
        holder.saIcon.setImageURI(Uri.parse(context.getFilesDir() + "/eSwaraj_" + String.valueOf(categoryDto.getId()) + ".png"));

        return row;
    }

    static class CategoryDtoHolder
    {
        ImageView saIcon;
        TextView saTitle;
    }
}
