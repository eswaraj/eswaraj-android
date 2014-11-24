package com.eswaraj.app.eswaraj.interfaces;

import com.eswaraj.app.eswaraj.models.GooglePlace;

import java.util.ArrayList;

public interface GooglePlacesInterface {
    public void onPlacesListAvailable(ArrayList<GooglePlace> googlePlaceArrayList);
    public void onPlaceDetailsAvailable(GooglePlace googlePlace);
}
