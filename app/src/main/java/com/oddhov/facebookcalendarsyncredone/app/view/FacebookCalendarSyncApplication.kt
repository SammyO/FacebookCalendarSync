package com.oddhov.facebookcalendarsyncredone.app.view

import android.app.Application
import com.oddhov.facebookcalendarsync.BuildConfig
import com.oddhov.facebookcalendarsyncredone.app.FacebookCalendarSyncContract
import com.oddhov.facebookcalendarsyncredone.app.di.ApplicationComponent
import com.oddhov.facebookcalendarsyncredone.app.di.ApplicationModule
import com.oddhov.facebookcalendarsyncredone.app.di.DaggerApplicationComponent
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by sammy on 06/12/2017.
 */
class FacebookCalendarSyncApplication : Application(), FacebookCalendarSyncContract.View {
    @Inject
    lateinit var presenter: FacebookCalendarSyncContract.Presenter

    //region Fields
    private lateinit var mApplicationComponent: ApplicationComponent
    //endregion

    //region Lifecycle Methods
    override fun onCreate() {
        super.onCreate()

        setupDi()
        setupTimber()
        presenter.setup()
    }
    //endregion

    //region Helper Methods
    fun getApplicationComponent(): ApplicationComponent {
        return this.mApplicationComponent
    }

    private fun setupDi() {
        this.mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
        this.mApplicationComponent.inject(this)
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
    //endregion
}