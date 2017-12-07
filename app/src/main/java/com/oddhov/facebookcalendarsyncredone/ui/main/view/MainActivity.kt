package com.oddhov.facebookcalendarsyncredone.ui.main.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.internal.CallbackManagerImpl
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.oddhov.facebookcalendarsync.R
import com.oddhov.facebookcalendarsyncredone.app.view.FacebookCalendarSyncApplication
import com.oddhov.facebookcalendarsyncredone.ui.main.MainContract
import com.oddhov.facebookcalendarsyncredone.ui.main.di.DaggerMainComponent
import com.oddhov.facebookcalendarsyncredone.ui.main.di.MainModule
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_main.vfMain
import kotlinx.android.synthetic.main.fragment_sync.btnSynNow
import kotlinx.android.synthetic.main.layout_facebook_login.btnLoginFacebook
import kotlinx.android.synthetic.main.layout_permission_request.btnGrantPermissions
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import timber.log.Timber
import java.util.Arrays
import javax.inject.Inject

/**
 * Created by sammy on 06/12/2017.
 */
class MainActivity : AppCompatActivity(), MainContract.View, FacebookCallback<LoginResult> {
    @Inject
    lateinit var presenter: MainContract.Presenter

    //region Fields
    private lateinit var mCallbackManager: CallbackManager
    //endregion

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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data)
        }
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

    override fun facebookLogin() {
        mCallbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(mCallbackManager, this)

        LoginManager.getInstance().logInWithReadPermissions(
                this, Arrays.asList("email", "public_profile", "user_events"))
    }
    //endregion

    //region FacebookCallback<LoginResult> Methods
    override fun onError(error: FacebookException?) {
        LoginManager.getInstance().unregisterCallback(mCallbackManager)
        toast(R.string.login_again)
        Timber.e(error.toString())
    }

    override fun onCancel() {
        LoginManager.getInstance().unregisterCallback(mCallbackManager)
        toast(R.string.login_again)
    }

    override fun onSuccess(result: LoginResult?) {
        LoginManager.getInstance().unregisterCallback(mCallbackManager)
        presenter.facebookLoginSuccess(result)
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
        btnLoginFacebook.setOnClickListener { facebookLogin() }
        btnSynNow.setOnClickListener{ presenter::onSyncNowClicked.invoke() }
    }
    //endregion
}