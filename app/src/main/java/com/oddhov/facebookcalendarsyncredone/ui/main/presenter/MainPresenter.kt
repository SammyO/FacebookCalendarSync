package com.oddhov.facebookcalendarsyncredone.ui.main.presenter

import com.oddhov.facebookcalendarsyncredone.ui.main.MainContract
import javax.inject.Inject

/**
 * Created by sammy on 06/12/2017.
 */
class MainPresenter
@Inject
constructor(private val view: MainContract.View): MainContract.Presenter {

    //region MainContract.Presenter
    override fun subscribe() {

    }

    override fun unsubscribe() {

    }
    //endregion
}