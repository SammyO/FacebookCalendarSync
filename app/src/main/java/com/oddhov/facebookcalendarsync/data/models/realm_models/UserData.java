package com.oddhov.facebookcalendarsync.data.models.realm_models;

import com.oddhov.facebookcalendarsync.data.models.CalendarColour;
import com.oddhov.facebookcalendarsync.data.models.CustomTime;

import io.realm.RealmList;
import io.realm.RealmObject;

public class UserData extends RealmObject {
    private long mLastSyncedTimeStamp;
    private boolean mSyncAdapterPaused;
    private boolean mSyncWifiOnly;
    private boolean mShowNotifications;
    private int mSyncInterval;
    private boolean mSyncOnlyUpcoming;
    private boolean mShowReminders;
    private int mCalendarColor;
    private RealmList<EventReminder> mEventReminders;
    private RealmList<RsvpSyncPreference> mRsvpSyncPreferences;

    public UserData() {
        setSyncInterval(CustomTime.ONE_HOUR.ordinal());
        setCalendarColor(CalendarColour.PURPLE.ordinal());
    }

    public long getLastSyncedTimeStamp() {
        return mLastSyncedTimeStamp;
    }

    public void setLastSyncedTimeStamp(long lastSyncedTimeStamp) {
        this.mLastSyncedTimeStamp = lastSyncedTimeStamp;
    }

    public boolean isSyncAdapterPaused() {
        return mSyncAdapterPaused;
    }

    public void setSyncAdapterPaused(boolean isSyncAdapterPaused) {
        this.mSyncAdapterPaused = isSyncAdapterPaused;
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

    public boolean isSyncOnlyUpcoming() {
        return mSyncOnlyUpcoming;
    }

    public void setSyncOnlyUpcoming(boolean syncOnlyUpcoming) {
        this.mSyncOnlyUpcoming = syncOnlyUpcoming;
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

    public RealmList<RsvpSyncPreference> getRsvpSyncPreferences() {
        return mRsvpSyncPreferences;
    }

    public void setRsvpSyncPreferences(RealmList<RsvpSyncPreference> rsvpSyncPreferences) {
        this.mRsvpSyncPreferences = rsvpSyncPreferences;
    }
}
