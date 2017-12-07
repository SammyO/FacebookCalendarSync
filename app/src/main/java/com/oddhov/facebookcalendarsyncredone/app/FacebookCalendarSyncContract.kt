package com.oddhov.facebookcalendarsyncredone.app

/**
 * Created by sammy on 06/12/2017.
 */
interface FacebookCalendarSyncContract {
    interface View {

    }

    interface Presenter {
        fun setup()
    }

    interface Repo {
        fun ensureSyncAdapterIsSetup()
    }
}