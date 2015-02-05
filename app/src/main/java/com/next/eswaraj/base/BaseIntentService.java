package com.next.eswaraj.base;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

import com.next.eswaraj.application.EswarajApplication;


public class BaseIntentService extends IntentService {

    public BaseIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((EswarajApplication)getApplication()).inject(this);
    }
}
