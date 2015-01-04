package com.eswaraj.app.eswaraj.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.eswaraj.app.eswaraj.application.EswarajApplication;

public class BaseService extends Service {
    public BaseService() {
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
