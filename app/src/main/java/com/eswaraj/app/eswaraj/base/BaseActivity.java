package com.eswaraj.app.eswaraj.base;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.eswaraj.app.eswaraj.application.EswarajApplication;
import com.eswaraj.app.eswaraj.helpers.WindowAnimationHelper;

public class BaseActivity extends FragmentActivity {

    protected Boolean dontUseAnimation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((EswarajApplication)getApplication()).inject(this);
    }


    @Override
    public void finish() {
        super.finish();
        if(!dontUseAnimation) {
            WindowAnimationHelper.finish(this);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if(dontUseAnimation) {
            super.startActivityForResult(intent, requestCode);
        }
        else {
            WindowAnimationHelper.startActivityForResultWithSlideFromRight(this, intent, requestCode);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if(dontUseAnimation) {
            super.startActivity(intent);
        }
        else {
            WindowAnimationHelper.startActivityWithSlideFromRight(this, intent);
        }
    }
}
