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
import com.next.eswaraj.models.DialogItem;

import java.util.List;

public class DialogAdapter extends ArrayAdapter<DialogItem>{

    private Context context;
    private int resourceId;
    private List<DialogItem> dialogItems;

    public DialogAdapter(Context context, int resource, List<DialogItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resourceId = resource;
        this.dialogItems = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        DialogItemHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resourceId, parent, false);

            holder = new DialogItemHolder();
            holder.icon = (ImageView)row.findViewById(R.id.sdIcon);
            holder.name = (TextView)row.findViewById(R.id.sdName);
            holder.title = (TextView)row.findViewById(R.id.sdTitle);

            row.setTag(holder);
        }
        else
        {
            holder = (DialogItemHolder)row.getTag();
        }

        DialogItem dialogItem = dialogItems.get(position);
        //holder.icon.loadData("<html><body><img src=\"" + dialogItem.getIcon() + "\"></body></html>", "text/html", null);
        if(dialogItem.getIcon() != null) {
            holder.icon.setImageBitmap(dialogItem.getIcon());
        }
        holder.name.setText(dialogItem.getName());
        holder.title.setText(dialogItem.getTitle());

        return row;
    }

    private static class DialogItemHolder {
        ImageView icon;
        TextView name;
        TextView title;
    }
}
