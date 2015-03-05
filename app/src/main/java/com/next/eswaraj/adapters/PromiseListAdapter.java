package com.next.eswaraj.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.next.eswaraj.R;
import com.next.eswaraj.models.PromiseDto;

import java.util.List;

public class PromiseListAdapter extends ArrayAdapter<PromiseDto> {

    private Context context;
    private int layoutResourceId;
    private List<PromiseDto> promiseDtoList;

    public PromiseListAdapter(Context context, int layoutResourceId, List<PromiseDto> promiseDtoList) {
        super(context, layoutResourceId, promiseDtoList);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.promiseDtoList = promiseDtoList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PromiseDtoHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new PromiseDtoHolder();
            holder.plTitle = (TextView) row.findViewById(R.id.plTitle);
            holder.plStatus = (TextView)row.findViewById(R.id.plStatus);
            holder.plDelivery = (TextView)row.findViewById(R.id.plDelivery);

            row.setTag(holder);
        } else {
            holder = (PromiseDtoHolder) row.getTag();
        }

        PromiseDto promiseDto = promiseDtoList.get(position);
        holder.plTitle.setText(promiseDto.getTitle());
        if(promiseDto.getDeliveryTime() != null && !promiseDto.getDeliveryTime().equals("")) {
            holder.plDelivery.setText(promiseDto.getDeliveryTime());
            holder.plDelivery.setVisibility(View.VISIBLE);
        }
        else {
            holder.plDelivery.setVisibility(View.GONE);
        }
        if(promiseDto.getStatus() != null && !promiseDto.getStatus().equals("")) {
            holder.plStatus.setText(promiseDto.getStatus());
            if(promiseDto.getStatus().equals("Pending")) {
                holder.plStatus.setBackgroundResource(R.drawable.red_promise_rounded_corner);
            }
            else if(promiseDto.getStatus().equals("On Going")) {
                holder.plStatus.setBackgroundResource(R.drawable.blue_promise_rounded_corner);
            }
            else if(promiseDto.getStatus().equals("Delivered")) {
                holder.plStatus.setBackgroundResource(R.drawable.green_promise_rounded_corner);
            }
        }
        else {
            holder.plStatus.setText("Pending");
            holder.plStatus.setBackgroundResource(R.drawable.red_promise_rounded_corner);
        }

        return row;
    }

    static class PromiseDtoHolder {
        TextView plTitle;
        TextView plDelivery;
        TextView plStatus;
    }
}
