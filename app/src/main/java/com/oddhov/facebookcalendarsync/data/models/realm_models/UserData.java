package com.oddhov.facebookcalendarsync.data.models.realm_models;

import io.realm.RealmObject;

public class UserData extends RealmObject {
    // TODO set default values
    private long mLastSyncedTimeStamp;
    private boolean mIsSyncAdapterPaused;
    private boolean mSyncWifiOnly;
    private boolean mShowNotifications;
    private int mSyncInterval;

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
}
