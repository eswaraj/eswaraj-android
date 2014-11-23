package com.eswaraj.app.eswaraj.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.eswaraj.app.eswaraj.config.ServicesEnums;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class ServicesCheckUtil {

    private Context context;

    public ServicesCheckUtil() {
        this.context = null;
    }

    public ServicesCheckUtil(Context context) {
        this.context = context;
    }

    public Boolean isServiceAvailable(ServicesEnums service) {
        switch (service) {
            case INTERNET: return isInternetServiceAvailable();
            case LOCATION: return isLocationServiceAvailable();
            default: return false;
        }
    }

    private Boolean isInternetServiceAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private Boolean isLocationServiceAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.context);
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        }
        return false;
    }
}
