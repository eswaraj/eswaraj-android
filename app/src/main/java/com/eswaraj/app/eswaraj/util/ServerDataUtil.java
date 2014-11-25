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
import com.eswaraj.app.eswaraj.config.ServerAccessEnums;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.datastore.Server;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.helpers.SharedPreferencesHelper;
import com.eswaraj.app.eswaraj.interfaces.DatastoreInterface;
import com.eswaraj.app.eswaraj.interfaces.ServerDataInterface;

import org.json.JSONArray;

import java.util.Date;

public class ServerDataUtil {
    private DatastoreInterface context;
    private Cache cache;
    private Server server;

    public ServerDataUtil() {
        this.context = null;
    }

    public ServerDataUtil(DatastoreInterface context) {
        this.context = context;
        this.cache = Cache.getInstance();
        this.server = Server.getInstance();
    }

    public void getData(ServerAccessEnums resource, Boolean dontGetFromCache) {
        if(dontGetFromCache) {
            server.getDataFromServer(this.context, resource);
        }
        else {
            if(cache.isDataInCache(this.context, resource)) {
                cache.getDataInCache(this.context, resource);
            }
            else {
                server.getDataFromServer(this.context, resource);
            }
        }
    }
}
