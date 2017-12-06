package com.oddhov.facebookcalendarsyncredone.app.di

import android.app.Application
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
constructor(private val application : Application) {
    @Provides
    fun bindPresenter(presenter : FacebookCalendarSyncPresenter): FacebookCalendarSyncContract.Presenter {
        return presenter
    }
}