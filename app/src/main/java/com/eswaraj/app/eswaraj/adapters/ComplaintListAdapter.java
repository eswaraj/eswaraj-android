package com.eswaraj.app.eswaraj.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.web.dto.ComplaintDto;

import java.util.Date;
import java.util.List;


public class ComplaintListAdapter extends ArrayAdapter<ComplaintDto> {

    private Context context;
    private int layoutResourceId;
    private List<ComplaintDto> complaintDtoList;
    private List<CategoryWithChildCategoryDto> categoryDtoList;

    public ComplaintListAdapter(Context context, int layoutResourceId, List<ComplaintDto> complaintDtoList, List<CategoryWithChildCategoryDto> categoryDtoList) {
        super(context, layoutResourceId, complaintDtoList);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.complaintDtoList = complaintDtoList;
        this.categoryDtoList = categoryDtoList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ComplaintDtoHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ComplaintDtoHolder();
            holder.mcId = (TextView)row.findViewById(R.id.mcID);
            holder.mcCategory = (TextView)row.findViewById(R.id.mcCategory);
            holder.mcDate = (TextView)row.findViewById(R.id.mcDate);

            row.setTag(holder);
        }
        else
        {
            holder = (ComplaintDtoHolder)row.getTag();
        }

        ComplaintDto complaintDto = complaintDtoList.get(position);

        for(CategoryWithChildCategoryDto root : categoryDtoList) {
            if(root.getChildCategories() != null) {
                for (CategoryWithChildCategoryDto child : root.getChildCategories()) {
                    if (child.getId() == complaintDto.getCategoryId()) {
                        holder.mcCategory.setText(child.getName());
                        break;
                    }
                }
            }
        }

        holder.mcId.setText(complaintDto.getId().toString());
        //TODO: Get new jar which has this field and then uncomment this line
        //holder.mcDate.setText(new Date(complaintDto.getComplaintTime()).toString());
        return row;
    }

    static class ComplaintDtoHolder
    {
        TextView mcId;
        TextView mcCategory;
        TextView mcDate;
    }
}
