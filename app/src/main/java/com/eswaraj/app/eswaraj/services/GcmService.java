package com.eswaraj.app.eswaraj.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.base.BaseIntentService;
import com.eswaraj.app.eswaraj.base.BaseService;
import com.eswaraj.app.eswaraj.broadcast_receivers.GcmBroadcastReceiver;
import com.eswaraj.app.eswaraj.datastore.Cache;
import com.eswaraj.app.eswaraj.datastore.Server;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.GetProfileEvent;
import com.eswaraj.app.eswaraj.events.GetProfileImageEvent;
import com.eswaraj.app.eswaraj.events.GetUserEvent;
import com.eswaraj.app.eswaraj.events.LoginStatusEvent;
import com.eswaraj.app.eswaraj.helpers.NotificationHelper;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.GcmMessageDto;
import com.eswaraj.app.eswaraj.util.GlobalSessionUtil;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.eswaraj.web.dto.device.NotificationMessage;
import com.facebook.Session;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class GcmService extends BaseService {

    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    EventBus eventBus;
    @Inject
    NotificationHelper notificationHelper;
    @Inject
    Cache cache;
    @Inject
    Server server;
    @Inject
    UserSessionUtil userSession;
    @Inject
    GlobalSessionUtil globalSession;

    private final String TAG = "GcmService";
    private GcmMessageDto gcmMessageDto;
    private Intent i;

    public GcmService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Started GcmService", Toast.LENGTH_LONG).show();
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        i = intent;

        if(!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //Do something
                GcmBroadcastReceiver.completeWakefulIntent(i);
                stopSelf();
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                //Do something
                GcmBroadcastReceiver.completeWakefulIntent(i);
                stopSelf();
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                String message = intent.getExtras().getString("MESSAGE");
                String appMessageType = intent.getExtras().getString("MESSAGE_TYPE");

                if(appMessageType.equals("POLITICAL_ADMIN_VIEW") || appMessageType.equals("POLITICAL_ADMIN_COMMENTED")) {
                    gcmMessageDto = new Gson().fromJson(message, GcmMessageDto.class);
                    if(gcmMessageDto != null) {
                        cache.loadUserData(this, Session.getActiveSession());
                    }
                    else {
                        Log.e(TAG, "Invalid json. json = " + message);
                        GcmBroadcastReceiver.completeWakefulIntent(i);
                        stopSelf();
                    }
                }
                else if(appMessageType.equals("USER_UPDATED_ON_WEB")) {
                    Session session = Session.getActiveSession();
                    if(session == null || !session.isOpened()) {
                        session = Session.openActiveSessionFromCache(this);
                    }
                    if (session != null && session.getAccessToken() != null) {
                        server.loadProfileUpdates(this, session.getAccessToken());
                    } else {
                        cache.setUserDataStale(this);
                    }
                }
                else {
                    GcmBroadcastReceiver.completeWakefulIntent(i);
                    stopSelf();
                }
            }
        }
        else {
            GcmBroadcastReceiver.completeWakefulIntent(i);
            stopSelf();
        }

        return Service.START_STICKY;
    }

    public void onEventMainThread(GetProfileImageEvent event) {
        if(event.getSuccess()) {
            if(gcmMessageDto != null) {
                if (event.getId().equals(gcmMessageDto.getViewedBy().getId())) {
                    notificationHelper.sendComplaintUpdateNotification(this, gcmMessageDto.getMessage(), event.getBitmap(), 8888, gcmMessageDto.getComplaintId());
                    //notificationHelper.sendNotification(this, null, event.getBitmap(), gcmMessageDto, 8888);
                }
            }
        }
        else {
            notificationHelper.sendComplaintUpdateNotification(this, gcmMessageDto.getMessage(), null, 8888, gcmMessageDto.getComplaintId());
            //notificationHelper.sendNotification(this, null, null, gcmMessageDto, 8888);
        }
        GcmBroadcastReceiver.completeWakefulIntent(i);
        stopSelf();
    }

    public void onEventMainThread(GetCategoriesDataEvent event) {
        if(event.getSuccess()) {
            globalSession.setCategoryDtoList(event.getCategoryList());
            middlewareService.loadProfileImage(this, gcmMessageDto.getViewedBy().getProfilePhoto(), gcmMessageDto.getViewedBy().getId(), false, false);
        }
        else {
            GcmBroadcastReceiver.completeWakefulIntent(i);
            stopSelf();
        }
    }

    public void onEventMainThread(GetUserEvent event) {
        if(event.getSuccess()) {
            if(event.getUserDto() != null) {
                userSession.setUser(event.getUserDto());
                userSession.setToken(event.getToken());
                cache.loadCategoriesData(this);

            }
            else {
                //User not logged in. No need to do anything
                GcmBroadcastReceiver.completeWakefulIntent(i);
                stopSelf();
            }
        }
        else {
            //User not logged in. No need to do anything
            GcmBroadcastReceiver.completeWakefulIntent(i);
            stopSelf();
        }
    }

    public void onEventMainThread(GetProfileEvent event) {
        if(event.getSuccess()) {
            userSession.setUser(event.getUserDto());
            userSession.setToken(event.getToken());
            GcmBroadcastReceiver.completeWakefulIntent(i);
            stopSelf();
        }
        else {
            cache.setUserDataStale(this);
            GcmBroadcastReceiver.completeWakefulIntent(i);
            stopSelf();
        }
    }
}
