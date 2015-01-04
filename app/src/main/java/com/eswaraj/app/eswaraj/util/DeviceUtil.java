package com.eswaraj.app.eswaraj.util;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;

import com.eswaraj.app.eswaraj.base.BaseClass;


public class DeviceUtil extends BaseClass {

    public static String getDeviceid(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getDeviceTypeRef() {
        return "Android";
    }
}
