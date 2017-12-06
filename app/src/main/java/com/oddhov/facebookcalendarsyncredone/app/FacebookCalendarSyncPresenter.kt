package com.oddhov.facebookcalendarsyncredone.app

import com.oddhov.facebookcalendarsyncredone.app.di.FacebookCalendarSyncContract
import javax.inject.Inject

/**
 * Created by sammy on 06/12/2017.
 */
class FacebookCalendarSyncPresenter
@Inject
constructor(private val view: FacebookCalendarSyncContract.View): FacebookCalendarSyncContract.Presenter {
    //region FacebookCalendarSyncContract.Presenter
    override fun subscribe() {

    }

    override fun unsubscribe() {

    }
    //endregion
}