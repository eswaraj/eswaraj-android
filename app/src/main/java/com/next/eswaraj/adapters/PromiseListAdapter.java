package com.next.eswaraj.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
            holder.plTitle = (TextView) row.findViewById(android.R.id.text1);

            row.setTag(holder);
        } else {
            holder = (PromiseDtoHolder) row.getTag();
        }

        PromiseDto promiseDto = promiseDtoList.get(position);
        holder.plTitle.setText(promiseDto.getTitle());

        return row;
    }

    static class PromiseDtoHolder {
        TextView plTitle;
    }
}
