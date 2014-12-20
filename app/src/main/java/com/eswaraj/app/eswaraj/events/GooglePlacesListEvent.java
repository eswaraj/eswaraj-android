package com.eswaraj.app.eswaraj.events;


import com.eswaraj.app.eswaraj.base.BaseEvent;
import com.eswaraj.app.eswaraj.models.GooglePlace;

import java.util.ArrayList;

public class GooglePlacesListEvent extends BaseEvent {
    private ArrayList<GooglePlace> arrayList;

    public void setArrayList(ArrayList<GooglePlace> arrayList) {
        this.arrayList = arrayList;
    }

    public ArrayList<GooglePlace> getArrayList() {
        return arrayList;
    }
}
