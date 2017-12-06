package com.oddhov.facebookcalendarsyncredone.ui.main.presenter

import android.Manifest
import com.oddhov.facebookcalendarsyncredone.ui.main.MainContract
import com.tbruyelle.rxpermissions2.RxPermissions
import javax.inject.Inject

/**
 * Created by sammy on 06/12/2017.
 */
class MainPresenter
@Inject
constructor(private val view: MainContract.View, private val rxPermissions: RxPermissions,
            private val repo: MainContract.Repo): MainContract.Presenter {

    //region MainContract.Presenter
    override fun subscribe() {
        determineViewState()
    }

    override fun unsubscribe() {

    }

    override fun onGrantPermissionsClicked() {
        rxPermissions.requestEach(
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.ACCESS_NETWORK_STATE)
                .all { it.granted }
                .subscribe { granted ->
                    when {
                        granted -> determineViewState()
                        else -> view.showPermissionRationale()
                    }
                }
    }

    override fun onFacebookLoginClicked() {

    }

    override fun onSyncNowClicked() {

    }
    //endregion

    //region Helper Methods (Validation)
    private fun determineViewState() {
        when {
            needsPermissions() -> view.showPermissionsView()
            needsFacebookLogin() -> view.showFacebookLoginView()
            else -> view.showMainSyncView()
        }
    }

    private fun needsPermissions(): Boolean {
        return !rxPermissions.isGranted(Manifest.permission.READ_CALENDAR) ||
                !rxPermissions.isGranted(Manifest.permission.WRITE_CALENDAR) ||
                !rxPermissions.isGranted(Manifest.permission.GET_ACCOUNTS) ||
                !rxPermissions.isGranted(Manifest.permission.ACCESS_NETWORK_STATE)
    }

    private fun needsFacebookLogin(): Boolean {
        return repo.hasEmptyOrExpiredAccessToken()
    }
    //endregion
}