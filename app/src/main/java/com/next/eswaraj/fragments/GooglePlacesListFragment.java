package com.next.eswaraj.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.next.eswaraj.R;
import com.next.eswaraj.adapters.GooglePlacesListAdapter;
import com.next.eswaraj.base.BaseFragment;
import com.next.eswaraj.models.GooglePlace;
import com.next.eswaraj.util.GooglePlacesUtil;

import java.util.ArrayList;

import javax.inject.Inject;


public class GooglePlacesListFragment extends BaseFragment {

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

    public void setPlacesList(ArrayList<GooglePlace> googlePlaceList) {
        mlSearchResults.setAdapter(new GooglePlacesListAdapter(getActivity(), R.layout.item_google_places_list, googlePlaceList));
    }
}
