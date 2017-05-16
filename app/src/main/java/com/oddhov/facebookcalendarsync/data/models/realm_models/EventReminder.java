package com.oddhov.facebookcalendarsync.data.models.realm_models;

import io.realm.RealmObject;

public class EventReminder extends RealmObject {
    private boolean mIsSet;
    private int mTimeInMinutes;

    public EventReminder() {
    }

    public EventReminder(boolean isSet, int timeInMinutes) {
        this.mIsSet = isSet;
        this.mTimeInMinutes = timeInMinutes;
    }

    public boolean isIsSet() {
        return mIsSet;
    }

    public void setIsSet(boolean isSet) {
        this.mIsSet = isSet;
    }

    public int getTimeInMinutes() {
        return mTimeInMinutes;
    }

    public void setTimeInMinutes(int timeInMinutes) {
        this.mTimeInMinutes = timeInMinutes;
    }
}
