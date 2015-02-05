package com.next.eswaraj.application;

import android.app.Application;
import android.os.StrictMode;
import android.util.Log;

import com.next.eswaraj.BuildConfig;
import com.next.eswaraj.config.Constants;
import com.next.eswaraj.modules.LocalServiceGraph;
import com.next.eswaraj.modules.MiddlewareGraph;

import java.util.Arrays;

import dagger.ObjectGraph;

public class EswarajApplication extends Application {
    private static ObjectGraph objectGraph;
    private static EswarajApplication instance;

    @Override
    public void onCreate() {
        if (BuildConfig.DEBUG) {
            Log.d("EswarajApplication", "Debug mode enabled");
            Constants.setDevMode();
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .penaltyDialog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }
        super.onCreate();
        instance = this;
        objectGraph = ObjectGraph.create(getModules());
    }

    private Object[] getModules() {
        return Arrays.asList(new LocalServiceGraph(), new MiddlewareGraph(this)).toArray();
    }

    public void inject(Object target) {
        objectGraph.inject(target);
    }

    public static EswarajApplication getInstance() {
        return instance;
    }

}
