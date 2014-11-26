package com.eswaraj.app.eswaraj.middleware;

import android.content.Context;

import com.eswaraj.app.eswaraj.datastore.CacheInterface;
import com.eswaraj.app.eswaraj.datastore.ServerInterface;
import com.eswaraj.web.dto.RegisterFacebookAccountRequest;

import javax.inject.Inject;

public class MiddlewareServiceImpl implements MiddlewareService {
    @Inject
    private CacheInterface cache;
    @Inject
    private ServerInterface server;

    @Override
    public void loadCategoriesData(Context context) {
        if(cache.isCategoriesDataAvailable(context)) {
            cache.loadCategoriesData(context);
        }
        else {
            server.loadCategoriesData(context);
        }
    }

    @Override
    public void loadCategoriesData(Context context, Boolean dontGetFromCache) {
        if(dontGetFromCache) {
            server.loadCategoriesData(context);
        }
        else {
            if (cache.isCategoriesDataAvailable(context)) {
                cache.loadCategoriesData(context);
            }
            else {
                server.loadCategoriesData(context);
            }
        }
    }

    @Override
    public void registerFacebookUser(Context context, RegisterFacebookAccountRequest request) {
        server.registerFacebookUser(context, request);
    }
}
