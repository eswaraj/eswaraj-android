package com.next.eswaraj.base;


import com.next.eswaraj.application.EswarajApplication;

public class BaseClass {
    public BaseClass() {
        EswarajApplication.getInstance().inject(this);
    }
}
