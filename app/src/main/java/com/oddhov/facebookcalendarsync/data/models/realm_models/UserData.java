package com.oddhov.facebookcalendarsync.data.models.realm_models;

import io.realm.RealmObject;

public class UserData extends RealmObject {
    private long mLastSyncedTimeStamp;

    public UserData() {
    }

    public long getLastSyncedTimeStamp() {
        return mLastSyncedTimeStamp;
    }

    public void setLastSyncedTimeStamp(long lastSyncedTimeStamp) {
        this.mLastSyncedTimeStamp = lastSyncedTimeStamp;
    }
}
