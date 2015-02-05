package com.next.eswaraj.util;

import android.content.Context;
import android.provider.Settings;

import com.next.eswaraj.base.BaseClass;


public class DeviceUtil extends BaseClass {

    public static String getDeviceid(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getDeviceTypeRef() {
        return "Android";
    }
}
