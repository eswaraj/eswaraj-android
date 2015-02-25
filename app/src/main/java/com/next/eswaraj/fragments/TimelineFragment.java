package com.next.eswaraj.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.next.eswaraj.R;
import com.next.eswaraj.adapters.TimelineListAdapter;
import com.next.eswaraj.base.BaseFragment;
import com.next.eswaraj.config.TimelineType;
import com.next.eswaraj.events.GetTimelineEvent;
import com.next.eswaraj.middleware.MiddlewareServiceImpl;
import com.next.eswaraj.models.TimelineDto;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class TimelineFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;

    private TimelineType type;
    private Long id;
    private int requestCount = 10;
    private int totalCount = 0;
    private Boolean isStopped = false;
    private List<TimelineDto> timelineDtoList;
    private TimelineListAdapter timelineListAdapter;
    private View header;
    private Boolean headerAdded = false;
    private Boolean footerAdded = false;
    private Boolean dataDownloadStarted = false;
    private Button showMore;

    private RelativeLayout tlRelativeLayout;
    private ListView tlList;

    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        isStopped = true;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        isStopped = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
        tlRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.tlRelativeLayout);
        tlList = (ListView) rootView.findViewById(R.id.tlList);

        if(header != null) {
            tlList.addHeaderView(header);
            headerAdded = true;
        }

        showMore = new Button(getActivity());
        setFooter(showMore);

        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                middlewareService.loadTimeline(getActivity(), type, id, totalCount, requestCount);
            }
        });

        if(!dataDownloadStarted && id != null) {
            middlewareService.loadTimeline(getActivity(), type, id, totalCount, requestCount);
        }
        return rootView;
    }

    public void setTypeAndId(TimelineType type, Long id) {
        this.type = type;
        this.id = id;
        if(getActivity() != null) {
            middlewareService.loadTimeline(getActivity(), type, id, totalCount, requestCount);
            dataDownloadStarted = true;
        }
    }

    public void onEventMainThread(GetTimelineEvent event) {
        if(!isStopped && event.getType() == type) {
            if(event.getSuccess()) {
                if(timelineDtoList == null) {
                    timelineDtoList = event.getTimelineDtoList();
                }
                else {
                    for(TimelineDto timelineDto : event.getTimelineDtoList()) {
                        timelineDtoList.add(timelineDto);
                    }
                }
                totalCount += event.getTimelineDtoList().size();
                if(event.getTimelineDtoList().size() < requestCount) {
                    showMore.setVisibility(View.GONE);
                }
                timelineListAdapter = new TimelineListAdapter(getActivity(), R.layout.item_timeline_list, timelineDtoList);
                tlList.setAdapter(timelineListAdapter);
            }
            else {
                Toast.makeText(getActivity(), event.getError(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setHeader(View view) {
        if(tlList != null) {
            if(!headerAdded) {
                tlList.addHeaderView(view);
                headerAdded = true;
            }
        }
        else {
            header = view;
        }
    }

    public void setFooter(View view) {
        if(tlList != null) {
            if(!footerAdded) {
                tlList.addFooterView(view);
                footerAdded = true;
            }
        }
    }

    public void removeHeader(View view) {
        tlList.removeHeaderView(view);
    }

}
