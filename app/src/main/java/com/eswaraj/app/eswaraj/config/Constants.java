package com.eswaraj.app.eswaraj.config;

import com.android.volley.toolbox.StringRequest;

public class Constants {

    public static final long SERVER_DATA_UPDATE_INTERVAL_IN_MS = 7*24*60*60*1000;

    //API keys
    public static final String GOOGLE_API_KEY = "AIzaSyAUkrt-jj0Udm_sGUlcmJrGMUG4Pr5Bo8k";
    public static final String FACEBOOK_API_KEY = "";
    public static final String YOUTUBE_API_KEY = "";

    //eSwaraj URLS
    public static final String BASE_URL = "http://dev.api.eswaraj.com/api/v0/";
    public static final String GET_CATEGORIES_URL = BASE_URL + "categories";
    public static final String SAVE_FACEBOOK_USER_URL = BASE_URL + "user/facebook";

    //Google URLS
    public static final String GOOGLE_PLACES_AUTOCOMPLETE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=" + GOOGLE_API_KEY + "&input=";
    public static final String GOOGLE_PLACES_DETAILS_URL= "https://maps.googleapis.com/maps/api/place/details/json?key=" + GOOGLE_API_KEY + "&placeid=";

}
