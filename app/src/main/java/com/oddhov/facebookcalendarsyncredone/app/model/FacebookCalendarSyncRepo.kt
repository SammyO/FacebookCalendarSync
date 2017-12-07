package com.oddhov.facebookcalendarsyncredone.app.model

import com.oddhov.facebookcalendarsync.utils.SyncAdapterUtils
import com.oddhov.facebookcalendarsyncredone.app.FacebookCalendarSyncContract
import javax.inject.Inject

/**
 * Created by sammy on 07/12/2017.
 */
class FacebookCalendarSyncRepo
@Inject
constructor(private val syncAdapterUtils: SyncAdapterUtils): FacebookCalendarSyncContract.Repo {
    override fun ensureSyncAdapterIsSetup() {
        syncAdapterUtils.ensureSyncAdapterIsSetup()
    }
}