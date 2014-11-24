package com.eswaraj.app.eswaraj.util;

import android.content.Context;
import android.util.Log;

import com.eswaraj.app.eswaraj.interfaces.DeviceRegisterInterface;
import com.eswaraj.app.eswaraj.interfaces.FacebookLoginInterface;

public class DeviceUtil {
    private DeviceRegisterInterface context;

    public DeviceUtil() {
        this.context = null;
    }

    public DeviceUtil(DeviceRegisterInterface context) {
        this.context = context;
    }

    public void startDeviceRegistration() {
        //Implement device registration logic here
        //Check SharedPreferences for externalId. If present, just call the callback function. Else start volley and call the function when volley finishes
        if(this.context != null) {
                this.context.onDeviceRegistered();
        }
        return;
    }
}
