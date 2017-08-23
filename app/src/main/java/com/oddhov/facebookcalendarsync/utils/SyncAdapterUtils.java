package com.oddhov.facebookcalendarsync.utils;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.PeriodicSync;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.data.exceptions.UnexpectedException;
import com.oddhov.facebookcalendarsync.data.models.CustomTime;

import java.util.List;

public class SyncAdapterUtils {

    DatabaseUtils mDatabaseUtils;

    public SyncAdapterUtils(DatabaseUtils databaseUtils) {
        mDatabaseUtils = databaseUtils;
    }

    public void ensureSyncAdapterIsSetup() {
        Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        List<PeriodicSync> periodicSyncsList = ContentResolver.getPeriodicSyncs(account, "com.android.calendar");
        if (periodicSyncsList.size() == 0) {
            Log.e("SyncAdapterUtils", "setting up new adapter");
            setupSyncAdapter();
        } else if (periodicSyncsList.size() > 1) {
            Crashlytics.logException(new UnexpectedException("SyncAdapterUtils",
                    "More than one SyncAdapter set up for this account"));
        } else {
            Log.e("SyncAdapterUtils", "adapter exists");
        }
    }

    public void setSyncAdapterRunnable(boolean runable) {
        Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        if (runable) {
            ContentResolver.setIsSyncable(account, "com.android.calendar", 1);
        } else {
            ContentResolver.setIsSyncable(account, "com.android.calendar", 0);
        }
        Log.i("SyncAdapter", "SyncAdapter is syncable: " + ContentResolver.getIsSyncable(account, "com.android.calendar"));
    }

    public void runSyncAdapterNow() {
        Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, "com.android.calendar", bundle);
    }

    public void setSyncAdapterRunInterval(CustomTime interval) {
        Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.addPeriodicSync(account, "com.android.calendar", Bundle.EMPTY, interval.getTimeInMinutes());

    }

    private void setupSyncAdapter() {
        Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.setIsSyncable(account, "com.android.calendar", 1);
        ContentResolver.addPeriodicSync(account, "com.android.calendar", Bundle.EMPTY,
                mDatabaseUtils.getSyncInterval().getTimeInMinutes());
        ContentResolver.setSyncAutomatically(account, "com.android.calendar", true);
        ContentResolver.requestSync(account, "com.android.calendar", bundle);
    }
}
