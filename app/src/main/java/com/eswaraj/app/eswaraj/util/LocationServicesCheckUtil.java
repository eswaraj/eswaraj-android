package com.eswaraj.app.eswaraj.util;


import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class LocationServicesCheckUtil {

    public Boolean isServiceAvailable(Context context) {
        return isLocationServiceAvailable(context);
    }

    private Boolean isLocationServiceAvailable(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        }
        return false;
    }
}
