package com.next.eswaraj.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseFragment;
import com.eswaraj.web.dto.LocationDto;


public class ConstituencyInfoFragment extends BaseFragment {

    private LocationDto locationDto;

    private TextView cTotalHouses;
    private TextView cTotalPopulation;
    private TextView cTotalMalePopulation;
    private TextView cTotalFemalePopulation;
    private TextView cTotalLiteratePopulation;
    private TextView cTotalMaleLiteratePopulation;
    private TextView cTotalFemaleLiteratePopulation;
    private TextView cTotalWorkingPopulation;
    private TextView cTotalMaleWorkingPopulation;
    private TextView cTotalFemaleWorkingPopulation;
    private TextView cArea;
    private TextView cPerimeter;

    public ConstituencyInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_constituency_info, container, false);
        cTotalHouses = (TextView)rootView.findViewById( R.id.cTotalHouses );
        cTotalPopulation = (TextView)rootView.findViewById( R.id.cTotalPopulation );
        cTotalMalePopulation = (TextView)rootView.findViewById( R.id.cTotalMalePopulation );
        cTotalFemalePopulation = (TextView)rootView.findViewById( R.id.cTotalFemalePopulation );
        cTotalLiteratePopulation = (TextView)rootView.findViewById( R.id.cTotalLiteratePopulation );
        cTotalMaleLiteratePopulation = (TextView)rootView.findViewById( R.id.cTotalMaleLiteratePopulation );
        cTotalFemaleLiteratePopulation = (TextView)rootView.findViewById( R.id.cTotalFemaleLiteratePopulation );
        cTotalWorkingPopulation = (TextView)rootView.findViewById( R.id.cTotalWorkingPopulation );
        cTotalMaleWorkingPopulation = (TextView)rootView.findViewById( R.id.cTotalMaleWorkingPopulation );
        cTotalFemaleWorkingPopulation = (TextView)rootView.findViewById( R.id.cTotalFemaleWorkingPopulation );
        cArea = (TextView)rootView.findViewById( R.id.cArea );
        cPerimeter = (TextView)rootView.findViewById( R.id.cPerimeter );

        if(locationDto != null) {
            setInfoData(locationDto);
        }
        return rootView;
    }

    public void setInfoData(LocationDto locationDto) {
        if(cTotalHouses == null) {
            this.locationDto = locationDto;
            return;
        }

        if (locationDto.getTotalNumberOfHouses() != null) {
            cTotalHouses.setText(locationDto.getTotalNumberOfHouses().toString());
        }
        if(locationDto.getTotalPopulation() != null) {
            cTotalPopulation.setText(locationDto.getTotalPopulation().toString());
        }
        if(locationDto.getTotalMalePopulation() != null) {
            cTotalMalePopulation.setText(locationDto.getTotalMalePopulation().toString());
        }
        if(locationDto.getTotalFemalePopulation() != null) {
            cTotalFemalePopulation.setText(locationDto.getTotalFemalePopulation().toString());
        }
        if(locationDto.getTotalLiteratePopulation() != null) {
            cTotalLiteratePopulation.setText(locationDto.getTotalLiteratePopulation().toString());
        }
        if(locationDto.getTotalMaleLiteratePopulation() != null) {
            cTotalMaleLiteratePopulation.setText(locationDto.getTotalMaleLiteratePopulation().toString());
        }
        if(locationDto.getTotalFemaleLiteratePopulation() != null) {
            cTotalFemaleLiteratePopulation.setText(locationDto.getTotalFemaleLiteratePopulation().toString());
        }
        if(locationDto.getTotalWorkingPopulation() != null) {
            cTotalWorkingPopulation.setText(locationDto.getTotalWorkingPopulation().toString());
        }
        if(locationDto.getTotalMaleWorkingPopulation() != null) {
            cTotalMaleWorkingPopulation.setText(locationDto.getTotalMaleWorkingPopulation().toString());
        }
        if(locationDto.getTotalFemaleWorkingPopulation() != null) {
            cTotalFemaleWorkingPopulation.setText(locationDto.getTotalFemaleWorkingPopulation().toString());
        }
        if(locationDto.getArea() != null) {
            cArea.setText(locationDto.getArea().toString());
        }
        if(locationDto.getPerimeter() != null) {
            cPerimeter.setText(locationDto.getPerimeter().toString());
        }
    }
}
