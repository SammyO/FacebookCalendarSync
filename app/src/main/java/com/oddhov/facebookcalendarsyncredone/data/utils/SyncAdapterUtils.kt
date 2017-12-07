package com.oddhov.facebookcalendarsyncredone.data.utils

import android.accounts.Account
import android.content.ContentResolver
import android.os.Bundle
import com.crashlytics.android.Crashlytics
import com.oddhov.facebookcalendarsync.data.Constants
import com.oddhov.facebookcalendarsync.data.exceptions.RealmException
import com.oddhov.facebookcalendarsync.data.exceptions.UnexpectedException
import com.oddhov.facebookcalendarsync.data.models.CustomTime
import com.oddhov.facebookcalendarsync.utils.DatabaseUtils
import timber.log.Timber
import javax.inject.Inject

class SyncAdapterUtils
@Inject
constructor(private var mDatabaseUtils: DatabaseUtils) {

    fun ensureSyncAdapterIsSetup() {
        val account = Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE)
        val bundle = Bundle()
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
        val periodicSyncsList = ContentResolver.getPeriodicSyncs(account, "com.android.calendar")
        if (periodicSyncsList.size == 0) {
            Timber.i("Setting up new adapter")
            setupSyncAdapter()
        } else if (periodicSyncsList.size > 1) {
            Crashlytics.logException(UnexpectedException("SyncAdapterUtils",
                    "More than one SyncAdapter set up for this account"))
        } else {
            Timber.i("Adapter already setup")
        }
    }

    fun setSyncAdapterRunnable(runnable: Boolean) {
        val account = Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE)
        val bundle = Bundle()
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
        if (runnable) {
            ContentResolver.setIsSyncable(account, "com.android.calendar", 1)
        } else {
            ContentResolver.setIsSyncable(account, "com.android.calendar", 0)
        }
        Timber.i("SyncAdapter is syncable: " + ContentResolver.getIsSyncable(account, "com.android.calendar"))
    }

    fun runSyncAdapterNow() {
        val account = Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE)
        val bundle = Bundle()
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
        ContentResolver.requestSync(account, "com.android.calendar", bundle)
    }

    fun setSyncAdapterRunInterval(interval: CustomTime) {
        val account = Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE)
        val bundle = Bundle()
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
        ContentResolver.addPeriodicSync(account, "com.android.calendar", Bundle.EMPTY, interval.timeInMinutes)
    }

    private fun setupSyncAdapter() {
        try {
            val account = Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE)
            val bundle = Bundle()
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
            ContentResolver.setIsSyncable(account, "com.android.calendar", 1)
            ContentResolver.addPeriodicSync(account, "com.android.calendar", Bundle.EMPTY,
                    mDatabaseUtils.syncInterval.timeInMinutes)
            ContentResolver.setSyncAutomatically(account, "com.android.calendar", true)
            ContentResolver.requestSync(account, "com.android.calendar", bundle)
        } catch (e: RealmException) {
            Crashlytics.logException(e)
        }

    }
}
