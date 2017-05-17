package com.oddhov.facebookcalendarsync.app;

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
@Component(modules = {ApplicationModule.class, UtilsModule.class})
public interface ApplicationComponent {
    void inject(FacebookCalendarSyncApplication application);

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
