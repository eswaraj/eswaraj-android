package com.eswaraj.app.eswaraj.util;

import android.app.Activity;
import android.provider.Settings;

import com.eswaraj.app.eswaraj.base.BaseClass;


public class DeviceUtil extends BaseClass {

    public static String getDeviceid(Activity activity) {
        String android_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public static String getDeviceTypeRef() {
        return "Android";
    }
}
