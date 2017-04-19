package com.oddhov.facebookcalendarsync.utils;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.facebook.AccessToken;
import com.oddhov.facebookcalendarsync.data.Constants;

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
        Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) mContext.getSystemService(ACCOUNT_SERVICE);
        if (!accountManager.addAccountExplicitly(account, null, null)) {
            // TODO
            Log.e("AccountUtils", "Error creating account");
        }
    }
}
