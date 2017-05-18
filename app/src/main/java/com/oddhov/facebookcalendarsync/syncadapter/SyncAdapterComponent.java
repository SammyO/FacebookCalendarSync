package com.oddhov.facebookcalendarsync.syncadapter;


import com.oddhov.facebookcalendarsync.utils.AccountUtils;
import com.oddhov.facebookcalendarsync.utils.CalendarUtils;
import com.oddhov.facebookcalendarsync.utils.DatabaseUtils;
import com.oddhov.facebookcalendarsync.utils.NetworkUtils;
import com.oddhov.facebookcalendarsync.utils.NotificationUtils;
import com.oddhov.facebookcalendarsync.utils.PermissionUtils;
import com.oddhov.facebookcalendarsync.utils.SyncAdapterUtils;
import com.oddhov.facebookcalendarsync.utils.TimeUtils;
import com.oddhov.facebookcalendarsync.utils.UtilsModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {SyncAdapterModule.class, UtilsModule.class})
public interface SyncAdapterComponent {
    void inject(SyncAdapter syncAdapter);

    // Utils
    AccountUtils getAccountUtils();

    CalendarUtils getCalendarUtils();

    DatabaseUtils getDatabaseUtils();

    NetworkUtils getNetworkUtils();

    NotificationUtils getNotificationUtils();

    PermissionUtils getPermissionUtils();

    SyncAdapterUtils getSyncAdapterUtils();

    TimeUtils getTimeUtils();
}
