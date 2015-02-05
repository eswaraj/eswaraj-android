package com.next.eswaraj.modules;

import com.next.eswaraj.datastore.Server;
import com.next.eswaraj.datastore.ServerInterface;
import com.next.eswaraj.middleware.MiddlewareServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {MiddlewareServiceImpl.class},
        complete = false,
        library = true
)
public class LocalServiceGraph {

    @Provides @Singleton
    ServerInterface provideServer() {
        return new Server();
    }
}
