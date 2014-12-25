package com.eswaraj.app.eswaraj.base;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.eswaraj.app.eswaraj.application.EswarajApplication;

public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((EswarajApplication)getApplication()).inject(this);
    }

}
