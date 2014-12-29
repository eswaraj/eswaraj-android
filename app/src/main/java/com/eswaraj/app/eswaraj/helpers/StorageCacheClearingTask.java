package com.eswaraj.app.eswaraj.helpers;


import android.content.Context;
import android.os.AsyncTask;

import com.eswaraj.app.eswaraj.application.EswarajApplication;
import com.eswaraj.app.eswaraj.datastore.StorageCache;
import com.eswaraj.app.eswaraj.events.CacheClearedEvent;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class StorageCacheClearingTask extends AsyncTask<Void, Void, Void> {

    @Inject
    EventBus eventBus;
    @Inject
    StorageCache storageCache;

    private Context context;

    public StorageCacheClearingTask(Context context) {
        this.context = context;
        EswarajApplication.getInstance().inject(this);
    }

    @Override
    protected Void doInBackground(Void... params) {
        if(isCancelled()) {
            return null;
        }
        storageCache.clear(context);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        CacheClearedEvent event = new CacheClearedEvent();
        event.setSuccess(true);
        eventBus.post(event);
    }
}
