package com.eswaraj.app.eswaraj.adapters;


import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.models.CommentDto;
import com.eswaraj.app.eswaraj.widgets.CustomNetworkImageView;
import com.squareup.picasso.Picasso;

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
            holder.cImage = (CustomNetworkImageView)row.findViewById(R.id.cImage);

            row.setTag(holder);
        }
        else
        {
            holder = (CommentDtoHolder)row.getTag();
        }

        CommentDto commentDto = commentDtoList.get(position);

        if(commentDto.getPostedBy() != null) {
            holder.cName.setText(commentDto.getPostedBy().getName());
            if(commentDto.getPostedBy().getProfilePhoto() != null && !commentDto.getPostedBy().getProfilePhoto().equals("")) {
                Picasso.with(context).load(commentDto.getPostedBy().getProfilePhoto().replace("http", "https")).error(R.drawable.anon).placeholder(R.drawable.anon).into(holder.cImage);
            }
            else {
                holder.cImage.setImageDrawable(context.getResources().getDrawable(R.drawable.anon));
            }
        }
        holder.cText.setText(commentDto.getText());
        holder.cTime.setText(DateUtils.getRelativeTimeSpanString(commentDto.getCreationTime(), new Date().getTime(), DateUtils.MINUTE_IN_MILLIS));
        return row;
    }

    public void addComment(CommentDto newCommentDto, Boolean start) {
        if(start) {
            commentDtoList.add(0, newCommentDto);
        }
        else {
            commentDtoList.add(newCommentDto);
        }
    }

    public void updateList(List<CommentDto> commentDtoList) {
        this.commentDtoList = commentDtoList;
    }

    static class CommentDtoHolder
    {
        TextView cName;
        TextView cText;
        TextView cTime;
        CustomNetworkImageView cImage;
    }
}
