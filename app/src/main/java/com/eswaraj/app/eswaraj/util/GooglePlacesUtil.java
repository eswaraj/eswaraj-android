package com.eswaraj.app.eswaraj.util;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.events.GooglePlaceDetailsEvent;
import com.eswaraj.app.eswaraj.events.GooglePlacesListEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.models.GooglePlace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class GooglePlacesUtil extends BaseClass {
    @Inject
    EventBus eventBus;
    @Inject
    NetworkAccessHelper networkAccessHelper;
    private GooglePlace googlePlace;


    public void getPlacesList(String input) {
        JsonObjectRequest request = null;
        try {
            request = new JsonObjectRequest(Request.Method.GET, Constants.GOOGLE_PLACES_AUTOCOMPLETE_URL + URLEncoder.encode(input, "UTF-8"), null, createPlacesListSuccessListener(), createPlacesListErrorListener());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        networkAccessHelper.submitNetworkRequest("GetPlacesList", request);
    }

    private Response.ErrorListener createPlacesListErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Google Places Autocomplete", "Unable to connect to service", error);
                GooglePlacesListEvent event = new GooglePlacesListEvent();
                event.setSuccess(false);
                event.setError(error.toString());
                eventBus.post(event);
            }
        };
    }

    private Response.Listener<JSONObject> createPlacesListSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {

                    JSONArray jsonArray = jsonObject.getJSONArray("predictions");
                    ArrayList<GooglePlace> arrayList = new ArrayList<GooglePlace>();
                    for (int i=0; i<jsonArray.length(); i++) {
                        GooglePlace googlePlace = new GooglePlace(jsonArray.getJSONObject(i).getString("place_id"), jsonArray.getJSONObject(i).getString("description"));
                        arrayList.add(googlePlace);
                    }
                    GooglePlacesListEvent event = new GooglePlacesListEvent();
                    event.setSuccess(true);
                    event.setArrayList(arrayList);
                    eventBus.post(event);
                } catch (JSONException e) {
                    Log.e("Google Places Autocomplete", "Got bad data");
                    e.printStackTrace();
                }
            }
        };
    }

    public void getPlaceDetails(GooglePlace googlePlace) {
        this.googlePlace = googlePlace;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Constants.GOOGLE_PLACES_DETAILS_URL + this.googlePlace.getId(), null, createPlaceDetailsSuccessListener(), createPlaceDetailsErrorListener());
        networkAccessHelper.submitNetworkRequest("GetPlaceDetails", request);
    }

    private Response.ErrorListener createPlaceDetailsErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Google Places Details", "Unable to connect to service", error);
                GooglePlaceDetailsEvent event = new GooglePlaceDetailsEvent();
                event.setSuccess(false);
                event.setError(error.toString());
                eventBus.post(event);
            }
        };
    }

    private Response.Listener<JSONObject> createPlaceDetailsSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    googlePlace = new GooglePlace(null,null);
                    googlePlace.setLatitude(jsonObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                    googlePlace.setLongitude(jsonObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                    GooglePlaceDetailsEvent event = new GooglePlaceDetailsEvent();
                    event.setSuccess(true);
                    event.setGooglePlace(googlePlace);
                    eventBus.post(event);
                } catch (JSONException e) {
                    Log.e("Google Places Details", "Got bad data");
                    e.printStackTrace();
                }
            }
        };
    }
}
