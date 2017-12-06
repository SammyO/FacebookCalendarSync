package com.oddhov.facebookcalendarsyncredone.ui.main.di

import com.oddhov.facebookcalendarsyncredone.app.di.ApplicationComponent
import com.oddhov.facebookcalendarsyncredone.data.dagger.PerActivity
import com.oddhov.facebookcalendarsyncredone.ui.main.view.MainActivity
import dagger.Component

/**
 * Created by sammy on 06/12/2017.
 */
@PerActivity
@Component(
        dependencies = [ApplicationComponent::class],
        modules = [MainModule::class]
)
interface MainComponent {
    fun inject(activity: MainActivity)
}
