package com.next.eswaraj.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.next.eswaraj.models.GooglePlace;

import java.util.ArrayList;


public class GooglePlaceAdapter extends ArrayAdapter<GooglePlace> {

    Context context;
    int layoutResourceId;
    ArrayList<GooglePlace> data = null;

    public GooglePlaceAdapter(Context context, int layoutResourceId, ArrayList<GooglePlace> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        GooglePlaceHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new GooglePlaceHolder();
            holder.txtDescription = (TextView)row.findViewById(android.R.id.text1);

            row.setTag(holder);
        }
        else
        {
            holder = (GooglePlaceHolder)row.getTag();
        }

        GooglePlace googlePlace = data.get(position);
        holder.txtDescription.setText(googlePlace.getDescription());

        return row;
    }

    static class GooglePlaceHolder
    {
        TextView txtDescription;
    }
}
