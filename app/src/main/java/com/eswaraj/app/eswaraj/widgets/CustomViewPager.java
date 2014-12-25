package com.eswaraj.app.eswaraj.widgets;


import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.animation.Interpolator;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.eswaraj.app.eswaraj.R;


public class CustomViewPager extends ViewPager {

    private Timer timer;
    private RadioGroup radioGroup;
    private OnPageChangeListener onPageChangeListener;
    private static final int RADIO_BUTTON_STARTING_ID = 0x100;

    public CustomViewPager(Context context) {
        super(context);
        onPageChangeListener = new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                radioGroup.check(RADIO_BUTTON_STARTING_ID + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        super.setOnPageChangeListener(onPageChangeListener);
        postInitViewPager();
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        onPageChangeListener = new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                radioGroup.check(RADIO_BUTTON_STARTING_ID + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        super.setOnPageChangeListener(onPageChangeListener);
        postInitViewPager();
    }

    public void setRadioGroup(RadioGroup radioGroup) {
        this.radioGroup = radioGroup;
    }

    private CustomScroller mScroller = null;

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
        setUpRadioGroup();
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


    private void setUpRadioGroup() {
        for (int i = 0; i < getAdapter().getCount(); i++) {
            RadioButton radio = new RadioButton(getContext());
            radio.setId(RADIO_BUTTON_STARTING_ID + i);
            radio.setEnabled(false);
            radioGroup.addView(radio);
        }
        radioGroup.check(RADIO_BUTTON_STARTING_ID + 0);
        radioGroup.setEnabled(true);
    }
}