package com.next.eswaraj.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.next.eswaraj.R;
import com.next.eswaraj.adapters.PromiseListAdapter;
import com.next.eswaraj.base.BaseFragment;
import com.next.eswaraj.events.GetPromisesEvent;
import com.next.eswaraj.events.PromiseSelectedEvent;
import com.next.eswaraj.middleware.MiddlewareServiceImpl;
import com.next.eswaraj.models.PoliticalBodyAdminDto;
import com.next.eswaraj.models.PromiseDto;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class PromisesListFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;

    private ListView plList;
    private TextView plPlaceholder;

    private PoliticalBodyAdminDto politicalBodyAdminDto;
    private List<PromiseDto> promiseDtoList;

    public PromisesListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);

        politicalBodyAdminDto = (PoliticalBodyAdminDto) getActivity().getIntent().getSerializableExtra("LEADER");
        getActivity().setTitle(politicalBodyAdminDto.getName() + "'s Promises");
        middlewareService.loadPromisesByLeaders(getActivity(), politicalBodyAdminDto.getId());
    }

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_promises_list, container, false);
        plList = (ListView) rootView.findViewById(R.id.plList);
        plPlaceholder = (TextView) rootView.findViewById(R.id.plPlaceholder);

        plList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PromiseSelectedEvent event = new PromiseSelectedEvent();
                event.setSuccess(true);
                event.setPromiseDto((PromiseDto) plList.getAdapter().getItem(position));
                eventBus.post(event);
            }
        });
        return rootView;
    }

    public void onEventMainThread(GetPromisesEvent event) {
        if(event.getSuccess()) {
            promiseDtoList = event.getPromiseDtoList();
            plList.setAdapter(new PromiseListAdapter(getActivity(), android.R.layout.simple_list_item_1, promiseDtoList));
            if(promiseDtoList.size() == 0) {
                plPlaceholder.setVisibility(View.VISIBLE);
            }
        }
        else {
            Toast.makeText(getActivity(), event.getError(), Toast.LENGTH_LONG).show();
        }
    }

}
