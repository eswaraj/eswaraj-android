package com.eswaraj.app.eswaraj.base;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.activities.MyProfileActivity;
import com.eswaraj.app.eswaraj.activities.SplashActivity;
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

    public void setupMenu(View menu) {
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });
    }

    public void showMenu(View view) {
        PopupMenu menu = new PopupMenu(this, view);
        menu.getMenuInflater().inflate(R.menu.menu, menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i;
                switch (item.getItemId()){
                    case R.id.menu_profile:
                        i = new Intent(getBaseContext(), MyProfileActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.menu_splash:
                        i = new Intent(getBaseContext(), SplashActivity.class);
                        i.putExtra("MODE", true);
                        startActivity(i);
                        return true;
                    default:
                        return false;

                }
            }
        });
        menu.show();
    }
}
