package com.eswaraj.app.eswaraj.util;

import android.content.Context;

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
        //If needed then start a new request for data, else return
        return;
    }
}
