package com.oddhov.facebookcalendarsync.app;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.oddhov.facebookcalendarsync.data.exceptions.RealmException;
import com.oddhov.facebookcalendarsync.utils.AccountUtils;
import com.oddhov.facebookcalendarsync.utils.DatabaseUtils;
import com.oddhov.facebookcalendarsync.utils.SyncAdapterUtils;
import com.oddhov.facebookcalendarsync.utils.UtilsModule;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

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
 *          # Facebook event settings (birthdays, show links in event, sync all events/sync upcoming events)
 *          # Local calendar settings (reminders, colours)
 *          # Sync settings (wifi only, sync frequency, show/hide notifications)
 *     - stop syncing
 *     - log out from Facebook
 *     - report a bug
 *
 * Navigation drawer footer with app version, and name etc.
 * MaterialDesign features like pull to refresh
 * How it works screen
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
 * Google Analytics
 *
 */
public class FacebookCalendarSyncApplication extends Application {
    //region Fields
    private ApplicationComponent mApplicationComponent;
    private RealmConfiguration mRealmConfiguration;

    @Inject
    DatabaseUtils mDatabaseUtils;
    @Inject
    AccountUtils mAccountUtils;
    @Inject
    SyncAdapterUtils mSyncAdapterUtils;
    //endregion

    @Override
    public void onCreate() {
        super.onCreate();
        this.mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(getApplicationContext(), EventBus.getDefault()))
                .utilsModule(new UtilsModule())
                .build();
        this.mApplicationComponent.inject(this);

        Fabric.with(this, new Crashlytics());

        if (mRealmConfiguration == null) {
            Realm.init(this);
            mRealmConfiguration = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded() // TODO
                    .build();
            Realm.setDefaultConfiguration(mRealmConfiguration);
        }

        try {
            mDatabaseUtils.setupUserData();
        } catch (RealmException e) {
            Crashlytics.logException(e);
        }

        mAccountUtils.ensureAccountExists();
        mSyncAdapterUtils.ensureSyncAdapterIsSetup();
    }

    public ApplicationComponent getApplicationComponent() {
        return this.mApplicationComponent;
    }
}
