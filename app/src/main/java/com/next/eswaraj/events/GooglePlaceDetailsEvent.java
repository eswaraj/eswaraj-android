package com.next.eswaraj.events;


import com.next.eswaraj.base.BaseEvent;
import com.next.eswaraj.models.GooglePlace;

public class GooglePlaceDetailsEvent extends BaseEvent {
    private GooglePlace googlePlace;

    public void setGooglePlace(GooglePlace googlePlace) {
        this.googlePlace = googlePlace;
    }

    public GooglePlace getGooglePlace() {
        return googlePlace;
    }
}
