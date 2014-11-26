package com.eswaraj.app.eswaraj.application;

import android.app.Application;

import com.eswaraj.app.eswaraj.modules.LocalServiceGraph;
import com.eswaraj.app.eswaraj.modules.MiddlewareGraph;

import java.util.Arrays;

import dagger.ObjectGraph;

public class EswarajApplication extends Application{
    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        objectGraph = ObjectGraph.create(getModules());
    }

    private Object[] getModules() {
        return Arrays.asList(new LocalServiceGraph(), new MiddlewareGraph(this)).toArray();
    }

    public void inject(Object target) {
        objectGraph.inject(target);
    }
}
