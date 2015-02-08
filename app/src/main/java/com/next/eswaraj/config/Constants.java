package com.next.eswaraj.config;


public class Constants {

    public static final long SERVER_DATA_UPDATE_INTERVAL_IN_MS = 7*24*60*60*1000;

    //API keys
    public static final String GOOGLE_API_KEY = "AIzaSyBpDIEEPbFGUr7cvXI5_nq04J6i5fou9rY";
    public static final String GOOGLE_API_BROWSER_KEY = "AIzaSyBjBEcixsQ_FNyfohbzZHgeDJqLzPaRsXw";
    public static final String FACEBOOK_API_KEY = "";
    public static final String YOUTUBE_API_KEY = "";
    public static final String GOOGLE_ANALYTICS_KEY = "UA-59500540-1";

    //eSwaraj URLS
    public static String BASE_URL = "http://dev.api.eswaraj.com/api/v0";
    public static String BASE_URL_2 = "http://dev.api.eswaraj.com";

    public static String GET_CATEGORIES_URL = BASE_URL + "/categories";
    public static String SAVE_FACEBOOK_USER_URL = BASE_URL + "/user/facebook";
    public static String SAVE_DEVICE_ANONYMOUS_USER_URL = BASE_URL + "/user/device";
    public static String POST_COMPLAINT_URL = BASE_URL + "/complaint";
    public static String USER_COMPLAINTS_URL = BASE_URL + "/user/complaints";
    public static String COMMENT_POST_URL = BASE_URL + "/complaint/user/comment";
    public static String COMPLAINT_CLOSE_URL = BASE_URL + "/complaint/user/status";
    public static String UPDATE_PROFILE_URL = BASE_URL + "/mobile/user/profile";
    public static String GET_PROFILE_URL = BASE_URL + "/mobile/user/profile";
    public static String LOCATION_COUNTERS_URL = BASE_URL_2 + "/stats/counter/location";
    public static String LOCATION_COMPLAINTS_URL = BASE_URL + "/complaint/location";
    public static String SAVE_GCM_ID_URL = BASE_URL + "/user/device/gcm";
    public static String GET_SINGLE_COMPLAINT_URL = BASE_URL + "/complaint";
    public static String GET_LOCATION_URL = BASE_URL + "/location";
    public static String GET_LEADER_URL = BASE_URL + "/leader";
    public static String GET_LEADERS_FOR_LOCATION = BASE_URL + "/leaders/location";

    //Google URLS
    public static final String GOOGLE_PLACES_AUTOCOMPLETE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=" + GOOGLE_API_BROWSER_KEY + "&components=country:in&types=geocode&input=";
    public static final String GOOGLE_PLACES_DETAILS_URL= "https://maps.googleapis.com/maps/api/place/details/json?key=" + GOOGLE_API_BROWSER_KEY + "&placeid=";

    public static void setDevMode() {
        BASE_URL = "http://dev.api.eswaraj.com/api/v0";
        BASE_URL_2 = "http://dev.api.eswaraj.com";

        GET_CATEGORIES_URL = BASE_URL + "/categories";
        SAVE_FACEBOOK_USER_URL = BASE_URL + "/user/facebook";
        SAVE_DEVICE_ANONYMOUS_USER_URL = BASE_URL + "/user/device";
        POST_COMPLAINT_URL = BASE_URL + "/complaint";
        USER_COMPLAINTS_URL = BASE_URL + "/user/complaints";
        COMMENT_POST_URL = BASE_URL + "/complaint/user/comment";
        COMPLAINT_CLOSE_URL = BASE_URL + "/complaint/user/status";
        UPDATE_PROFILE_URL = BASE_URL + "/mobile/user/profile";
        GET_PROFILE_URL = BASE_URL + "/mobile/user/profile";
        LOCATION_COUNTERS_URL = BASE_URL_2 + "/stats/counter/location";
        LOCATION_COMPLAINTS_URL = BASE_URL + "/complaint/location";
        SAVE_GCM_ID_URL = BASE_URL + "/user/device/gcm";
        GET_SINGLE_COMPLAINT_URL = BASE_URL + "/complaint";
        GET_LOCATION_URL = BASE_URL + "/location";
        GET_LEADER_URL = BASE_URL + "/leader";
        GET_LEADERS_FOR_LOCATION = BASE_URL + "/leaders/location";
    }

    public static String getCommentsUrl(Long complaintId, int start, int count) {
        return BASE_URL + "/complaint/" + complaintId + "/comments?count=" + count + "&start=" + start + "&order=DESC";
    }

    public static String getLocationCountersPerRootCategoryUrl(Long locationId, Long rootCategoryId) {
        return BASE_URL_2 + "/stats/counter/location/" + locationId + "/parentcategory/" + rootCategoryId;
    }

    public static String getLocationComplaintsUrl(Long locationId, int start, int count) {
        return LOCATION_COMPLAINTS_URL + "/" + locationId + "?count=" + count + "&start=" + start;
    }

    public static String getLeadersUrl(Double lat, Double lng) {
        return BASE_URL + "/leaders?lat=" + lat + "&long=" + lng;
    }

    public static String getGlobalSearchUrl(String query) {
        return BASE_URL + "/search?q=" + query;
    }

    public static String getUserComplaintsUrl(Long userId, int start, int count) {
        return USER_COMPLAINTS_URL + "/" + userId + "?count=" + count + "&start=" + start;
    }
}
