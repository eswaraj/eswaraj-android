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
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.web.dto.RegisterFacebookAccountRequest;

import org.json.JSONArray;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class Server implements ServerInterface {

    @Inject
    private NetworkAccessHelper networkAccessHelper;
    @Inject
    private CacheInterface cache;
    @Inject
    private EventBus eventBus;


    public void loadCategoriesData(Context context) {
        JsonArrayRequest request = new JsonArrayRequest(Constants.GET_CATEGORIES_URL, createCategoriesDataReqSuccessListener(context), createReqErrorListener(context));
        this.networkAccessHelper.submitNetworkRequest("GetCategories", request);
    }


    private Response.ErrorListener createReqErrorListener(final Context context) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(context, R.string.getDataError, Toast.LENGTH_LONG).show();
                //Log.e("Server", "Unable to connect to service", error);
                //TODO: Create LoadCategoryEvent and set success=false, error=error and publish the event on eventBus
            }
        };
    }

    private Response.Listener<JSONArray> createCategoriesDataReqSuccessListener(final Context context) {
        return new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                //TODO: Parse the data. If data if fine then create CategoryDto. Create LoadCategory event, success=true and publish on eventBus
                //Update the cache
                cache.updateCategoriesData(context, jsonArray.toString());
            }
        };
    }


    @Override
    public void registerFacebookUser(Context context, RegisterFacebookAccountRequest request) {
        //TODO: Implement logic
    }
}
