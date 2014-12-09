package com.eswaraj.app.eswaraj.config;

public class Constants {

    public static final long SERVER_DATA_UPDATE_INTERVAL_IN_MS = 7*24*60*60*1000;

    //API keys
    public static final String GOOGLE_API_KEY = "";
    public static final String FACEBOOK_API_KEY = "";
    public static final String YOUTUBE_API_KEY = "";

    //eSwaraj URLS
    public static final String BASE_URL = "http://dev.eswaraj.com/";
    public static final String GET_CATEGORIES_URL = BASE_URL + "/ajax/categories";

    //Google URLS
    public static final String GOOGLE_PLACES_AUTOCOMPLETE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=" + GOOGLE_API_KEY + "&input=";
    public static final String GOOGLE_PLACES_DETAILS_URL= "https://maps.googleapis.com/maps/api/place/details/json?key=" + GOOGLE_API_KEY + "&placeid=";

}
