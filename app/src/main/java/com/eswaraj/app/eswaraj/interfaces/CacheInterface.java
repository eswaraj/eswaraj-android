package com.eswaraj.app.eswaraj.interfaces;

import android.content.Context;
import android.os.Bundle;

import com.eswaraj.app.eswaraj.config.ServerAccessEnums;

public interface CacheInterface {

    public void onNewDataFetched(Context context, ServerAccessEnums resource, Bundle bundle);
}
