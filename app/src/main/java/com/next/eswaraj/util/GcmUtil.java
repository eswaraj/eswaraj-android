package com.next.eswaraj.util;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.next.eswaraj.base.BaseClass;
import com.next.eswaraj.config.Constants;
import com.next.eswaraj.helpers.SharedPreferencesHelper;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;

public class GcmUtil extends BaseClass {

    @Inject
    SharedPreferencesHelper sharedPreferencesHelper;

    private static final String GCM_SENDER_ID = "788694755236";
    private static final String GCM_PREFS_FILE = "GcmPref";
    private static final String GCM_ID = "GcmId";
    private static final String GCM_ID_TIME_IN_MS = "GcmIdTimeInMs";
    private static final String GCM_APP_VERSION = "GcmAppVersion";
    private static final String GCM_SYNCED_WITH_SERVER = "GcmSyncedWithServer";
    private static final int GCM_MAX_RETRIES = 5;

    GoogleCloudMessaging gcm;
    String regid;
    int count = 0;

    private void saveRegistrationId(Context context, String gcmId) {
        sharedPreferencesHelper.putString(context, GCM_PREFS_FILE, GCM_ID, gcmId);
        sharedPreferencesHelper.putLong(context, GCM_PREFS_FILE, GCM_ID_TIME_IN_MS, new Date().getTime());
        sharedPreferencesHelper.putInt(context, GCM_PREFS_FILE, GCM_APP_VERSION, getAppVersion(context));
        sharedPreferencesHelper.putBoolean(context, GCM_PREFS_FILE, GCM_SYNCED_WITH_SERVER, false);
    }

    public Boolean isSyncedWithServer(Context context) {
        return sharedPreferencesHelper.getBoolean(context, GCM_PREFS_FILE, GCM_SYNCED_WITH_SERVER, false);
    }

    public void markSyncedWithServer(Context context) {
        sharedPreferencesHelper.putBoolean(context, GCM_PREFS_FILE, GCM_SYNCED_WITH_SERVER, true);
    }

    public String getRegistrationId(Context context) {
        String registrationId = sharedPreferencesHelper.getString(context, GCM_PREFS_FILE, GCM_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("GcmUtil", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = sharedPreferencesHelper.getInt(context, GCM_PREFS_FILE, GCM_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("GcmUtil", "App version changed.");
            return "";
        }

        if((new Date().getTime() - sharedPreferencesHelper.getLong(context, GCM_PREFS_FILE, GCM_ID_TIME_IN_MS, 0L)) > Constants.SERVER_DATA_UPDATE_INTERVAL_IN_MS) {
            Log.i("GcmUtil", "Stale GCM ID.");
            return "";
        }

        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void registerInBackground(final Context context) {
        count++;
        if(count > GCM_MAX_RETRIES) {
            return;
        }
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                Boolean result;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(GCM_SENDER_ID);
                    result = true;
                    saveRegistrationId(context, regid);
                    Log.d("GcmUtil", "Success: Got GCM id = " + regid);
                } catch (IOException ex) {
                    result = false;
                    try {
                        Thread.sleep(50*(2^count));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    registerInBackground(context);
                    Log.d("GcmUtil", "Failed");
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return result;
            }

        }.execute(null, null, null);
    }

    public void registerWithGcmServerIfNeeded(Context context) {
        if(getRegistrationId(context).isEmpty()) {
            registerInBackground(context);
        }
    }
}
