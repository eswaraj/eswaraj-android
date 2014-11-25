package com.eswaraj.app.eswaraj.interfaces;

import android.os.Bundle;

import com.eswaraj.app.eswaraj.config.ServerAccessEnums;

public interface DatastoreInterface {

    public void onDataAvailable(ServerAccessEnums resource, Bundle bundle);
}
