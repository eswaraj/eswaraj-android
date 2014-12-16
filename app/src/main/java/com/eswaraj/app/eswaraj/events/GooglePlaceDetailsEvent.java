package com.eswaraj.app.eswaraj.events;


import com.eswaraj.app.eswaraj.models.GooglePlace;

public class GooglePlaceDetailsEvent extends BaseEvent {
    private GooglePlace googlePlace;

    public void setGooglePlace(GooglePlace googlePlace) {
        this.googlePlace = googlePlace;
    }

    public GooglePlace getGooglePlace() {
        return googlePlace;
    }
}
