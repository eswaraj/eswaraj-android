package com.eswaraj.app.eswaraj.util;

import android.content.Context;
import android.util.Log;

import com.eswaraj.app.eswaraj.interfaces.FacebookLoginInterface;
import com.eswaraj.app.eswaraj.interfaces.ServerDataInterface;

public class ServerDataUtil {
    private Context context;

    public ServerDataUtil() {
        this.context = null;
    }

    public ServerDataUtil(Context context) {
        this.context = context;
    }

    public void getDataIfNeeded() {
        //TODO:Check the SharedPreferences if data is already available
        //Check the SharedPreferences if older data has exceeded a predefined limit
        //If needed then start a new request for data, else call the callback from here
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
