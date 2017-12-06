package com.oddhov.facebookcalendarsyncredone.ui.main.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import com.oddhov.facebookcalendarsync.R
import com.oddhov.facebookcalendarsyncredone.app.FacebookCalendarSyncApplication
import com.oddhov.facebookcalendarsyncredone.ui.main.MainContract
import com.oddhov.facebookcalendarsyncredone.ui.main.di.DaggerMainComponent
import com.oddhov.facebookcalendarsyncredone.ui.main.di.MainModule
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_main.vfMain
import kotlinx.android.synthetic.main.fragment_sync.btnSynNow
import kotlinx.android.synthetic.main.layout_facebook_login.btnLoginFacebook
import kotlinx.android.synthetic.main.layout_permission_request.btnGrantPermissions
import org.jetbrains.anko.alert
import javax.inject.Inject

/**
 * Created by sammy on 06/12/2017.
 */
class MainActivity : AppCompatActivity(), MainContract.View {
    @Inject
    lateinit var presenter: MainContract.Presenter

    //region Activity Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupDi()

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setOnClickListeners()
    }

    override fun onStart() {
        super.onStart()
        presenter.subscribe()
    }

    override fun onStop() {
        presenter.unsubscribe()
        super.onStop()
    }
    //endregion

    //region MainContract.View
    override fun showPermissionsView() {
        changeScreenState(R.id.clPermission)
    }

    override fun showFacebookLoginView() {
        changeScreenState(R.id.clFacebookLogin)
    }

    override fun showMainSyncView() {
        changeScreenState(R.id.clMainSync)
    }

    override fun showPermissionRationale() {
        alert(R.string.permission_rationale_description,
                R.string.request_permissions_title) {
            positiveButton(R.string.alert_allow_permission_app_info, {
                startActivityForResult(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + packageName)), 0)
            })
            negativeButton(R.string.word_cancel, {})
        }.show()
    }
    //endregion

    //region Helper Methods (DI)
    private fun setupDi() {
        DaggerMainComponent.builder()
                .applicationComponent((application as FacebookCalendarSyncApplication).getApplicationComponent())
                .mainModule(MainModule(this))
                .build()
                .inject(this)
    }
    //endregion

    //region Helper Methods (UI)
    private fun changeScreenState(state: Int) {
        vfMain.displayedChild = vfMain.indexOfChild(vfMain.findViewById(state))
    }

    private fun setOnClickListeners() {
        btnGrantPermissions.setOnClickListener{ presenter::onGrantPermissionsClicked.invoke() }
        btnLoginFacebook.setOnClickListener { presenter::onFacebookLoginClicked.invoke() }
        btnSynNow.setOnClickListener{ presenter::onSyncNowClicked.invoke() }
    }
    //endregion
}