package com.next.eswaraj.activities;

import android.content.Intent;
import android.os.Bundle;

import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.config.Constants;
import com.next.eswaraj.helpers.GoogleAnalyticsTracker;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import android.widget.Toast;

import com.next.eswaraj.R;

import java.util.Date;

import javax.inject.Inject;

public class YoutubeActivity extends BaseActivity implements YouTubePlayer.OnInitializedListener {

    @Inject
    GoogleAnalyticsTracker googleAnalyticsTracker;

    private YouTubePlayerSupportFragment youTubePlayerSupportFragment;
    private String video;
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private Long startTime;
    private Long endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        startTime = new Date().getTime();

        youTubePlayerSupportFragment = YouTubePlayerSupportFragment.newInstance();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.youtube, youTubePlayerSupportFragment).commit();
        }
        youTubePlayerSupportFragment.initialize(Constants.GOOGLE_API_KEY, this);

        Intent i = getIntent();
        video = i.getStringExtra("VIDEO_ID");
    }

    @Override
    protected void onDestroy() {
        endTime = new Date().getTime();
        googleAnalyticsTracker.trackTimeSpent("Youtube", video, endTime - startTime);
        super.onDestroy();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if(!wasRestored) {
            if(video != null) {
                startTime = new Date().getTime();
                youTubePlayer.cueVideo(video);
                youTubePlayer.play();
            }
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format("There was an error initializing the YouTubePlayer (%1$s)", youTubeInitializationResult.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            youTubePlayerSupportFragment.initialize(Constants.YOUTUBE_API_KEY, this);
        }
    }
}
