package com.next.eswaraj.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class ProgressTextView extends TextView {

    private Boolean textSet = false;
    private Boolean animating = false;
    private TimerTask timerTask;
    private Timer timer;
    private Handler handler;

    public ProgressTextView(Context context) {
        super(context);
        handler = new Handler(context.getMainLooper());
    }

    public ProgressTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        handler = new Handler(context.getMainLooper());
    }

    public ProgressTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handler = new Handler(context.getMainLooper());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProgressTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!textSet && !animating) {
            startTimer();
            animating = true;
        }
    }

    public void setActualText(CharSequence text) {
        if(timer != null) {
            timer.cancel();
        }
        setText(text);
        textSet = true;
        animating = false;
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        switch (visibility) {
            case VISIBLE:
                if(animating != null && textSet != null && timer != null) {
                    if (animating && !textSet) {
                        startTimer();
                    }
                }
                break;

            case INVISIBLE:
                stopTimer();
                break;

            case GONE:
                stopTimer();
                break;
        }
    }

    private void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(getText().equals("")) {
                            setText(".");
                        }
                        else if(getText().equals(".")) {
                            setText(". .");
                        }
                        else if(getText().equals(". .")) {
                            setText(". . .");
                        }
                        else if(getText().equals(". . .")) {
                            setText("");
                        }
                    }
                });
            }
        };
        setText("");
        timer = new Timer();
        timer.schedule(timerTask, 0, 500);
    }

    private void stopTimer() {
        if(timer != null) {
            timer.cancel();
        }
    }
}
