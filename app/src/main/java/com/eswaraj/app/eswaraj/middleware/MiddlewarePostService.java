package com.eswaraj.app.eswaraj.middleware;


import android.content.Context;

import com.eswaraj.web.dto.RegisterFacebookAccountRequest;

public interface MiddlewarePostService {

    public void registerFacebookUser(Context context, RegisterFacebookAccountRequest request);
}
