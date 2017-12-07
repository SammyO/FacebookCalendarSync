package com.oddhov.facebookcalendarsyncredone.app.di

import com.oddhov.facebookcalendarsync.utils.UtilsModule
import com.oddhov.facebookcalendarsyncredone.app.view.FacebookCalendarSyncApplication
import com.oddhov.facebookcalendarsyncredone.data.dagger.PerApp
import dagger.Component

/**
 * Created by sammy on 06/12/2017.
 */
@PerApp
@Component(
        modules = [ApplicationModule::class, UtilsModule::class]
)
interface ApplicationComponent {
    fun inject(application: FacebookCalendarSyncApplication)
}