package com.eswaraj.app.eswaraj.helpers;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.eswaraj.app.eswaraj.R;

public class NotificationHelper {

    public void sendNotification(Context caller, Class<?> activityToLaunch, String title, String msg, int id) {
        Intent toLaunch;
        if(activityToLaunch != null) {
            toLaunch = new Intent(caller, activityToLaunch);
        }
        else {
            toLaunch = new Intent();
        }
        PendingIntent intentBack = PendingIntent.getActivity(caller, 0, toLaunch, 0);

        NotificationManager notifier = (NotificationManager) caller.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(caller)
                        .setSmallIcon(android.R.drawable.ic_notification_overlay)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setAutoCancel(true)
                        .setContentIntent(intentBack);

        notifier.notify(id, mBuilder.build());
    }
}
