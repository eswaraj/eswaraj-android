package com.next.eswaraj.util;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.next.eswaraj.base.BaseClass;
import com.next.eswaraj.config.Constants;
import com.next.eswaraj.events.GooglePlaceDetailsEvent;
import com.next.eswaraj.events.GooglePlacesListEvent;
import com.next.eswaraj.helpers.NetworkAccessHelper;
import com.next.eswaraj.models.GooglePlace;

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
                        String str = jsonArray.getJSONObject(i).getString("description");
                        String title = getTitle(str);
                        String description = getDescription(str);
                        GooglePlace googlePlace = new GooglePlace(jsonArray.getJSONObject(i).getString("place_id"), title, description);
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
                    googlePlace = new GooglePlace(null,null,null);
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

    private String getTitle(String str) {
        return str.split(",")[0];
    }

    private String getDescription(String str) {
        int i = 0;
        String description = "";
        for(String s : str.split(",")) {
            if(i != 0) {
                if(i != 1) {
                    description += ", ";
                }
                description += s.trim();
            }
            i++;
        }
        return description;
    }
}
