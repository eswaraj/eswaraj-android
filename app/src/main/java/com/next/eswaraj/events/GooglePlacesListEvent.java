package com.next.eswaraj.events;


import com.next.eswaraj.base.BaseEvent;
import com.next.eswaraj.models.GooglePlace;

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
