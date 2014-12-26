package com.eswaraj.app.eswaraj.widgets;


import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Interpolator;

import com.eswaraj.app.eswaraj.interfaces.OnSwipeOutListenerInterface;


public class CustomViewPager extends ViewPager {

    private Timer timer;
    private CustomScroller mScroller = null;
    float mStartDragX;
    OnSwipeOutListenerInterface mListener;


    public void setOnSwipeOutListener(OnSwipeOutListenerInterface listener) {
        mListener = listener;
    }

    public CustomViewPager(Context context) {
        super(context);
        postInitViewPager();
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        postInitViewPager();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("splash", "1");
        float x = ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartDragX = x;
                Log.d("splash", "2");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("splash", "3");
                if (mStartDragX < x && getCurrentItem() == 0) {
                    mListener.onSwipeOutAtStart();
                } else if (mStartDragX > x && getCurrentItem() == getAdapter().getCount() - 1) {
                    mListener.onSwipeOutAtEnd();
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
    
    /**
     * Override the Scroller instance with our own class so we can change the
     * duration
     */
    private void postInitViewPager() {

        try {
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);

            mScroller = new CustomScroller(getContext(), (Interpolator) interpolator.get(null));
            scroller.set(this, mScroller);
        } catch (Exception e) {
        }
    }

    /**
     * Set the factor by which the duration will change
     */
    public void setScrollDurationFactor(double scrollFactor) {
        mScroller.setScrollDurationFactor(scrollFactor);
    }

    public void start() {
        if (null != timer) {
            timer.cancel();
        }
        timer = new Timer();
        startTimer();
    }

    public void cancel() {
        if(timer != null) {
            timer.cancel();
        }
    }

    private void startTimer() {

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                ((Activity)getContext()).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        int currentItem = getCurrentItem();
                        if (currentItem == getAdapter().getCount() - 1) {
                            Log.d("Gallery", "show first item, current:" + currentItem);
                            setCurrentItem(0, true);
                        } else {
                            Log.d("Gallery", "show next item, current:" + currentItem);
                            setCurrentItem(currentItem + 1, true);
                        }
                    }
                });
            }
        }, 4000, 4000);
    }

}