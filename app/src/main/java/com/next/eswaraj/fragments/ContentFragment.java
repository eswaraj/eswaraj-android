package com.next.eswaraj.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.next.eswaraj.R;
import com.next.eswaraj.adapters.VideoContentListAdapter;
import com.next.eswaraj.base.BaseFragment;
import com.next.eswaraj.events.BannerClickEvent;
import com.next.eswaraj.helpers.GoogleAnalyticsTracker;
import com.next.eswaraj.models.VideoContentItem;
import com.next.eswaraj.util.GlobalSessionUtil;
import com.next.eswaraj.widgets.NonScrollableListView;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class ContentFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    GlobalSessionUtil globalSession;
    @Inject
    GoogleAnalyticsTracker googleAnalyticsTracker;

    private NonScrollableListView cVideoList;
    private String pattern = ".*v=(.*)";

    public ContentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);
        cVideoList = (NonScrollableListView) rootView.findViewById(R.id.cVideoList);

        ArrayList<VideoContentItem> videoContentItems = new ArrayList<VideoContentItem>();

        for(CategoryWithChildCategoryDto categoryDto : globalSession.getCategoryDtoList()) {
            VideoContentItem videoContentItem = new VideoContentItem();
            videoContentItem.setName(categoryDto.getName());
            videoContentItem.setLink(extractVideoId(categoryDto.getVideoUrl()));
            videoContentItems.add(videoContentItem);
        }

        VideoContentListAdapter videoContentListAdapter = new VideoContentListAdapter(getActivity(), R.layout.item_video_content_list, videoContentItems);
        cVideoList.setAdapter(videoContentListAdapter);

        cVideoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "ContentFragment: Video = " + ((VideoContentItem) cVideoList.getAdapter().getItem(position)).getLink());
                BannerClickEvent event = new BannerClickEvent();
                event.setSuccess(true);
                event.setVideo(((VideoContentItem) cVideoList.getAdapter().getItem(position)).getLink());
                eventBus.post(event);
            }
        });
        return rootView;
    }

    private String extractVideoId(String url) {
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(url);
        if (m.find( )) {
            return m.group(1);
        }
        else {
            return null;
        }
    }
}
