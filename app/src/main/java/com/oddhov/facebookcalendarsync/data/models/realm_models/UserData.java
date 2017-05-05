package com.oddhov.facebookcalendarsync.data.models.realm_models;

import io.realm.RealmObject;

public class UserData extends RealmObject {
    private long mLastSyncedTimeStamp;
    private boolean mIsSyncAdapterPaused;

    public UserData() {
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
}
