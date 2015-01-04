package com.eswaraj.app.eswaraj.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class GcmBroadcastReceiver extends BroadcastReceiver {
    public GcmBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Got message from eSwaraj", Toast.LENGTH_LONG).show();
    }
}
