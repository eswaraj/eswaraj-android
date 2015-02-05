package com.next.eswaraj.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.next.eswaraj.R;
import com.next.eswaraj.models.VideoContentItem;

import java.util.List;

public class VideoContentListAdapter extends ArrayAdapter<VideoContentItem> {

    private Context context;
    private int layoutResourceId;
    private List<VideoContentItem> videoContentItemList;

    public VideoContentListAdapter(Context context, int layoutResourceId, List<VideoContentItem> videoContentItemList) {
        super(context, layoutResourceId, videoContentItemList);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.videoContentItemList = videoContentItemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        VideoContentDtoHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new VideoContentDtoHolder();
            holder.cText = (TextView)row.findViewById(R.id.vcName);

            row.setTag(holder);
        }
        else
        {
            holder = (VideoContentDtoHolder)row.getTag();
        }

        VideoContentItem videoContentItem = videoContentItemList.get(position);

        holder.cText.setText(videoContentItem.getName());
        return row;
    }

    static class VideoContentDtoHolder
    {
        TextView cText;
    }
}
