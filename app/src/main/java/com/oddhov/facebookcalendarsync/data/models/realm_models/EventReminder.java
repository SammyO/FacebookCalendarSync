package com.oddhov.facebookcalendarsync.data.models.realm_models;

import com.oddhov.facebookcalendarsync.data.models.CustomTime;

import io.realm.RealmObject;

public class EventReminder extends RealmObject {
    private boolean mIsSet;
    private String enumDescription;

    public EventReminder() {
    }

    public EventReminder(boolean isSet, CustomTime timeInMinutes) {
        this.mIsSet = isSet;
        this.enumDescription = timeInMinutes.getTimeInMinutesString();
    }

    public boolean isIsSet() {
        return mIsSet;
    }

    public void setIsSet(boolean isSet) {
        this.mIsSet = isSet;
    }

    public CustomTime getEnum() {
        return CustomTime.getEnumValueFromTimeInMinutesString(enumDescription);
    }
}
