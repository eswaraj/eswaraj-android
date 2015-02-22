package com.next.eswaraj.adapters;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.next.eswaraj.R;
import com.next.eswaraj.models.ComplaintDto;
import com.next.eswaraj.widgets.CustomNetworkImageView;
import com.eswaraj.web.dto.CategoryDto;
import com.squareup.picasso.Picasso;

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
            holder.mcIcon = (CustomNetworkImageView)row.findViewById(R.id.mcIcon);
            holder.mcImage = (CustomNetworkImageView)row.findViewById(R.id.mcImage);
            holder.mcProfilePhoto = (CustomNetworkImageView)row.findViewById(R.id.mcProfilePhoto);
            holder.mcName = (TextView) row.findViewById(R.id.mcSubmitterName);
            holder.mcDescription = (TextView) row.findViewById(R.id.mcDescription);
            holder.mcAddress = (TextView) row.findViewById(R.id.mcAddress);
            holder.mcAmenity = (TextView) row.findViewById(R.id.mcAmenityName);

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
            else {
                holder.mcAmenity.setText(categoryDto.getName());
            }
        }

        holder.mcId.setText(complaintDto.getId().toString());
        holder.mcDate.setText(DateUtils.getRelativeTimeSpanString(complaintDto.getComplaintTime(), new Date().getTime(), DateUtils.MINUTE_IN_MILLIS));
        holder.mcStatus.setText(complaintDto.getStatus());
        if(complaintDto.getCreatedBy() != null) {
            holder.mcName.setText(complaintDto.getCreatedBy().get(0).getName());
            if (complaintDto.getDescription() != null && !complaintDto.getDescription().equals("")) {
                holder.mcDescription.setText(complaintDto.getDescription());
                holder.mcDescription.setVisibility(View.VISIBLE);
            } else {
                holder.mcDescription.setVisibility(View.GONE);
            }
            if (complaintDto.getCreatedBy().get(0).getProfilePhoto() != null && !complaintDto.getCreatedBy().get(0).getProfilePhoto().equals("")) {
                Picasso.with(context).load(complaintDto.getCreatedBy().get(0).getProfilePhoto().replace("http:", "https:")).error(R.drawable.anon_grey).placeholder(R.drawable.anon_grey).into(holder.mcProfilePhoto);
            } else {
                holder.mcProfilePhoto.setImageDrawable(context.getResources().getDrawable(R.drawable.anon_grey));
            }
            if (complaintDto.getImages() != null && complaintDto.getImages().get(0) != null && complaintDto.getImages().get(0).getOrgUrl() != null && !complaintDto.getImages().get(0).getOrgUrl().equals("")) {
                Picasso.with(context).load(complaintDto.getImages().get(0).getOrgUrl()).into(holder.mcImage);
                holder.mcImage.setVisibility(View.VISIBLE);
            } else {
                holder.mcImage.setVisibility(View.GONE);
            }
        }
        if(getRootCategoryId(complaintDto) != null) {
            holder.mcIcon.setImageURI(Uri.parse(context.getFilesDir() + "/eSwaraj_" + String.valueOf(getRootCategoryId(complaintDto)) + ".png"));
        }
        if(complaintDto.getLocationAddress() != null) {
            holder.mcAddress.setText(complaintDto.getLocationAddress());
        }
        else {
            holder.mcAddress.setText("");
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

    public void markComplaintClosed(Long id) {
        for(ComplaintDto complaintDto : complaintDtoList) {
            if(complaintDto.getId().equals(id)) {
                complaintDto.setStatus("Done");
            }
        }
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
        CustomNetworkImageView mcIcon;
        CustomNetworkImageView mcProfilePhoto;
        CustomNetworkImageView mcImage;
        TextView mcName;
        TextView mcDescription;
        TextView mcAddress;
        TextView mcAmenity;
    }
}
