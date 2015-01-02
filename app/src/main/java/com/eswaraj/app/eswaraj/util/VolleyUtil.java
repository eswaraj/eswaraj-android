package com.eswaraj.app.eswaraj.util;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.datastore.LruBitmapCache;

import javax.inject.Inject;

public class VolleyUtil extends BaseClass {

    @Inject
    RequestQueue requestQueue;
    @Inject
    LruBitmapCache lruBitmapCache;

    private ImageLoader imageLoader;

    public VolleyUtil() {
        imageLoader = new ImageLoader(requestQueue, lruBitmapCache);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
