package com.eswaraj.app.eswaraj.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.base.BaseService;
import com.eswaraj.app.eswaraj.events.DatabaseComplaintPostEvent;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class ComplaintPostService extends BaseService {

    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    EventBus eventBus;

    public ComplaintPostService() {
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
        middlewareService.postOneComplaint();
        return Service.START_STICKY;
    }

    public void onEventMainThread(DatabaseComplaintPostEvent event) {
        if(event.getSuccess()) {
            if(event.getEnd()) {
                stopSelf();
            }
            else {
                middlewareService.postOneComplaint();
            }
        }
        else {
            middlewareService.postOneComplaint();
        }
    }
}
