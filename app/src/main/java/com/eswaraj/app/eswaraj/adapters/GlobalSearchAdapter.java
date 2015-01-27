package com.eswaraj.app.eswaraj.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.models.GlobalSearchResponseDto;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GlobalSearchAdapter extends ArrayAdapter<GlobalSearchResponseDto> {

    private Context context;
    private int layoutResourceId;
    private List<GlobalSearchResponseDto> globalSearchResponseDtoList;

    public GlobalSearchAdapter(Context context, int layoutResourceId, List<GlobalSearchResponseDto> globalSearchResponseDtoList) {
        super(context, layoutResourceId, globalSearchResponseDtoList);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.globalSearchResponseDtoList = globalSearchResponseDtoList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ResultDtoHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ResultDtoHolder();
            holder.sName = (TextView)row.findViewById(R.id.sName);
            holder.sDetails = (TextView)row.findViewById(R.id.sDetails);
            holder.sImage = (ImageView)row.findViewById(R.id.sImage);

            row.setTag(holder);
        }
        else
        {
            holder = (ResultDtoHolder)row.getTag();
        }

        GlobalSearchResponseDto globalSearchResponseDto = globalSearchResponseDtoList.get(position);

        holder.sName.setText(globalSearchResponseDto.getName());
        if(globalSearchResponseDto.getImage() != null && !globalSearchResponseDto.getImage().equals("")) {
            Picasso.with(context).load(globalSearchResponseDto.getImage().replace("http", "https")).into(holder.sImage);
        }
        if(globalSearchResponseDto.getType().equals("Location")) {
            holder.sDetails.setText(globalSearchResponseDto.getSubType());
        }
        else if(globalSearchResponseDto.getType().equals("Leader")) {
            holder.sDetails.setText(globalSearchResponseDto.getSubType() + ", " + globalSearchResponseDto.getcName() + "\n" + globalSearchResponseDto.getPartyName());
        }

        return row;
    }

    static class ResultDtoHolder
    {
        TextView sName;
        TextView sDetails;
        ImageView sImage;

    }
}
