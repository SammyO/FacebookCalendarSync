package com.oddhov.facebookcalendarsyncredone.ui.main.di

import com.oddhov.facebookcalendarsyncredone.data.dagger.PerActivity
import com.oddhov.facebookcalendarsyncredone.ui.main.MainContract
import com.oddhov.facebookcalendarsyncredone.ui.main.model.MainRepo
import com.oddhov.facebookcalendarsyncredone.ui.main.presenter.MainPresenter
import com.oddhov.facebookcalendarsyncredone.ui.main.view.MainActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.Module
import dagger.Provides

/**
 * Created by sammy on 06/12/2017.
 */
@PerActivity
@Module
class MainModule
constructor(private val activity: MainActivity) {
    @Provides
    internal fun provideView(): MainContract.View {
        return activity
    }

    @Provides
    fun providePresenter(presenter: MainPresenter) : MainContract.Presenter {
        return presenter
    }

    @Provides
    internal fun provideRepo(repo: MainRepo): MainContract.Repo {
        return repo
    }

    @Provides
    internal fun provideRxPermission(): RxPermissions {
        return RxPermissions(activity)
    }
}