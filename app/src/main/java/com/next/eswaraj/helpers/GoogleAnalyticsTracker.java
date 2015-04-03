package com.next.eswaraj.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.next.eswaraj.base.BaseClass;
import com.next.eswaraj.config.Constants;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import javax.inject.Inject;


/**
 * <p>
 * Wrapper around Google analytics tracker. It:
 * <ul>
 * <li>enqueues tracking events on a background thread so that the main UI thread is not blocked.</li>
 * <li>provides high-level event-categorization and provides filtering based on categories.</li>
 * <li>records statistics on the underlying Google analytics tracker.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Note that calls to configure which categories are tracked, and whether
 * statistics are gathered, are not thread safe, even though calls to the
 * tracking methods are. Calls to the configuration methods should be externally
 * synchronized with the tracking calls. The easiest way to achieve this is to
 * simply configure the GoogleAnalyticsTracker immediately after construction, before
 * it is made available to the rest of the application.
 * </p>
 *
 * @author Tom Gibara
 *
 */

public class GoogleAnalyticsTracker extends BaseClass {

    /**
     * The kind of event being tracked.
     */

    public enum Category {

        /**
         * The category of tracking-events that record activity views (as page
         * views). Eg. the user has viewed a specific activity.
         */
        ACTIVITY,

        /**
         * The category of tracking-events that records user interaction with
         * specific UI controls. Eg. the user has clicked on a specific button.
         */
        UI,

        /**
         * The category of tracking-events that records high-level network
         * interactions. Eg. the user has uploaded a file to the server.
         */
        NET,

        /**
         * The category of tracking-events that records global actions the user
         * performs with the app. Eg. the user uses the app to change the
         * phone's wallpaper.
         */
        APP,

        /**
         * The category of tracking-events that records time spent by user on an item
         * Eg. splash screen or youtube video
         */
        TIME,
    }

    /**
     * The kind of UI action performed.
     */

    public enum UIAction {
        /**
         * The user performs a click (or another type of direct activation) on a
         * control.
         */
        CLICK,

        /**
         * The user performs a long-click (or otherwise contextualizing action)
         * on a control
         */
        LONG_CLICK,

        /**
         * The user selects an item within a control.
         */
        SELECT
    }

    /**
     * The general nature of a global action.
     */

    public enum AppAction {
        /**
         * The user posts complaint in offline mode.
         */
        OFFLINE,

        /**
         * The user posts complaint in online mode.
         */
        ONLINE,

        /**
         * The user makes profile changes
         */
        PROFILE_CHANGE,

        /**
         * The user updates his location
         */
        LOCATION_UPDATE,

        /**
         * The user tries to access a screen which requires login while not logged-in
         */
        ACCESS_DENIED,

        /**
         * The user tries to access a screen while services are not available
         */
        NO_SERVICE,

        /**
         * The user attempts to perform an application-level action but fails
         * (for any reason).
         */
        FAIL
    }

    public enum NetAction {
        /**
         * The user has uploaded something on the network.
         */
        UPLOAD,

        /**
         * The user has downloaded something on the network.
         */
        DOWNLOAD

    }

    /**
     * Gathers performance statistics about the underlying Google Analytics
     * tracker.
     */
    public static class Statistics {

        private int mCount;
        private long mSum;
        private long mSumSq;

        Statistics() {
            mCount = 0;
            mSum = 0L;
            mSumSq = 0L;
        }

        Statistics(Statistics that) {
            synchronized (that) {
                this.mCount = that.mCount;
                this.mSum = that.mSum;
                this.mSumSq = that.mSumSq;
            }
        }

        public synchronized int getCount() {
            return mCount;
        }

        public synchronized double getAverageTime() {
            if (mCount == 0) return 0.0;
            return mSum / (double) mCount;
        }

        public synchronized double getTimeVariance() {
            if (mCount == 0) return 0.0;
            double e = mSum / (double) mCount;
            double eSq = mSumSq / (double) mCount;
            return eSq - e*e;
        }

