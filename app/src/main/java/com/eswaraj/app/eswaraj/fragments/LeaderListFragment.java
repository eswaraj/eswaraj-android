package com.eswaraj.app.eswaraj.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.activities.LeaderActivity;
import com.eswaraj.app.eswaraj.adapters.GlobalSearchAdapter;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.GetLeadersEvent;
import com.eswaraj.app.eswaraj.events.ShowLeaderEvent;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.DialogItem;
import com.eswaraj.app.eswaraj.models.GlobalSearchResponseDto;
import com.eswaraj.app.eswaraj.models.PoliticalBodyAdminDto;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.eswaraj.app.eswaraj.widgets.CustomProgressDialog;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class LeaderListFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    UserSessionUtil userSession;
    @Inject
    MiddlewareServiceImpl middlewareService;

    private ListView llList;
    private TextView llEmpty;
    private CustomProgressDialog pDialog;

    private Long id;
    private List<GlobalSearchResponseDto> globalSearchResponseDtoList;
    private GlobalSearchAdapter globalSearchAdapter;

    public LeaderListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);
        pDialog = new CustomProgressDialog(getActivity(), false, true, "Fetching leaders...");
        pDialog.show();
        id = getActivity().getIntent().getLongExtra("ID", 0);
        if(id == 0) {
            middlewareService.loadLeaders(getActivity(), userSession, true);
        }
    }

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_leader_list, container, false);
        llList = (ListView) rootView.findViewById(R.id.llList);
        llEmpty = (TextView) rootView.findViewById(R.id.llEmpty);

        llList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShowLeaderEvent event = new ShowLeaderEvent();
                event.setSuccess(true);
                event.setPoliticalBodyAdminDto(((GlobalSearchResponseDto)llList.getAdapter().getItem(position)).getPoliticalBodyAdminDto());
                eventBus.post(event);
            }
        });
        return rootView;
    }

    public void onEventMainThread(GetLeadersEvent event) {
        if(event.getSuccess()) {
            globalSearchResponseDtoList = new ArrayList<GlobalSearchResponseDto>();
            for(PoliticalBodyAdminDto politicalBodyAdminDto : event.getPoliticalBodyAdminDtos()) {
                GlobalSearchResponseDto globalSearchResponseDto = new GlobalSearchResponseDto();
                globalSearchResponseDto.setType("Leader");
                globalSearchResponseDto.setSubType(politicalBodyAdminDto.getPoliticalAdminType().getShortName());
                globalSearchResponseDto.setcName(politicalBodyAdminDto.getLocation().getName());
                globalSearchResponseDto.setPartyName(politicalBodyAdminDto.getParty().getName());
                globalSearchResponseDto.setImage(politicalBodyAdminDto.getProfilePhoto());
                globalSearchResponseDto.setId(politicalBodyAdminDto.getId());
                globalSearchResponseDto.setPoliticalBodyAdminDto(politicalBodyAdminDto);
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
        else {
            Toast.makeText(getActivity(), "Could not fetch list of leaders. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
        pDialog.dismiss();
    }

}
