package com.eswaraj.app.eswaraj.helpers;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NetworkAccessHelper {
    private static NetworkAccessHelper networkAccessHelper = new NetworkAccessHelper();
    private RequestQueue requestQueue;
    private Context context;

    private NetworkAccessHelper() {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static NetworkAccessHelper getInstance() {
        networkAccessHelper.context = null;
        return networkAccessHelper;
    }

    public static NetworkAccessHelper getInstance(Context context) {
        networkAccessHelper.context = context;
        return networkAccessHelper;
    }

    public void submitNetworkRequest(String requestTag, Request request) {
        requestQueue.cancelAll(requestTag);
        requestQueue.add(request);
        request.setTag(requestTag);
    }
}
