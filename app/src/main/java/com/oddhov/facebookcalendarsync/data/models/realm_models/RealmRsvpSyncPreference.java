package com.oddhov.facebookcalendarsync.data.models.realm_models;

import com.oddhov.facebookcalendarsync.data.models.RsvpStatus;

import io.realm.RealmObject;

public class RealmRsvpSyncPreference extends RealmObject {
    private boolean mIsSet;
    private String enumDescription;

    public RealmRsvpSyncPreference() {
    }

    public RealmRsvpSyncPreference(boolean isSet, RsvpStatus rsvpStatus) {
        this.mIsSet = isSet;
        this.enumDescription = rsvpStatus.getFacebookParameter();
    }

    public boolean isSet() {
        return mIsSet;
    }

    public void setIsSet(boolean isSet) {
        mIsSet = isSet;
    }

    public RsvpStatus getEnum() {
        return RsvpStatus.getEnumFromFacebookParameter(enumDescription);
    }
}
