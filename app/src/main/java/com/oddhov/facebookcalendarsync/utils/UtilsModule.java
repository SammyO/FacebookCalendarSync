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
                                       DatabaseUtils databaseUtils, TimeUtils timeUtils,
                                       ColorUtils colorUtils) {
        return new CalendarUtils(context, notificationUtils, databaseUtils, timeUtils, colorUtils);
    }

    @Provides
    @Singleton
    DatabaseUtils provideDatabaseUtils(Context context) {
        return new DatabaseUtils(context);
    }

    @Provides
    @Singleton
    NavigationDrawerUtils provideNavigationDrawerUtils(Context context,
                                                       DatabaseUtils databaseUtils,
                                                       SyncAdapterUtils syncAdapterUtils) {
        return new NavigationDrawerUtils(context, databaseUtils, syncAdapterUtils);
    }

    @Provides
    @Singleton
    NetworkUtils provideNetworkUtils(Context context, NotificationUtils notificationUtils) {
        return new NetworkUtils(context, notificationUtils);
    }

    @Provides
    @Singleton
    NotificationUtils provideNotificationUtils(Context context, DatabaseUtils databaseUtils) {
        return new NotificationUtils(context, databaseUtils);
    }

    @Provides
    @Singleton
    PermissionUtils providePermissionUtils(Context context) {
        return new PermissionUtils(context);
    }

    @Provides
    @Singleton
    SyncAdapterUtils provideSyncAdapterUtils(DatabaseUtils databaseUtils) {
        return new SyncAdapterUtils(databaseUtils);
    }

    @Provides
    @Singleton
    TimeUtils provideTimeUtils() {
        return new TimeUtils();
    }

    @Provides
    @Singleton
    ColorUtils provideColorUtils() {
        return new ColorUtils();
    }
}
