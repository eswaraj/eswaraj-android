package com.next.eswaraj.util;


import android.content.Context;
import android.location.LocationManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class LocationServicesCheckUtil {

    public Boolean isServiceAvailable(Context context) {
        return isLocationServiceAvailable(context);
    }

    private Boolean isLocationServiceAvailable(final Context context) {
        LocationManager lm = null;
        boolean gps_enabled = false, network_enabled = false;

        if(lm == null) {
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}


        if(!gps_enabled && !network_enabled) {
            return false;
        }

        return true;
    }
}
