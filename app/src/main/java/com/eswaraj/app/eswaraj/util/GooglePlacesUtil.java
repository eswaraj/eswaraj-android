package com.eswaraj.app.eswaraj.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.interfaces.GooglePlacesInterface;
import com.eswaraj.app.eswaraj.models.GooglePlace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GooglePlacesUtil {
    private GooglePlacesInterface context;
    private NetworkAccessHelper networkAccessHelper;
    private GooglePlace googlePlace;

    public GooglePlacesUtil(GooglePlacesInterface context) {
        this.context = context;
        this.networkAccessHelper = NetworkAccessHelper.getInstance((Context)this.context);
    }

    public void getPlacesList(String input) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Constants.GOOGLE_PLACES_AUTOCOMPLETE_URL + input, null, createPlacesListSuccessListener(), createPlacesListErrorListener());
        networkAccessHelper.submitNetworkRequest("GetPlacesList", request);
    }

    private Response.ErrorListener createPlacesListErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText((Context) context, R.string.getDataError, Toast.LENGTH_LONG).show();
                Log.e("Google Places Autocomplete", "Unable to connect to service", error);
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
                        GooglePlace googlePlace = new GooglePlace(jsonArray.getJSONObject(i).getString("id"), jsonArray.getJSONObject(i).getString("description"));
                        arrayList.add(googlePlace);
                    }
                    context.onPlacesListAvailable(arrayList);
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
                Toast.makeText((Context) context, R.string.getDataError, Toast.LENGTH_LONG).show();
                Log.e("Google Places Details", "Unable to connect to service", error);
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
                    context.onPlaceDetailsAvailable(googlePlace);
                } catch (JSONException e) {
                    Log.e("Google Places Details", "Got bad data");
                    e.printStackTrace();
                }
            }
        };
    }
}
