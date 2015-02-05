package com.next.eswaraj.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.next.eswaraj.R;
import com.next.eswaraj.models.GooglePlace;

import java.util.ArrayList;


public class GooglePlacesListAdapter extends ArrayAdapter<GooglePlace> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<GooglePlace> googlePlacesList;

    public GooglePlacesListAdapter(Context context, int layoutResourceId, ArrayList<GooglePlace> googlePlacesList) {
        super(context, layoutResourceId, googlePlacesList);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.googlePlacesList = googlePlacesList;
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
            holder.stTitle = (TextView)row.findViewById(R.id.text1);

            row.setTag(holder);
        }
        else
        {
            holder = (GooglePlaceHolder)row.getTag();
        }

        GooglePlace googlePlace = googlePlacesList.get(position);
        holder.stTitle.setText(googlePlace.getDescription());

        return row;
    }

    static class GooglePlaceHolder
    {
        TextView stTitle;
    }
}
