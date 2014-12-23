package com.eswaraj.app.eswaraj.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.adapters.GooglePlacesListAdapter;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.GooglePlacesListEvent;
import com.eswaraj.app.eswaraj.models.GooglePlace;
import com.eswaraj.app.eswaraj.util.GooglePlacesUtil;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class GooglePlacesListFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    GooglePlacesUtil googlePlacesUtil;

    private ListView mlSearchResults;

    public GooglePlacesListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_google_places_list, container, false);
        mlSearchResults = (ListView) rootView.findViewById(R.id.mlSearchResults);
        mlSearchResults.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                googlePlacesUtil.getPlaceDetails((GooglePlace) mlSearchResults.getAdapter().getItem(position));
            }
        });
        return rootView;
    }


    public void onEventMainThread(GooglePlacesListEvent event) {
        if(event.getSuccess()) {
            mlSearchResults.setAdapter(new GooglePlacesListAdapter(getActivity(), android.R.layout.simple_list_item_1, event.getArrayList()));
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch list of places. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        eventBus.registerSticky(this);
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }
}