        synchronized void record(long timeInMillis) {
            mCount ++;
            mSum += timeInMillis;
            mSumSq += timeInMillis * timeInMillis;
        }

    }

    // fields

    @Inject
    Context applicationContext;

    private final EnumSet<Category> mEnabled = EnumSet.allOf(Category.class);

    private final Tracker mTracker;

    private Statistics mStatistics = null;
    private boolean mAsync = false;

    private final ArrayList<Event> mQueue = new ArrayList<Event>();
    private boolean mQueueFlushing = false;
    private Event[] mEvents = null; // temporary working set, held globally to avoid pointless repeated allocations

    // constructors

    /**
     * Constructs a new tracker around a <code>GoogleAnalyticsTracker</code>.
     * This object enqueues tracking events asynchronously, so that events can
     * be tracked from the main UI thread without any delay.
     */

    public GoogleAnalyticsTracker() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(applicationContext);
        mTracker = analytics.newTracker(Constants.GOOGLE_ANALYTICS_KEY);
        mTracker.enableExceptionReporting(true);
        mTracker.enableAutoActivityTracking(true);
        //mTracker.enableAdvertisingIdCollection(true);
        mTracker.setAppName("eSwaraj");
        mTracker.setAppVersion("1.3.0");
        mTracker.setSessionTimeout(-1);
    }

    public void setUserId(Long id) {
        mTracker.set("&uid", id.toString());
    }

    // category methods


    /**
     * Enables tracking of events in the specified category.
     *
     * @param category the category of events which should not be sent to the analytics server, not null
     */

    public void enable(Category category) {
        if (category == null) throw new IllegalArgumentException("null category");
        mEnabled.add(category);
    }

    /**
     * Disables tracking of events in the specified category.
     *
     * @param category the category of events which should not be sent to the analytics server, not null
     */

    public void disable(Category category) {
        if (category == null) throw new IllegalArgumentException("null category");
        mEnabled.remove(category);
    }

    /**
     * Enables all categories of event for tracking.
     */

    public void enableAll() {
        mEnabled.addAll(EnumSet.allOf(Category.class));
    }

    /**
     * Disables all categories of event for tracking.
     */

    public void disableAll() {
        mEnabled.clear();
    }

    /**
     * Whether the specified category is tracked.
     *
     * @param category a category of tracking event
     * @return whether events in the the category are sent to the analytics server
     */

    public boolean isEnabled(Category category) {
        if (category == null) throw new IllegalArgumentException("null category");
        return mEnabled.contains(category);
    }

    // statistics methods

    public void setStatisticsGathered(boolean trackStats) {
        boolean hasStats = mStatistics != null;
        if (trackStats != hasStats) {
            mStatistics = trackStats ? new Statistics() : null;
        }
    }

    public boolean isStatisticsGathered() {
        return mStatistics != null;
    }

    public Statistics getStatistics() {
        if (mStatistics == null) throw new IllegalStateException("No statistics gathered");
        return mStatistics;
    }

    // async methods

    public void setAsynchronous(boolean async) {
        mAsync = async;
    }

    public boolean isAsynchronous() {
        return mAsync;
    }

    // tracking methods

    public void trackActivityView(String pagePath) {
        if (!mEnabled.contains(Category.ACTIVITY)) {
            return;
        }
        if (mAsync) {
            enqueue(new Event(Category.ACTIVITY, pagePath, null, 0));
        } else {
            mTracker.setScreenName(pagePath);
            mTracker.send(new HitBuilders.AppViewBuilder().build());
        }
    }

    public void trackUIEvent(UIAction action, String label, int value) {
        track(Category.UI, action.name(), label, value);
    }

    public void trackUIEvent(UIAction action, String label) {
        trackUIEvent(action, label, 0);
    }

    public void trackAppAction(AppAction action, String label, int value) {
        track(Category.APP, action.name(), label, value);
    }

    public void trackAppAction(AppAction action, String label) {
        trackAppAction(action, label, 0);
    }

    public void trackNetAction(NetAction action, String uri, int bytesTransferred) {
        track(Category.NET, action.name(), uri, bytesTransferred);
    }

    public void trackNetAction(NetAction action, String uri) {
        trackNetAction(action, uri, 0);
    }

    public void trackTimeSpent(String label, String variable, Long value) {
        track(Category.TIME, label, variable, value);
    }

    // private utility methods

    private void track(Category category, String action, String label, int value) {
        if (!mEnabled.contains(category)) return;
        if (mAsync) {
            enqueue(new Event(category, action, label, value));
        } else {
            track(category.name(), action, label, value);
        }
    }

    private void track(Category category, String label, String variable, Long value) {
        if (!mEnabled.contains(category)) return;
        if (mAsync) {
            enqueue(new Event(category, label, variable, value));
        } else {
            track(category.name(), label, variable, value);
        }
    }

    private void enqueue(Event event) {
        synchronized (mQueue) {
            mQueue.add(event);
            if (!mQueueFlushing) {
                new EventTask().execute();
            }
        }
    }

    private void track(String page) {
        mTracker.setScreenName(page);
        if (mStatistics == null) {
            mTracker.send(new HitBuilders.AppViewBuilder().build());
        } else {
            long startTime = SystemClock.uptimeMillis();
            mTracker.send(new HitBuilders.AppViewBuilder().build());
            long timeTaken = SystemClock.uptimeMillis() - startTime;
            mStatistics.record(timeTaken);
        }
    }

    private void track(String category, String action, String label, int value) {
        if (mStatistics == null) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setValue(value)
                    .setAction(action)
                    .setLabel(label)
                    .build());
        } else {
            long startTime = SystemClock.uptimeMillis();
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setValue(value)
                    .setAction(action)
                    .setLabel(label)
                    .build());
            long timeTaken = SystemClock.uptimeMillis() - startTime;
            mStatistics.record(timeTaken);
        }
    }

    private void track(String category, String label, String variable, Long value) {
        if (mStatistics == null) {
            mTracker.send(new HitBuilders.TimingBuilder()
                    .setCategory(category)
                    .setValue(value)
                    .setVariable(variable)
                    .setLabel(label)
                    .build());
        } else {
            long startTime = SystemClock.uptimeMillis();
            mTracker.send(new HitBuilders.TimingBuilder()
                    .setCategory(category)
                    .setValue(value)
                    .setVariable(variable)
                    .setLabel(label)
                    .build());
            long timeTaken = SystemClock.uptimeMillis() - startTime;
            mStatistics.record(timeTaken);
        }
    }

    // inner classes

    private class EventTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            synchronized (mQueue) {
                if (mQueueFlushing) return null;
                mQueueFlushing = true;
            }
            try {
                while (true) {
                    final int length;
                    synchronized (mQueue) {
                        length = mQueue.size();
                        if (length == 0) return null;
                        if (mEvents == null || mEvents.length < length) {
                            mEvents = new Event[mQueue.size()];
                        }
                        mQueue.toArray(mEvents);
                        mQueue.clear();
                    }
                    for (int i = 0; i < length; i++) {
                        Event event = mEvents[i];
                        if (event.mCategory == Category.ACTIVITY) {
                            track(event.mAction);
                        }
                        else if(event.mCategory == Category.TIME) {
                            track(event.mCategory.toString(), event.mLabel, event.mVar, event.mTime);
                        }
                        else {
                            track(event.mCategory.toString(), event.mAction, event.mLabel, event.mValue);
                        }
                    }
                    Arrays.fill(mEvents, null);
                }
            } finally {
                synchronized (mQueue) {
                    mQueueFlushing = false;
                }
            }
        }

    }

    private static class Event {

        Category mCategory;
        String mAction;
        String mLabel;
        String mVar;
        int mValue;
        long mTime;

        Event(Category category, String action, String label, int value) {
            mCategory = category;
            mAction = action;
            mLabel = label;
            mValue = value;
        }

        Event(Category category, String label, String variable, Long value) {
            mCategory = category;
            mLabel = label;
            mVar = variable;
            mTime = value;
        }

    }

}
