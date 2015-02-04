package com.eswaraj.app.eswaraj.util;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class LocationServicesCheckUtil {

    public Boolean isServiceAvailable(Context context) {
        //return isLocationServiceAvailable(context);
        return isAnyProviderAvailable(context);
    }

    private Boolean isLocationServiceAvailable(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        }
        return false;
    }

    private Boolean isAnyProviderAvailable(final Context context) {
        Boolean available = false;
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
            /*
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage("Enable Location Service");
            dialog.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent( Settings.ACTION_SECURITY_SETTINGS );
                    context.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    return;
                }
            });
            dialog.show();
            */

        }

        return true;
    }
}
