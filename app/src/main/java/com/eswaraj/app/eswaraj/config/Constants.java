package com.eswaraj.app.eswaraj.config;


public class Constants {

    public static final long SERVER_DATA_UPDATE_INTERVAL_IN_MS = 7*24*60*60*1000;

    //API keys
    public static final String GOOGLE_API_KEY = "AIzaSyBvatkHkBtQ8lk6xtRnd4ZXTX8CgxfsZQQ";
    public static final String GOOGLE_API_BROWSER_KEY = "AIzaSyBeYpT7CyYFXoPmdnZi5kj4xodwCV_ZjEY";
    public static final String FACEBOOK_API_KEY = "";
    public static final String YOUTUBE_API_KEY = "";

    //eSwaraj URLS
    public static final String BASE_URL = "http://dev.api.eswaraj.com/api/v0";
    public static final String GET_CATEGORIES_URL = BASE_URL + "/categories";
    public static final String SAVE_FACEBOOK_USER_URL = BASE_URL + "/user/facebook";
    public static final String SAVE_DEVICE_ANONYMOUS_USER_URL = BASE_URL + "/user/device";
    public static final String POST_COMPLAINT_URL = BASE_URL + "/complaint";
    public static final String USER_COMPLAINTS_URL = BASE_URL + "/user/complaints";

    //Google URLS
    public static final String GOOGLE_PLACES_AUTOCOMPLETE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=" + GOOGLE_API_BROWSER_KEY + "&components=country:in&input=";
    public static final String GOOGLE_PLACES_DETAILS_URL= "https://maps.googleapis.com/maps/api/place/details/json?key=" + GOOGLE_API_BROWSER_KEY + "&placeid=";

}
