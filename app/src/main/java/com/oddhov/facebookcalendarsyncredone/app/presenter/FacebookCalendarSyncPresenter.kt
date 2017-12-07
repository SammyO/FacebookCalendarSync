package com.oddhov.facebookcalendarsyncredone.app.presenter

import com.oddhov.facebookcalendarsyncredone.app.FacebookCalendarSyncContract
import javax.inject.Inject

/**
 * Created by sammy on 06/12/2017.
 */
class FacebookCalendarSyncPresenter
@Inject
constructor(private val view: FacebookCalendarSyncContract.View,
            private val repo: FacebookCalendarSyncContract.Repo): FacebookCalendarSyncContract.Presenter {
    override fun setup() {
        repo.ensureSyncAdapterIsSetup()
    }
    //endregion
}