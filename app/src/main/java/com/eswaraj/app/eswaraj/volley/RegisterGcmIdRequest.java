package com.eswaraj.app.eswaraj.volley;


import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.util.DeviceUtil;
import com.eswaraj.app.eswaraj.util.GcmUtil;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.eswaraj.web.dto.device.RegisterGcmDeviceId;

import javax.inject.Inject;


public class RegisterGcmIdRequest extends BaseClass {

    @Inject
    NetworkAccessHelper networkAccessHelper;
    @Inject
    UserSessionUtil userSession;
    @Inject
    GcmUtil gcmUtil;

    public void processRequest(Context context)
    {
        RegisterGcmDeviceId registerGcmDeviceId = new RegisterGcmDeviceId();
        registerGcmDeviceId.setDeviceId(DeviceUtil.getDeviceid(context));
        registerGcmDeviceId.setUserExternalId(userSession.getUser().getExternalId());
        registerGcmDeviceId.setGcmId(gcmUtil.getRegistrationId(context));

        GenericPostVolleyRequest request = new GenericPostVolleyRequest(Constants.SAVE_GCM_ID_URL, createErrorListener(), createSuccessListener(context), registerGcmDeviceId);
        networkAccessHelper.submitNetworkRequest("PostComment", request);
    }

    private Response.Listener<String> createSuccessListener(final Context context) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RegisterGcmIdRequest", "Response:" + response);
                gcmUtil.markSyncedWithServer(context);
            }
        };
    }


    private Response.ErrorListener createErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RegisterGcmIdRequest", "Registration failed");
            }
        };
    }
}
