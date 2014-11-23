package com.eswaraj.app.eswaraj.util;

import android.content.Context;
import android.util.Log;

import com.eswaraj.app.eswaraj.interfaces.DeviceRegisterInterface;
import com.eswaraj.app.eswaraj.interfaces.FacebookLoginInterface;

public class DeviceUtil {
    private Context context;

    public DeviceUtil() {
        this.context = null;
    }

    public DeviceUtil(Context context) {
        this.context = context;
    }

    public void startDeviceRegistration() {
        //Implement device registration logic here
        //Check SharedPreferences for externalId. If present, just call the callback function. Else start volley and call the function when volley finishes
        if(this.context != null) {
            try {
                ((DeviceRegisterInterface) context).onDeviceRegistered();
            }
            catch (ClassCastException e) {
                Log.e("Interface not implemented", "The activity should implement DeviceRegisterInterface");
                e.printStackTrace();
            }
        }
        return;
    }
}
