package com.eswaraj.app.eswaraj.helpers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.eswaraj.app.eswaraj.application.EswarajApplication;
import com.eswaraj.app.eswaraj.events.RevGeocodeEvent;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class ReverseGeocodingTask extends AsyncTask<Void, Void, Void>{

    @Inject
    EventBus eventBus;

    private Context context;
    private Location location;

    public ReverseGeocodingTask(Context context, Location location) {
        this.context = context;
        this.location = location;
        EswarajApplication.getInstance().inject(this);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Geocoder geoCoder = new Geocoder(context.getApplicationContext());
        List<Address> matches = null;
        if(isCancelled()) {
            //Log.e("Address", "Cancelled");
            return null;
        }
        try {
        	matches = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        	Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
        	String userLocationString = bestMatch.getAddressLine(1) + ", " + bestMatch.getAddressLine(2);
        	//Log.e("Address", userLocationString);
            RevGeocodeEvent event = new RevGeocodeEvent();
            event.setRevGeocodedLocation(userLocationString);
            event.setRevGeocodedFullData(new Gson().toJson(bestMatch));
            event.setSuccess(true);
            eventBus.post(event);
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return null;
    }
}
