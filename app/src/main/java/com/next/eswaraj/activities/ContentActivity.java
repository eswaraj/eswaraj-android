package com.next.eswaraj.activities;

import android.content.Intent;
import android.os.Bundle;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.events.BannerClickEvent;
import com.next.eswaraj.fragments.ContentFragment;

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
        setTitle(getResources().getString(R.string.titleContentActivity));

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
