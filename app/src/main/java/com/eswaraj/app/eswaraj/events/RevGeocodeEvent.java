package com.eswaraj.app.eswaraj.events;

public class RevGeocodeEvent extends BaseEvent {
    private String revGeocodedLocation;

    public RevGeocodeEvent() {
        super();
        this.revGeocodedLocation = null;
    }

    public String getRevGeocodedLocation() {
        return this.revGeocodedLocation;
    }

    public void setRevGeocodedLocation(String revGeocodedLocation) {
        this.revGeocodedLocation = revGeocodedLocation;
    }
}
