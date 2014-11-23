package com.eswaraj.app.eswaraj.handlers;


import android.content.Context;
import android.util.Log;
import android.view.View;

import com.eswaraj.app.eswaraj.interfaces.FacebookLoginInterface;
import com.eswaraj.app.eswaraj.interfaces.LoginSkipInterface;

public class SkipButtonClickHandler implements View.OnClickListener{

    private Context context;

    public SkipButtonClickHandler() {
        this.context = null;
    }

    public SkipButtonClickHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        try {
            ((LoginSkipInterface) context).onSkipDone();
        }
        catch (ClassCastException e) {
            Log.e("Interface not implemented", "The activity should implement LoginSkipInterface");
            e.printStackTrace();
        }
    }
}
