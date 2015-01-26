package com.eswaraj.app.eswaraj.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.adapters.ComplaintListAdapter;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.ComplaintSelectedEvent;
import com.eswaraj.app.eswaraj.models.ComplaintDto;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class ComplaintListFragment extends BaseFragment {

    @Inject
    EventBus eventBus;

    private ListView mcList;
    private ComplaintListAdapter complaintsAdapter;
    private View header;
    private View footer;

    public ComplaintListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_complaint_list, container, false);
        mcList = (ListView) rootView.findViewById(R.id.mcList);

        if(header != null) {
            mcList.addHeaderView(header);
        }
        if(footer != null) {
            mcList.addFooterView(footer);
        }

        mcList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ComplaintSelectedEvent event = new ComplaintSelectedEvent();
                event.setComplaintDto((ComplaintDto) mcList.getAdapter().getItem(position));
                eventBus.post(event);
            }
        });

        return rootView;
    }

    public void setData(List<ComplaintDto> complaintDtoList) {
        complaintsAdapter = new ComplaintListAdapter(getActivity(), R.layout.item_complaint_list, complaintDtoList);
        mcList.setAdapter(complaintsAdapter);
        mcList.setDividerHeight(0);
    }

    public void markComplaintClosed(Long id) {
        complaintsAdapter.markComplaintClosed(id);
        complaintsAdapter.notifyDataSetChanged();
    }

    public void setHeader(View view) {
        if(mcList != null) {
            mcList.addHeaderView(view);
        }
        else {
            header = view;
        }
    }

    public void setFooter(View view) {
        if(mcList != null) {
            mcList.addFooterView(view);
        }
        else {
            footer = view;
        }
    }

    public void removeHeader(View view) {
        mcList.removeHeaderView(view);
    }

    public void removeFooter(View view) {
        mcList.removeFooterView(view);
    }
}
