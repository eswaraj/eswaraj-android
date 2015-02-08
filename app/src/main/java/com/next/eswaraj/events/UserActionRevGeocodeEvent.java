package com.next.eswaraj.events;


import com.next.eswaraj.base.BaseEvent;

public class UserActionRevGeocodeEvent extends BaseEvent {

    private String revGeocodedLocation;
    private String revGeocodedFullData;

    public String getRevGeocodedFullData() {
        return revGeocodedFullData;
    }

    public void setRevGeocodedFullData(String revGeocodedFullData) {
        this.revGeocodedFullData = revGeocodedFullData;
    }

    public UserActionRevGeocodeEvent() {
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
