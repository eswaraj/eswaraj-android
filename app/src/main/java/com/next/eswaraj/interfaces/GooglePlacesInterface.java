package com.next.eswaraj.interfaces;

import com.next.eswaraj.models.GooglePlace;

import java.util.ArrayList;

public interface GooglePlacesInterface {
    public void onPlacesListAvailable(ArrayList<GooglePlace> googlePlaceArrayList);
    public void onPlaceDetailsAvailable(GooglePlace googlePlace);
}
