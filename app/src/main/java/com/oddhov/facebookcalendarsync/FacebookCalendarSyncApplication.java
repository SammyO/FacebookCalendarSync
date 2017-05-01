package com.oddhov.facebookcalendarsync;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.oddhov.facebookcalendarsync.utils.AccountUtils;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/*
 * TODO:
 * EventBus
 * Dagger
 * ButterKnife
 * RxJava?
 * Navigation drawer with:
 *     - settings:
 *          # Facebook event settings (birthdays, show links in event)
 *          # Local calendar settings (reminders, colours)
 *          # Sync settings (wifi only, sync frequency, show/hide notifications)
 *     - stop syncing
 *     - report a bug
 *
 * Show last synced time
 * Sync birthday calendars
 * Wifi only setting
 * Sync frequency
 * Reminder feature + settings
 * Show/hide notifications setting
 * Show Facebook links in events
 * Calendar colour setting
 * Sync all events, or only RSVPed setting
 * Support functionality + email address
 * Sonar
 * Gradle checkstyle tasks
 * Donate functionality
 * Events timezone
 * Rate app
 */
public class FacebookCalendarSyncApplication extends Application {
    //region Fields
    private AccountUtils mAccountUtils;
    private RealmConfiguration mRealmConfiguration;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        if (mRealmConfiguration == null) {
            Realm.init(this);
            mRealmConfiguration = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded() // TODO
                    .build();
            Realm.setDefaultConfiguration(mRealmConfiguration);
        }

        mAccountUtils = new AccountUtils(this);
        mAccountUtils.ensureAccountExists();
    }
}
