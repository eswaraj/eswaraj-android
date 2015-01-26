package com.eswaraj.app.eswaraj.base;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.eswaraj.app.eswaraj.application.EswarajApplication;

public class BaseFragmentActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((EswarajApplication)getApplication()).inject(this);
    }

}
