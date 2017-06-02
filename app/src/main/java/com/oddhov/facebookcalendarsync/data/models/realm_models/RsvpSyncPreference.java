package com.oddhov.facebookcalendarsync.data.models.realm_models;

import io.realm.RealmObject;

public class RsvpSyncPreference extends RealmObject {
    private boolean mIsSet;
    private String mRsvpStatusValue;

    public RsvpSyncPreference() {
    }

    public RsvpSyncPreference(boolean isSet, String rsvpStatus) {
        this.mIsSet = isSet;
        this.mRsvpStatusValue = rsvpStatus;
    }

    public boolean isSet() {
        return mIsSet;
    }

    public void setIsSet(boolean isSet) {
        mIsSet = isSet;
    }

    public String getRsvpStatusValue() {
        return mRsvpStatusValue;
    }
}
