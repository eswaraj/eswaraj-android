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
    public static final String BASE_URL_2 = "http://dev.api.eswaraj.com";
    public static final String GET_CATEGORIES_URL = BASE_URL + "/categories";
    public static final String SAVE_FACEBOOK_USER_URL = BASE_URL + "/user/facebook";
    public static final String SAVE_DEVICE_ANONYMOUS_USER_URL = BASE_URL + "/user/device";
    public static final String POST_COMPLAINT_URL = BASE_URL + "/complaint";
    public static final String USER_COMPLAINTS_URL = BASE_URL + "/user/complaints";
    public static final String COMMENT_POST_URL = BASE_URL + "/complaint/user/comment";
    public static final String COMPLAINT_CLOSE_URL = BASE_URL + "/complaint/user/status";
    public static final String UPDATE_PROFILE_URL = BASE_URL + "/mobile/user/profile";
    public static final String GET_PROFILE_URL = BASE_URL + "/mobile/user/profile/";
    public static final String LOCATION_COUNTERS_URL = BASE_URL_2 + "/stats/counter/location/";
    public static final String LOCATION_COMPLAINTS_URL = BASE_URL + "/complaint/location/";

    //Google URLS
    public static final String GOOGLE_PLACES_AUTOCOMPLETE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=" + GOOGLE_API_BROWSER_KEY + "&components=country:in&input=";
    public static final String GOOGLE_PLACES_DETAILS_URL= "https://maps.googleapis.com/maps/api/place/details/json?key=" + GOOGLE_API_BROWSER_KEY + "&placeid=";

    public static String getCommentsUrl(Long complaintId, int start, int count) {
        return BASE_URL + "/complaint/" + complaintId + "/comments?count=" + count + "&start=" + start + "&order=DESC";
    }

    public static String getLocationCountersPerRootCategoryUrl(Long locationId, Long rootCategoryId) {
        return BASE_URL_2 + "/stats/counter/location/" + locationId + "/parentcategory/" + rootCategoryId;
    }

    public static String getLocationComplaintsUrl(Long locationId, int start, int count) {
        return LOCATION_COMPLAINTS_URL + locationId + "?count=" + count + "&start=" + start;
    }
}
