package com.eswaraj.app.eswaraj.modules;

import com.eswaraj.app.eswaraj.datastore.Server;
import com.eswaraj.app.eswaraj.datastore.ServerInterface;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class LocalServiceGraph {

    @Provides @Singleton
    ServerInterface provideServerApi() {
        return new Server();
    }
}
