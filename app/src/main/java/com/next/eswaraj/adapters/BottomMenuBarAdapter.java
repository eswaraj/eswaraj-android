package com.next.eswaraj.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.next.eswaraj.R;
import com.next.eswaraj.models.BottomMenuBarItem;

import java.util.ArrayList;


public class BottomMenuBarAdapter extends ArrayAdapter<BottomMenuBarItem> {

    Context context;
    int layoutResourceId;
    ArrayList<BottomMenuBarItem> data = null;

    public BottomMenuBarAdapter(Context context, int layoutResourceId, ArrayList<BottomMenuBarItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        BottomMenuBarHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new BottomMenuBarHolder();
            holder.text = (TextView)row.findViewById(R.id.textMenuItem);
            holder.icon = (ImageView)row.findViewById(R.id.iconMenuItem);

            row.setTag(holder);
        }
        else
        {
            holder = (BottomMenuBarHolder)row.getTag();
        }

        BottomMenuBarItem bottomMenuBarItem = data.get(position);
        holder.text.setText(bottomMenuBarItem.getText());
        if(bottomMenuBarItem.getIcon() != -1) {
            holder.icon.setImageResource(bottomMenuBarItem.getIcon());
        }

        return row;
    }

    static class BottomMenuBarHolder
    {
        TextView text;
        ImageView icon;
    }
}
