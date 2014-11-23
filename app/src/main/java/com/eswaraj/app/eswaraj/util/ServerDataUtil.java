package com.eswaraj.app.eswaraj.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.config.PreferenceConstants;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.helpers.SharedPreferencesHelper;
import com.eswaraj.app.eswaraj.interfaces.ServerDataInterface;

import org.json.JSONArray;

import java.util.Date;

public class ServerDataUtil {
    private Context context;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private NetworkAccessHelper networkAccessHelper;

    public ServerDataUtil() {
        this.context = null;
    }

    public ServerDataUtil(Context context, SharedPreferencesHelper sharedPreferencesHelper) {
        this.context = context;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
        this.networkAccessHelper = NetworkAccessHelper.getInstance(this.context);
    }

    public void getDataIfNeeded() {
        //Check the SharedPreferences if data is already available
        //Check the SharedPreferences if older data has exceeded a predefined limit
        //If needed then start a new request for data, else call the callback from here
        if(sharedPreferencesHelper.getBoolean(PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.DATA_AVAILABLE, false)) {
            if((new Date().getTime() - sharedPreferencesHelper.getLong(PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.DATA_DOWNLOAD_TIME_IN_MS, 0L)) < Constants.SERVER_DATA_UPDATE_INTERVAL_IN_MS) {
                //Data already present and not stale yet. Call the callback function and return
                if(this.context != null) {
                    try {
                        ((ServerDataInterface) context).onServerDataAvailable();
                    }
                    catch (ClassCastException e) {
                        Log.e("Interface not implemented", "The activity should implement ServerDataInterface");
                        e.printStackTrace();
                    }
                }
                return;
            }
        }
        //If we reach here, then it meant that either data was not available or was stale. Hence need to start the data download thread here
        //Once data is fetched, it needs to be saved in SharedPreferences and data download time needs to be updated
        JsonArrayRequest request = new JsonArrayRequest(Constants.GET_CATEGORIES_URL, createCategoryReqSuccessListener(), createMyReqErrorListener());
        networkAccessHelper.submitNetworkRequest("GetCategories", request);
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, R.string.getDataError, Toast.LENGTH_LONG).show();
                Log.e("ServerDataUtil", "Unable to connect to service", error);
            }
        };
    }

    private Response.Listener<JSONArray> createCategoryReqSuccessListener() {
        return new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                sharedPreferencesHelper.putString(PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.SERVER_CATEGORY_DATA, jsonArray.toString());
                sharedPreferencesHelper.putLong(PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.DATA_DOWNLOAD_TIME_IN_MS, new Date().getTime());
                sharedPreferencesHelper.putBoolean(PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.DATA_AVAILABLE, true);
            }
        };
    }
}
