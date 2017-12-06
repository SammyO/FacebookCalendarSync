package com.oddhov.facebookcalendarsyncredone.app.di

import com.oddhov.facebookcalendarsyncredone.app.FacebookCalendarSyncPresenter
import com.oddhov.facebookcalendarsyncredone.data.dagger.PerApp
import dagger.Module
import dagger.Provides

/**
 * Created by sammy on 06/12/2017.
 */
@PerApp
@Module
class ApplicationModule
constructor(private val application : FacebookCalendarSyncContract.View) {
    @Provides
    internal fun provideView(): FacebookCalendarSyncContract.View {
        return application
    }

    @Provides
    fun providePresenter(presenter: FacebookCalendarSyncPresenter): FacebookCalendarSyncContract.Presenter {
        return presenter
    }
}