package com.next.eswaraj.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.next.eswaraj.R;
import com.next.eswaraj.adapters.GlobalSearchAdapter;
import com.next.eswaraj.base.BaseFragment;
import com.next.eswaraj.events.ShowLocationEvent;
import com.next.eswaraj.models.GlobalSearchResponseDto;
import com.next.eswaraj.util.UserSessionUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class LocationListFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    UserSessionUtil userSession;

    private List<GlobalSearchResponseDto> globalSearchResponseDtoList;
    private GlobalSearchAdapter globalSearchAdapter;

    private ListView llList;
    private TextView llEmpty;

    public LocationListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location_list, container, false);
        llList = (ListView) rootView.findViewById(R.id.llList);
        llEmpty = (TextView) rootView.findViewById(R.id.llEmpty);

        llList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShowLocationEvent event = new ShowLocationEvent();
                event.setSuccess(true);
                event.setLocationDto(((GlobalSearchResponseDto)llList.getAdapter().getItem(position)).getLocationDto());
                eventBus.post(event);
            }
        });
        setData();
        return rootView;
    }

    private void setData() {
        globalSearchResponseDtoList = new ArrayList<GlobalSearchResponseDto>();
        if(userSession.getUser().getPerson().getPersonAddress().getState() != null) {
            GlobalSearchResponseDto globalSearchResponseDto = new GlobalSearchResponseDto();
            globalSearchResponseDto.setType("Location");
            globalSearchResponseDto.setSubType("State");
            globalSearchResponseDto.setName(userSession.getUser().getPerson().getPersonAddress().getState().getName());
            globalSearchResponseDto.setImage(userSession.getUser().getPerson().getPersonAddress().getState().getMobileHeaderImageUrl());
            globalSearchResponseDto.setId(userSession.getUser().getPerson().getPersonAddress().getState().getId());
            globalSearchResponseDto.setLocationDto(userSession.getUser().getPerson().getPersonAddress().getState());
            globalSearchResponseDtoList.add(globalSearchResponseDto);
        }

        if(userSession.getUser().getPerson().getPersonAddress().getPc() != null) {
            GlobalSearchResponseDto globalSearchResponseDto = new GlobalSearchResponseDto();
            globalSearchResponseDto.setType("Location");
            globalSearchResponseDto.setSubType("Parliamentary Constituency");
            globalSearchResponseDto.setName(userSession.getUser().getPerson().getPersonAddress().getPc().getName());
            globalSearchResponseDto.setImage(userSession.getUser().getPerson().getPersonAddress().getPc().getMobileHeaderImageUrl());
            globalSearchResponseDto.setId(userSession.getUser().getPerson().getPersonAddress().getPc().getId());
            globalSearchResponseDto.setLocationDto(userSession.getUser().getPerson().getPersonAddress().getPc());
            globalSearchResponseDtoList.add(globalSearchResponseDto);
        }

        if(userSession.getUser().getPerson().getPersonAddress().getAc() != null) {
            GlobalSearchResponseDto globalSearchResponseDto = new GlobalSearchResponseDto();
            globalSearchResponseDto.setType("Location");
            globalSearchResponseDto.setSubType("Assembly Constituency");
            globalSearchResponseDto.setName(userSession.getUser().getPerson().getPersonAddress().getAc().getName());
            globalSearchResponseDto.setImage(userSession.getUser().getPerson().getPersonAddress().getAc().getMobileHeaderImageUrl());
            globalSearchResponseDto.setId(userSession.getUser().getPerson().getPersonAddress().getAc().getId());
            globalSearchResponseDto.setLocationDto(userSession.getUser().getPerson().getPersonAddress().getAc());
            globalSearchResponseDtoList.add(globalSearchResponseDto);
        }

        if(userSession.getUser().getPerson().getPersonAddress().getWard() != null) {
            GlobalSearchResponseDto globalSearchResponseDto = new GlobalSearchResponseDto();
            globalSearchResponseDto.setType("Location");
            globalSearchResponseDto.setSubType("Ward");
            globalSearchResponseDto.setName(userSession.getUser().getPerson().getPersonAddress().getWard().getName());
            globalSearchResponseDto.setImage(userSession.getUser().getPerson().getPersonAddress().getWard().getMobileHeaderImageUrl());
            globalSearchResponseDto.setId(userSession.getUser().getPerson().getPersonAddress().getWard().getId());
            globalSearchResponseDto.setLocationDto(userSession.getUser().getPerson().getPersonAddress().getWard());
            globalSearchResponseDtoList.add(globalSearchResponseDto);
        }

        if(globalSearchResponseDtoList.size() == 0) {
            llEmpty.setVisibility(View.VISIBLE);
            llList.setVisibility(View.GONE);
        }
        else {
            globalSearchAdapter = new GlobalSearchAdapter(getActivity(), R.layout.item_global_search_result, globalSearchResponseDtoList);
            llList.setAdapter(globalSearchAdapter);
        }
    }
}
