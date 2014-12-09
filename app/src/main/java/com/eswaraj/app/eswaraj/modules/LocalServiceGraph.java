package com.eswaraj.app.eswaraj.modules;

import com.eswaraj.app.eswaraj.activities.SplashActivity;
import com.eswaraj.app.eswaraj.datastore.Server;
import com.eswaraj.app.eswaraj.datastore.ServerInterface;
import com.eswaraj.app.eswaraj.fragments.SplashFragment;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;

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
