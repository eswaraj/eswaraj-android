package com.next.eswaraj.helpers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

import com.next.eswaraj.application.EswarajApplication;
import com.next.eswaraj.events.RevGeocodeEvent;
import com.google.gson.Gson;
import com.next.eswaraj.events.UserActionRevGeocodeEvent;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class ReverseGeocodingTask extends AsyncTask<Void, Void, Void>{

    @Inject
    EventBus eventBus;

    private Context context;
    private Location location;
    private Boolean onUserAction = false;

    public ReverseGeocodingTask(Context context, Location location) {
        this.context = context;
        this.location = location;
        EswarajApplication.getInstance().inject(this);
    }

    public ReverseGeocodingTask(Context context, Location location, Boolean onUserAction) {
        this.context = context;
        this.location = location;
        this.onUserAction = onUserAction;
        EswarajApplication.getInstance().inject(this);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Geocoder geoCoder = new Geocoder(context.getApplicationContext());
        List<Address> matches = null;
        if(isCancelled()) {
            return null;
        }
        try {
        	matches = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        	Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
            if(bestMatch != null) {
                String userLocationString = bestMatch.getAddressLine(1);
                if(bestMatch.getAddressLine(2) != null) {
                    userLocationString += ", " + bestMatch.getAddressLine(2);
                }
                if(onUserAction) {
                    UserActionRevGeocodeEvent event = new UserActionRevGeocodeEvent();
                    event.setRevGeocodedLocation(userLocationString);
                    event.setRevGeocodedFullData(new Gson().toJson(bestMatch));
                    event.setSuccess(true);
                    eventBus.post(event);
                }
                else {
                    RevGeocodeEvent event = new RevGeocodeEvent();
                    event.setRevGeocodedLocation(userLocationString);
                    event.setRevGeocodedFullData(new Gson().toJson(bestMatch));
                    event.setSuccess(true);
                    eventBus.post(event);
                }
            }
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return null;
    }
}
