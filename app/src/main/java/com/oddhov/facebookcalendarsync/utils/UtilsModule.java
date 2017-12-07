package com.oddhov.facebookcalendarsync.utils;

import android.content.Context;

import com.oddhov.facebookcalendarsyncredone.data.dagger.ApplicationContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class UtilsModule {
    @Provides
    @Singleton
    AccountUtils provideAccountUtils(@ApplicationContext  Context context) {
        return new AccountUtils(context);
    }

    @Provides
    @Singleton
    CalendarUtils provideCalendarUtils(@ApplicationContext Context context,
                                       NotificationUtils notificationUtils,
                                       DatabaseUtils databaseUtils, TimeUtils timeUtils,
                                       ColorUtils colorUtils) {
        return new CalendarUtils(context, notificationUtils, databaseUtils, timeUtils, colorUtils);
    }

    @Provides
    @Singleton
    DatabaseUtils provideDatabaseUtils(@ApplicationContext Context context) {
        return new DatabaseUtils(context);
    }

    @Provides
    @Singleton
    NavigationDrawerUtils provideNavigationDrawerUtils(@ApplicationContext Context context,
                                                       DatabaseUtils databaseUtils,
                                                       SyncAdapterUtils syncAdapterUtils) {
        return new NavigationDrawerUtils(context, databaseUtils, syncAdapterUtils);
    }

    @Provides
    @Singleton
    NetworkUtils provideNetworkUtils(@ApplicationContext Context context,
                                     NotificationUtils notificationUtils,
                                     DatabaseUtils databaseUtils) {
        return new NetworkUtils(context, notificationUtils, databaseUtils);
    }

    @Provides
    @Singleton
    NotificationUtils provideNotificationUtils(@ApplicationContext Context context) {
        return new NotificationUtils(context);
    }

    @Provides
    @Singleton
    PermissionUtils providePermissionUtils(@ApplicationContext Context context) {
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
