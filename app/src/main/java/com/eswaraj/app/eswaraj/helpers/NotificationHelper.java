package com.eswaraj.app.eswaraj.helpers;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.activities.SingleComplaintActivity;
import com.eswaraj.app.eswaraj.models.GcmMessageDto;

import java.util.Date;

public class NotificationHelper {

    public void sendNotification(Context caller, Class<?> activityToLaunch, String title, String msg, Bitmap icon, int id) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
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

        if(icon != null) {
            mBuilder.setLargeIcon(icon);
        }
        mBuilder.setSound(alarmSound);
        notifier.notify(id, mBuilder.build());
    }

    public void sendNotification(Context caller, Bitmap icon, GcmMessageDto gcmMessageDto, int id) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        RemoteViews remoteViews = new RemoteViews(caller.getPackageName(), R.layout.notification);
        Intent toLaunch;
        toLaunch = new Intent(caller, SingleComplaintActivity.class);
        toLaunch.putExtra("DATA_PRESENT", false);
        toLaunch.putExtra("COMPLAINT_ID", gcmMessageDto.getComplaintId());

        PendingIntent intentBack = PendingIntent.getActivity(caller, 0, toLaunch, 0);

        NotificationManager notifier = (NotificationManager) caller.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(caller)
                        .setSmallIcon(android.R.drawable.ic_notification_overlay)
                        .setContentIntent(intentBack);

        remoteViews.setTextViewText(R.id.nMessage, gcmMessageDto.getMessage());
        if(icon != null) {
            remoteViews.setImageViewBitmap(R.id.nImage, icon);
        }
        mBuilder.setContent(remoteViews);
        mBuilder.setSound(alarmSound);

        notifier.notify(id, mBuilder.build());
    }

    public void sendComplaintUpdateNotification(Context caller, String msg, Bitmap icon, int id, Long complaintId) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent toLaunch;
        toLaunch = new Intent(caller, SingleComplaintActivity.class);
        toLaunch.putExtra("DATA_PRESENT", false);
        toLaunch.putExtra("COMPLAINT_ID", complaintId);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(caller);
        stackBuilder.addParentStack(SingleComplaintActivity.class);
        stackBuilder.addNextIntent(toLaunch);

        PendingIntent intentBack = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //PendingIntent intentBack = PendingIntent.getActivity(caller, 0, toLaunch, 0);

        NotificationManager notifier = (NotificationManager) caller.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(caller)
                        .setSmallIcon(android.R.drawable.ic_notification_overlay)
                        .setContentTitle("eSwaraj: Your voice is heard")
                        .setContentText(msg)
                        .setAutoCancel(true)
                        .setContentIntent(intentBack);

        if(icon != null) {
            mBuilder.setLargeIcon(icon);
        }
        mBuilder.setSound(alarmSound);
        notifier.notify(id, mBuilder.build());
    }
}
