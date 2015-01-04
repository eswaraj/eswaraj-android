package com.eswaraj.app.eswaraj.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.services.ComplaintPostService;

public class Internet extends BroadcastReceiver {
    public Internet() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if(networkInfo.isConnected()) {
                // Wifi is connected
                Log.d("Inetify", "Wifi is connected: " + String.valueOf(networkInfo));
                Intent i = new Intent(context, ComplaintPostService.class);
                context.startService(i);
            }
        } else if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI && !networkInfo.isConnected()) {
                // Wifi is disconnected
                Log.d("Inetify", "Wifi is disconnected: " + String.valueOf(networkInfo));
            }
        }
    }
}
