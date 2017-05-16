package com.oddhov.facebookcalendarsync.data.models.realm_models;

import io.realm.RealmList;
import io.realm.RealmObject;

public class UserData extends RealmObject {
    // TODO set default values
    private long mLastSyncedTimeStamp;
    private boolean mIsSyncAdapterPaused;
    private boolean mSyncWifiOnly;
    private boolean mShowNotifications;
    private int mSyncInterval;
    private boolean mSyncBirthdays;
    private boolean mShowLinks;
    private int mSyncRange;
    private boolean mShowReminders;
    private int mCalendarColor;
    private RealmList<EventReminder> mEventReminders;

    public UserData() {
        setSyncInterval(1);
        setShowNotifications(true);
    }

    public long getLastSyncedTimeStamp() {
        return mLastSyncedTimeStamp;
    }

    public void setLastSyncedTimeStamp(long lastSyncedTimeStamp) {
        this.mLastSyncedTimeStamp = lastSyncedTimeStamp;
    }

    public boolean isIsSyncAdapterPaused() {
        return mIsSyncAdapterPaused;
    }

    public void setIsSyncAdapterPaused(boolean isSyncAdapterPaused) {
        this.mIsSyncAdapterPaused = isSyncAdapterPaused;
    }

    public boolean isSyncWifiOnly() {
        return mSyncWifiOnly;
    }

    public void setSyncWifiOnly(boolean syncWifiOnly) {
        this.mSyncWifiOnly = syncWifiOnly;
    }

    public boolean isShowNotifications() {
        return mShowNotifications;
    }

    public void setShowNotifications(boolean showNotifications) {
        this.mShowNotifications = showNotifications;
    }

    public int getSyncInterval() {
        return mSyncInterval;
    }

    public void setSyncInterval(int syncInterval) {
        this.mSyncInterval = syncInterval;
    }

    public boolean getSyncBirthdays() {
        return mSyncBirthdays;
    }

    public void setSyncBirthdays(boolean syncBirthdays) {
        this.mSyncBirthdays = syncBirthdays;
    }

    public boolean getShowLinks() {
        return mShowLinks;
    }

    public void setShowLinks(boolean showLinks) {
        this.mShowLinks = showLinks;
    }

    public int getSyncRange() {
        return mSyncRange;
    }

    public void setSyncRange(int syncRange) {
        this.mSyncRange = syncRange;
    }

    public boolean getShowReminders() {
        return mShowReminders;
    }

    public void setShowReminders(boolean showReminders) {
        this.mShowReminders = showReminders;
    }

    public int getCalendarColor() {
        return mCalendarColor;
    }

    public void setCalendarColor(int calendarColor) {
        this.mCalendarColor = calendarColor;
    }

    public RealmList<EventReminder> getEventReminders() {
        return mEventReminders;
    }

    public void setEventReminders(RealmList<EventReminder> reminders) {
        this.mEventReminders = reminders;
    }
}
