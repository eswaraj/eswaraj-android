package com.eswaraj.app.eswaraj.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.events.BannerClickEvent;
import com.eswaraj.app.eswaraj.fragments.ContentFragment;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class ContentActivity extends BaseActivity {

    @Inject
    EventBus eventBus;

    private ContentFragment contentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        contentFragment = (ContentFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragment);

        eventBus.register(this);
    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(BannerClickEvent event) {
        Intent i = new Intent(this, YoutubeActivity.class);
        i.putExtra("VIDEO_ID", event.getVideo());
        startActivity(i);
    }
}
