package com.next.eswaraj.base;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.next.eswaraj.application.EswarajApplication;

public class BaseFragmentActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((EswarajApplication)getApplication()).inject(this);
    }

}
