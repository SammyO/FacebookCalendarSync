package com.oddhov.facebookcalendarsyncredone.ui.main

import com.oddhov.facebookcalendarsyncredone.ui.base.BasePresenter

/**
 * Created by sammy on 06/12/2017.
 */
interface MainContract {
    interface View {
        fun showPermissionsView()
        fun showFacebookLoginView()
        fun showMainSyncView()
    }

    interface Presenter : BasePresenter {

    }

    interface Repo {

    }

}