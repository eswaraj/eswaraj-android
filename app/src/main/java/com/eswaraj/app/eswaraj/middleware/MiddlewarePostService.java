package com.eswaraj.app.eswaraj.middleware;


import android.content.Context;

import com.eswaraj.web.dto.RegisterFacebookAccountRequest;
import com.eswaraj.web.dto.UserDto;
import com.facebook.Session;

public interface MiddlewarePostService {

    public void registerDevice(Context context);
    public void saveUserLocation(Context context, UserDto userDto, double lat, double lng);
}
