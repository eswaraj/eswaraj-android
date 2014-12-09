package com.eswaraj.app.eswaraj.base;


import com.eswaraj.app.eswaraj.application.EswarajApplication;

public class BaseClass {
    public BaseClass() {
        EswarajApplication.getInstance().inject(this);
    }
}
