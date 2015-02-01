package com.eswaraj.app.eswaraj.adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.models.ComplaintFilter;

import java.util.List;

public class FilterListAdapter extends ArrayAdapter<ComplaintFilter> {

    private Context context;
    private int layoutResourceId;
    private List<ComplaintFilter> filterList;


    public FilterListAdapter(Context context, int layoutResourceId, List<ComplaintFilter> filterList) {
        super(context, layoutResourceId, filterList);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.filterList = filterList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        FilterDtoHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new FilterDtoHolder();
            holder.cfTitle = (TextView)row.findViewById(R.id.cfTitle);

            row.setTag(holder);
        }
        else
        {
            holder = (FilterDtoHolder)row.getTag();
        }

        ComplaintFilter complaintFilter = filterList.get(position);
        holder.cfTitle.setText(complaintFilter.getDisplayText());
        holder.cfTitle.setGravity(Gravity.CENTER);
        if(complaintFilter.getHighlight()) {
            holder.cfTitle.setBackgroundColor(context.getResources().getColor(R.color.navy_blue_background));
        }

        return row;
    }

    static class FilterDtoHolder
    {
        TextView cfTitle;
    }
}

