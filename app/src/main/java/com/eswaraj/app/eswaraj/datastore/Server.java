package com.eswaraj.app.eswaraj.datastore;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.config.PreferenceConstants;
import com.eswaraj.app.eswaraj.config.ServerAccessEnums;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.interfaces.CacheInterface;
import com.eswaraj.app.eswaraj.interfaces.DatastoreInterface;

import org.json.JSONArray;


public class Server {

    private NetworkAccessHelper networkAccessHelper;
    private CacheInterface cache;
    private static Server server = new Server();

    public static Server getInstance( ) {
        return server;
    }

    private Server() {
        this.networkAccessHelper = NetworkAccessHelper.getInstance();
        this.cache = Cache.getInstance();
    }

    public void getDataFromServer(DatastoreInterface context, ServerAccessEnums resource) {
        switch (resource) {
            case GET_CATEGORIES: getCategoriesDataFromServer(context); break;
            case GET_LOGGED_IN_USER_DTO: getLoggedInUserDtoFromServer(context); break;
            default: break;
        }
    }

    private void getCategoriesDataFromServer(DatastoreInterface context) {
        JsonArrayRequest request = new JsonArrayRequest(Constants.GET_CATEGORIES_URL, createCategoriesDataReqSuccessListener(context), createReqErrorListener(context));
        this.networkAccessHelper.submitNetworkRequest("GetCategories", request);
    }

    private void getLoggedInUserDtoFromServer(DatastoreInterface context) {
        //TODO: Create a volley request here and pass context to it
    }

    private Response.ErrorListener createReqErrorListener(final DatastoreInterface context) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText((Context) context, R.string.getDataError, Toast.LENGTH_LONG).show();
                Log.e("Server", "Unable to connect to service", error);
            }
        };
    }

    private Response.Listener<JSONArray> createCategoriesDataReqSuccessListener(final DatastoreInterface context) {
        return new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                //Notify the activity
                Bundle bundle = new Bundle();
                bundle.putString("data", jsonArray.toString());
                context.onDataAvailable(ServerAccessEnums.GET_CATEGORIES, bundle);
                //Update the cache
                cache.onNewDataFetched((Context) context, ServerAccessEnums.GET_CATEGORIES, bundle);
            }
        };
    }
}
