package com.eswaraj.app.eswaraj.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.models.CommentDto;

import java.util.Date;
import java.util.List;


public class CommentListAdapter extends ArrayAdapter<CommentDto> {

    private Context context;
    private int layoutResourceId;
    private List<CommentDto> commentDtoList;

    public CommentListAdapter(Context context, int layoutResourceId, List<CommentDto> commentDtoList) {
        super(context, layoutResourceId, commentDtoList);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.commentDtoList = commentDtoList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CommentDtoHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new CommentDtoHolder();
            holder.cName = (TextView)row.findViewById(R.id.cName);
            holder.cText = (TextView)row.findViewById(R.id.cText);
            holder.cTime = (TextView)row.findViewById(R.id.cTime);

            row.setTag(holder);
        }
        else
        {
            holder = (CommentDtoHolder)row.getTag();
        }

        CommentDto commentDto = commentDtoList.get(position);

        holder.cName.setText(commentDto.getPostedBy().getName());
        holder.cText.setText(commentDto.getText());
        holder.cTime.setText(new Date(commentDto.getCreationTime()).toString());
        return row;
    }

    public void addComment(CommentDto newCommentDto) {
        commentDtoList.add(0, newCommentDto);
    }

    public void updateList(List<CommentDto> commentDtoList) {
        this.commentDtoList = commentDtoList;
    }

    static class CommentDtoHolder
    {
        TextView cName;
        TextView cText;
        TextView cTime;
    }
}
