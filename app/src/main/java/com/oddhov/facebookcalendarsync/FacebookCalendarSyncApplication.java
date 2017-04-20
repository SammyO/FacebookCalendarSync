package com.oddhov.facebookcalendarsync;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.oddhov.facebookcalendarsync.utils.AccountUtils;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

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
