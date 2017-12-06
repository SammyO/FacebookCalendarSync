package com.oddhov.facebookcalendarsyncredone.app

import android.app.Application
import com.oddhov.facebookcalendarsyncredone.app.di.ApplicationComponent
import com.oddhov.facebookcalendarsyncredone.app.di.ApplicationModule
import com.oddhov.facebookcalendarsyncredone.app.di.DaggerApplicationComponent
import com.oddhov.facebookcalendarsyncredone.app.di.FacebookCalendarSyncContract

/**
 * Created by sammy on 06/12/2017.
 */
class FacebookCalendarSyncApplication : Application(), FacebookCalendarSyncContract.View {
    //region Fields
    private lateinit var mApplicationComponent: ApplicationComponent
    //endregion

    //region Lifecycle Methods
    override fun onCreate() {
        super.onCreate()

        this.mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
        this.mApplicationComponent.inject(this)
    }
    //endregion

    //region Helper Methods
    fun getApplicationComponent(): ApplicationComponent {
        return this.mApplicationComponent
    }
    //endregion
}