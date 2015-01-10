package com.eswaraj.app.eswaraj.adapters;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.models.ComplaintDto;
import com.eswaraj.web.dto.UserDto;
import com.eswaraj.web.dto.CategoryDto;

import java.util.Date;
import java.util.List;


public class ComplaintListAdapter extends ArrayAdapter<ComplaintDto> {

    private Context context;
    private int layoutResourceId;
    private List<ComplaintDto> complaintDtoList;

    public ComplaintListAdapter(Context context, int layoutResourceId, List<ComplaintDto> complaintDtoList) {
        super(context, layoutResourceId, complaintDtoList);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.complaintDtoList = complaintDtoList;
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
            holder.mcStatus = (TextView)row.findViewById(R.id.mcStatus);
            holder.mcIcon = (ImageView)row.findViewById(R.id.mcIcon);

            row.setTag(holder);
        }
        else
        {
            holder = (ComplaintDtoHolder)row.getTag();
        }

        ComplaintDto complaintDto = complaintDtoList.get(position);

        for(CategoryDto categoryDto : complaintDto.getCategories()) {
            if(!categoryDto.isRoot()) {
                holder.mcCategory.setText(categoryDto.getName());
            }
        }

        holder.mcId.setText(complaintDto.getId().toString());
        //holder.mcDate.setText(new Date(complaintDto.getComplaintTime()).toString());
        holder.mcDate.setText(DateUtils.getRelativeTimeSpanString(complaintDto.getComplaintTime(), new Date().getTime(), DateUtils.MINUTE_IN_MILLIS));
        holder.mcStatus.setText(complaintDto.getStatus());
        if(getRootCategoryId(complaintDto) != null) {
            holder.mcIcon.setImageURI(Uri.parse(context.getFilesDir() + "/eSwaraj_" + String.valueOf(getRootCategoryId(complaintDto)) + ".png"));
        }
        return row;
    }

    private Long getRootCategoryId(ComplaintDto complaintDto) {
        for(CategoryDto categoryDto : complaintDto.getCategories()) {
            if(categoryDto.isRoot()) {
                return categoryDto.getId();
            }
        }
        return null;
    }

    public void addComplaint(ComplaintDto newComplaintDto) {
        if(newComplaintDto != null) {
            complaintDtoList.add(newComplaintDto);
        }
    }

    public ComplaintDto removeComplaint(Long id) {
        for(ComplaintDto complaintDto : complaintDtoList) {
            if(complaintDto.getId().equals(id)) {
                complaintDtoList.remove(complaintDto);
                return complaintDto;
            }
        }
        return null;
    }

    public void clearComplaints() {
        complaintDtoList.clear();
    }

    static class ComplaintDtoHolder
    {
        TextView mcId;
        TextView mcCategory;
        TextView mcDate;
        TextView mcStatus;
        ImageView mcIcon;
    }
}
