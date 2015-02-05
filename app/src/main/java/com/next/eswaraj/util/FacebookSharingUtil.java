package com.next.eswaraj.util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.next.eswaraj.base.BaseClass;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class FacebookSharingUtil extends BaseClass {

    @Inject
    Context applicationContext;
    @Inject
    EventBus eventBus;

    private UiLifecycleHelper uiHelper;

    public void onCreate(Activity context, Bundle savedInstanceState) {
        uiHelper = new UiLifecycleHelper(context, null);
        uiHelper.onCreate(savedInstanceState);
    }

    public void onResume() {
        uiHelper.onResume();
    }

    public void onSaveInstanceState(Bundle outState) {
        uiHelper.onSaveInstanceState(outState);
    }

    public void onPause() {
        uiHelper.onPause();
    }

    public void onDestroy() {
        uiHelper.onDestroy();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
    }

    public void shareLink(Activity context, String link) {
        if (FacebookDialog.canPresentShareDialog(applicationContext, FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
            FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(context)
                    .setName("eSwaraj for Android")
                    .setLink(link)
                    .build();
            uiHelper.trackPendingDialogCall(shareDialog.present());
        }
        else {
            publishFeedDialog(context, null, null, null, link);
        }
    }

    public void shareComplaintWithImage(Activity context, String caption, String description, String imageUrl, String link) {
        if (FacebookDialog.canPresentShareDialog(applicationContext, FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
            FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(context)
                    .setName("eSwaraj for Android")
                    .setCaption(caption)
                    .setDescription(description)
                    .setPicture(imageUrl)
                    .setLink(link)
                    .build();
            uiHelper.trackPendingDialogCall(shareDialog.present());
        }
        else {
            publishFeedDialog(context, caption, description, imageUrl, link);
        }
    }

    public void shareComplaint(Activity context, String caption, String description, String link) {
        if (FacebookDialog.canPresentShareDialog(applicationContext, FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
            FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(context)
                    .setName("eSwaraj for Android")
                    .setCaption(caption)
                    .setDescription(description)
                    .setPicture("http://dev.eswaraj.com/images/eswaraj-dashboard-logo.png")
                    .setLink(link)
                    .build();
            uiHelper.trackPendingDialogCall(shareDialog.present());
        }
        else {
            publishFeedDialog(context, caption, description, null, link);
        }
    }

    private void publishFeedDialog(final Activity context, String caption, String description, String picture, String link) {
        Bundle params = new Bundle();
        params.putString("name", "eSwaraj for Android");
        if(caption != null) {
            params.putString("caption", caption);
        }
        if(description != null) {
            params.putString("description", description);
        }
        if(link != null) {
            params.putString("link", link);
        }
        if(picture != null) {
            params.putString("picture", picture);
        }
        else {
            params.putString("picture", "http://dev.eswaraj.com/images/eswaraj-dashboard-logo.png");
        }

        WebDialog feedDialog = (
                new WebDialog.FeedDialogBuilder(context, Session.getActiveSession(), params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values,
                                           FacebookException error) {
                        if (error == null) {
                            // When the story is posted, echo the success and the post Id.
                            final String postId = values.getString("post_id");
                            if (postId != null) {
                                Toast.makeText(context, "Posted story, id: " + postId, Toast.LENGTH_SHORT).show();
                            } else {
                                // User clicked the Cancel button
                                Toast.makeText(context, "Publish cancelled", Toast.LENGTH_SHORT).show();
                            }
                        } else if (error instanceof FacebookOperationCanceledException) {
                            // User clicked the "x" button
                            Toast.makeText(context, "Publish cancelled", Toast.LENGTH_SHORT).show();
                        } else {
                            // Generic, ex: network error
                            Toast.makeText(context, "Error posting story", Toast.LENGTH_SHORT).show();
                        }
                    }

                })
                .build();
        feedDialog.show();
    }
}
