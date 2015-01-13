package com.eswaraj.app.eswaraj.broadcast_receivers;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.services.GcmService;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    public GcmBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName comp = new ComponentName(context.getPackageName(), GcmService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
