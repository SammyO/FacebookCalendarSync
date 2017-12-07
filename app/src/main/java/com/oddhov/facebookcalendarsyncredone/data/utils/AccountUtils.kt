package com.oddhov.facebookcalendarsyncredone.data.utils

import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.Context.ACCOUNT_SERVICE
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import com.crashlytics.android.Crashlytics
import com.facebook.AccessToken
import com.oddhov.facebookcalendarsync.data.Constants
import com.oddhov.facebookcalendarsync.data.exceptions.UnexpectedException
import timber.log.Timber
import javax.inject.Inject

class AccountUtils
@Inject
constructor(private var context: Context) {

    fun ensureAccountExists() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val accountManager = context.getSystemService(ACCOUNT_SERVICE) as AccountManager
        val accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE)
        if (accounts.isEmpty()) {
            val account = Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE)
            if (!accountManager.addAccountExplicitly(account, null, null)) {
                Timber.e("AccountUtils", "Error creating account")
                Crashlytics.logException(UnexpectedException("AccountUtils", "Error creating account"))
            }
        }
    }

    fun hasEmptyOrExpiredAccessToken(): Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()
        return (accessToken == null || accessToken.isExpired)
    }
}
