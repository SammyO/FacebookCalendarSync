package com.oddhov.facebookcalendarsync.utils;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.data.exceptions.UnexpectedException;

import static android.content.Context.ACCOUNT_SERVICE;

public class AccountUtils {
    Context mContext;

    public AccountUtils(Context context) {
        this.mContext = context;
    }

    public static boolean hasEmptyOrExpiredAccessToken() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null || accessToken.isExpired()) {
            return true;
        }
        return false;
    }

    public void ensureAccountExists() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        AccountManager accountManager = (AccountManager) mContext.getSystemService(ACCOUNT_SERVICE);
        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        if (accounts.length == 0) {
            Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
            if (!accountManager.addAccountExplicitly(account, null, null)) {
                Log.e("AccountUtils", "Error creating account");
                Crashlytics.logException(new UnexpectedException("AccountUtils", "Error creating account"));
            }
        }
    }
}
