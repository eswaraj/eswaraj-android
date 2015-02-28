package com.next.eswaraj.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.next.eswaraj.R;
import com.next.eswaraj.activities.DocumentActivity;
import com.next.eswaraj.activities.YoutubeActivity;
import com.next.eswaraj.config.Constants;
import com.next.eswaraj.models.TimelineDto;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TimelineListAdapter extends ArrayAdapter<TimelineDto> implements YouTubeThumbnailView.OnInitializedListener {

    private Context context;
    private int layoutResourceId;
    private List<TimelineDto> timelineDtoList;
    private Map<View, YouTubeThumbnailLoader> loaders;


    public TimelineListAdapter(Context context, int layoutResourceId, List<TimelineDto> timelineDtoList) {
        super(context, layoutResourceId, timelineDtoList);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.timelineDtoList = timelineDtoList;
        loaders = new HashMap<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TimelineDtoHolder holder = null;

        final TimelineDto timelineDto = timelineDtoList.get(position);

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TimelineDtoHolder();
            holder.tlTitle = (TextView)row.findViewById(R.id.tlTitle);
            holder.tlDescription = (WebView)row.findViewById(R.id.tlDescription);
            holder.tlDate = (TextView)row.findViewById(R.id.tlDate);
            holder.tlYoutube = (YouTubeThumbnailView)row.findViewById(R.id.tlYoutube);
            holder.tlImage = (ImageView)row.findViewById(R.id.tlImage);
            holder.tlIcon = (ImageView)row.findViewById(R.id.tlIcon);
            holder.tlDoc = (ImageView)row.findViewById(R.id.tlDoc);
            holder.tlVideoLabel = (TextView)row.findViewById(R.id.tlVideoLabel);
            holder.tlDocLabel = (TextView)row.findViewById(R.id.tlDocLabel);
            holder.tlDocName = (TextView)row.findViewById(R.id.tlDocName);
            holder.tlDocLayout = (LinearLayout) row.findViewById(R.id.tlDocLayout);
            holder.tlVideoLayout = (LinearLayout) row.findViewById(R.id.tlVideoLayout);
            holder.tlImageLayout = (RelativeLayout) row.findViewById(R.id.tlImageLayout);

            if(timelineDto.getYoutubeUrl() != null && timelineDto.getYoutubeUrl().get(0) != null && !timelineDto.getYoutubeUrl().get(0).equals("")) {
                holder.tlYoutube.setVisibility(View.VISIBLE);
                holder.tlIcon.setVisibility(View.VISIBLE);
                holder.tlVideoLabel.setVisibility(View.VISIBLE);
                holder.tlYoutube.setTag(extractVideoId(timelineDto.getYoutubeUrl().get(0)));
                holder.tlYoutube.initialize(Constants.GOOGLE_API_KEY, this);
                holder.tlVideoLayout.setVisibility(View.VISIBLE);
            }
            else {
                holder.tlVideoLayout.setVisibility(View.GONE);
            }

            holder.tlYoutube.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, YoutubeActivity.class);
                    i.putExtra("VIDEO_ID", (String) v.getTag());
                    context.startActivity(i);
                }
            });

            row.setTag(holder);
        }
        else
        {
            holder = (TimelineDtoHolder)row.getTag();
            YouTubeThumbnailLoader loader = loaders.get(holder.tlYoutube);
            if (loader == null) {
                if(timelineDto.getYoutubeUrl() != null && timelineDto.getYoutubeUrl().get(0) != null && !timelineDto.getYoutubeUrl().get(0).equals("")) {
                    holder.tlYoutube.setVisibility(View.VISIBLE);
                    holder.tlIcon.setVisibility(View.VISIBLE);
                    holder.tlVideoLabel.setVisibility(View.VISIBLE);
                    holder.tlYoutube.setTag(extractVideoId(timelineDto.getYoutubeUrl().get(0)));
                    holder.tlYoutube.initialize(Constants.GOOGLE_API_KEY, this);
                    holder.tlVideoLayout.setVisibility(View.VISIBLE);
                }
                else {
                    holder.tlVideoLayout.setVisibility(View.GONE);
                }
            } else {
                if(timelineDto.getYoutubeUrl() != null && timelineDto.getYoutubeUrl().get(0) != null && !timelineDto.getYoutubeUrl().get(0).equals("")) {
                    holder.tlYoutube.setVisibility(View.VISIBLE);
                    holder.tlIcon.setVisibility(View.VISIBLE);
                    holder.tlVideoLabel.setVisibility(View.VISIBLE);
                    holder.tlYoutube.setTag(extractVideoId(timelineDto.getYoutubeUrl().get(0)));
                    holder.tlYoutube.initialize(Constants.GOOGLE_API_KEY, this);
                    loader.setVideo(extractVideoId(timelineDto.getYoutubeUrl().get(0)));
                }
                else {
                    holder.tlVideoLayout.setVisibility(View.GONE);
                }
            }
        }

        holder.tlTitle.setText(timelineDto.getTitle());
        holder.tlDescription.loadData(timelineDto.getDescription(), "text/html", null);
        holder.tlDate.setText(DateUtils.getRelativeTimeSpanString(timelineDto.getUpdateTime(), new Date().getTime(), DateUtils.MINUTE_IN_MILLIS));
        if(timelineDto.getImages() != null && timelineDto.getImages().get(0) != null && !timelineDto.getImages().get(0).equals("")) {
            holder.tlImageLayout.setVisibility(View.VISIBLE);
            holder.tlImage.setVisibility(View.VISIBLE);
            Picasso.with(context).load(timelineDto.getImages().get(0)).into(holder.tlImage);
        }
        else {
            holder.tlImageLayout.setVisibility(View.GONE);
        }
        if(timelineDto.getDocuments() != null && timelineDto.getDocuments().get(0) != null && !timelineDto.getDocuments().get(0).equals("")) {
            holder.tlDocLayout.setVisibility(View.VISIBLE);
            holder.tlDoc.setVisibility(View.VISIBLE);
            holder.tlDocName.setVisibility(View.VISIBLE);
            holder.tlDocLabel.setVisibility(View.VISIBLE);
            String[] parts = timelineDto.getDocuments().get(0).split("/");
            holder.tlDocName.setText(parts[parts.length - 1]);
            holder.tlDoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, DocumentActivity.class);
                    i.putExtra("URL", timelineDto.getDocuments().get(0));
                    context.startActivity(i);
                }
            });
            holder.tlDocName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, DocumentActivity.class);
                    i.putExtra("URL", timelineDto.getDocuments().get(0));
                    context.startActivity(i);
                }
            });
        }
        else {
            holder.tlDocLayout.setVisibility(View.GONE);
            holder.tlDocLabel.setVisibility(View.GONE);
        }

        return row;
    }

    private String extractVideoId(String url) {
        String video = null;
        String pattern = ".*v=(.*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(url);
        if (m.find( )) {
            video = m.group(1);
        }
        return video;
    }

    @Override
    public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
        String videoId = (String) youTubeThumbnailView.getTag();
        loaders.put(youTubeThumbnailView, youTubeThumbnailLoader);
        youTubeThumbnailLoader.setVideo(videoId);
    }

    @Override
    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
        Log.e("TimelineListAdapter", "YouTubeThumbnailLoader initialization failed");
    }

    static class TimelineDtoHolder
    {
        TextView tlTitle;
        WebView tlDescription;
        TextView tlDate;
        YouTubeThumbnailView tlYoutube;
        ImageView tlImage;
        ImageView tlIcon;
        ImageView tlDoc;
        TextView tlVideoLabel;
        TextView tlDocLabel;
        TextView tlDocName;
        LinearLayout tlDocLayout;
        LinearLayout tlVideoLayout;
        RelativeLayout tlImageLayout;
    }
}
