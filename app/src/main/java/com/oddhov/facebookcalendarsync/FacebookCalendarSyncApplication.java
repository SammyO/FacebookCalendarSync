package com.oddhov.facebookcalendarsync;

import android.app.Application;

import com.oddhov.facebookcalendarsync.utils.DatabaseUtils;

public class FacebookCalendarSyncApplication extends Application {
    //region Fields
    DatabaseUtils mDatabaseUtils;
    //endregion

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabaseUtils = new DatabaseUtils(this);
        mDatabaseUtils.initializeRealmConfig(this);
    }
}
