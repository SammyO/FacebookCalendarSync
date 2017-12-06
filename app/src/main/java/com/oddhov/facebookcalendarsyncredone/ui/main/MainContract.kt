package com.oddhov.facebookcalendarsyncredone.ui.main

import com.facebook.login.LoginResult
import com.oddhov.facebookcalendarsyncredone.ui.base.BasePresenter

/**
 * Created by sammy on 06/12/2017.
 */
interface MainContract {
    interface View {
        fun showPermissionsView()
        fun showFacebookLoginView()
        fun showMainSyncView()
        fun showPermissionRationale()

        fun facebookLogin()
    }

    interface Presenter : BasePresenter {
        fun onGrantPermissionsClicked()
        fun onSyncNowClicked()

        fun facebookLoginSuccess(result: LoginResult?)
    }

    interface Repo {
        fun hasEmptyOrExpiredAccessToken(): Boolean
    }

}