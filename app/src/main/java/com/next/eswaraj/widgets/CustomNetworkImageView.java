package com.next.eswaraj.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.Toast;

import com.next.eswaraj.application.EswarajApplication;
import com.next.eswaraj.events.GetComplaintImageEvent;
import com.next.eswaraj.events.GetHeaderImageEvent;
import com.next.eswaraj.events.GetProfileImageEvent;
import com.next.eswaraj.middleware.MiddlewareServiceImpl;
import com.makeramen.RoundedImageView;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class CustomNetworkImageView extends RoundedImageView {

    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    EventBus eventBus;

    private Long id;
    private Boolean registered = false;
    private Bitmap bitmap;

    public CustomNetworkImageView(Context context) {
        super(context);
        if(!isInEditMode()) {
            EswarajApplication.getInstance().inject(this);
        }
    }

    public CustomNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            EswarajApplication.getInstance().inject(this);
        }
    }

    public CustomNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(!isInEditMode()) {
            EswarajApplication.getInstance().inject(this);
        }
    }

    public void loadProfileImage(String url, Long id) {
        if(!registered) {
            eventBus.register(this);
            this.id = id;
            middlewareService.loadProfileImage(getContext(), url, id, false);
            registered = true;
        }
        else {
            if(bitmap != null) {
                setImageBitmap(bitmap);
            }
        }
    }

    public void onEventMainThread(GetProfileImageEvent event) {
        if(event.getId().equals(id)) {
            if (event.getSuccess()) {
                setImageBitmap(event.getBitmap());
                bitmap = event.getBitmap();
            } else {
                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT);
            }
            eventBus.unregister(this);
        }
    }

    public void loadComplaintImage(String url, Long id) {
        if(!registered) {
            eventBus.register(this);
            this.id = id;
            middlewareService.loadComplaintImage(getContext(), url, id, false);
            registered = true;
        }
        else {
            if(bitmap != null) {
                setImageBitmap(bitmap);
            }
        }
    }

    public void onEventMainThread(GetComplaintImageEvent event) {
        if(event.getId().equals(id)) {
            if (event.getSuccess()) {
                setImageBitmap(event.getBitmap());
                bitmap = event.getBitmap();
            } else {
                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT);
            }
            eventBus.unregister(this);
        }
    }

    public void loadHeaderImage(String url, Long id) {
        if(!registered) {
            eventBus.register(this);
            this.id = id;
            middlewareService.loadHeaderImage(getContext(), url, id, false);
            registered = true;
        }
        else {
            if(bitmap != null) {
                setImageBitmap(bitmap);
            }
        }
    }

    public void onEventMainThread(GetHeaderImageEvent event) {
        if(event.getId().equals(id)) {
            if (event.getSuccess()) {
                setImageBitmap(event.getBitmap());
                bitmap = event.getBitmap();
            } else {
                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT);
            }
            eventBus.unregister(this);
        }
    }

    public void setRounded() {
        setOval(true);
    }
}
