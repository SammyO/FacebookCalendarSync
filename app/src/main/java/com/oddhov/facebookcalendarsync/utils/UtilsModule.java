package com.oddhov.facebookcalendarsync.utils;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class UtilsModule {
    @Provides
    @Singleton
    AccountUtils provideAccountUtils(Context context) {
        return new AccountUtils(context);
    }

    @Provides
    @Singleton
    CalendarUtils provideCalendarUtils(Context context, NotificationUtils notificationUtils,
                                       DatabaseUtils databaseUtils, TimeUtils timeUtils) {
        return new CalendarUtils(context, notificationUtils, databaseUtils, timeUtils);
    }

    @Provides
    @Singleton
    DatabaseUtils provideDatabaseUtils(Context context) {
        return new DatabaseUtils(context);
    }

    @Provides
    @Singleton
    NetworkUtils provideNetworkUtils(Context context, NotificationUtils notificationUtils,
                                     DatabaseUtils databaseUtils) {
        return new NetworkUtils(context, notificationUtils, databaseUtils);
    }

    @Provides
    @Singleton
    NotificationUtils provideNotificationutils(Context context) {
        return new NotificationUtils(context);
    }

    @Provides
    @Singleton
    PermissionUtils providePermissionUtils(Context context) {
        return new PermissionUtils(context);
    }

    @Provides
    @Singleton
    SyncAdapterUtils provideSyncAdapterUtils() {
        return new SyncAdapterUtils();
    }

    @Provides
    @Singleton
    TimeUtils provideTimeUtils() {
        return new TimeUtils();
    }
}
