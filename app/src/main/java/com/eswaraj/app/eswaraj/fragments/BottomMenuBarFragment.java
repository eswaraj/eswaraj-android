package com.eswaraj.app.eswaraj.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.activities.SplashActivity;
import com.eswaraj.app.eswaraj.adapters.BottomMenuBarAdapter;
import com.eswaraj.app.eswaraj.models.BottomMenuBarItem;

import java.util.ArrayList;


public class BottomMenuBarFragment extends Fragment {

    public static BottomMenuBarFragment newInstance() {
        BottomMenuBarFragment fragment = new BottomMenuBarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public BottomMenuBarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bottom_menu_bar, container, false);
        final GridView gridView = (GridView) rootView.findViewById(R.id.GridViewBottomMenuBar);
        ArrayList<BottomMenuBarItem> arrayList = getBottomMenuBarData();
        BottomMenuBarAdapter bottomMenuBarAdapter = new BottomMenuBarAdapter(getActivity(), R.layout.bottom_menubar_item, arrayList);
        gridView.setAdapter(bottomMenuBarAdapter);

        gridView.setOnItemClickListener(new GridView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Launch the target activity when an item is clicked
                BottomMenuBarItem item = (BottomMenuBarItem) gridView.getAdapter().getItem(position);
                Class targetActivity = item.getTargetActivity();
                if(targetActivity != null) {
                    Intent i = new Intent(getActivity(), targetActivity);
                    startActivity(i);
                }
            }
        });
        return rootView;
    }

    private ArrayList<BottomMenuBarItem> getBottomMenuBarData() {
        ArrayList<BottomMenuBarItem> arrayList = new ArrayList<BottomMenuBarItem>();
        //Add all navigation items here
        //-1 means that icon is not present
        //null means that no target activity needs to be launched
        arrayList.add(new BottomMenuBarItem(-1, "My Complaints", null));
        arrayList.add(new BottomMenuBarItem(-1, "My Constituencies", null));
        arrayList.add(new BottomMenuBarItem(-1, "My Leaders", null));
        arrayList.add(new BottomMenuBarItem(-1, "My Profile", null));
        return arrayList;
    }


}
