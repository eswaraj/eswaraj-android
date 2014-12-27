package com.eswaraj.app.eswaraj.base;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.eswaraj.app.eswaraj.application.EswarajApplication;
import com.eswaraj.app.eswaraj.helpers.WindowAnimationHelper;

public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((EswarajApplication)getApplication()).inject(this);
    }


    @Override
    public void finish() {
        super.finish();
        WindowAnimationHelper.finish(this);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        WindowAnimationHelper.startActivityForResultWithSlideFromRight(this, intent, requestCode);
    }

    @Override
    public void startActivity(Intent intent) {
        WindowAnimationHelper.startActivityWithSlideFromRight(this, intent);
    }
}
