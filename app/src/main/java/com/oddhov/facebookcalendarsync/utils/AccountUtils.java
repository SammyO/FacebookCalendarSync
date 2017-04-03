package com.oddhov.facebookcalendarsync.utils;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.facebook.AccessToken;
import com.oddhov.facebookcalendarsync.PermissionsFragment;
import com.oddhov.facebookcalendarsync.R;
import com.oddhov.facebookcalendarsync.data.Constants;

import java.util.regex.Pattern;

import static android.content.Context.ACCOUNT_SERVICE;

public class AccountUtils {

    private Context mContext;
    private NotificationUtils mNotificationUtils;

    public AccountUtils(Context mContext, NotificationUtils notificationUtils) {
        this.mContext = mContext;
        this.mNotificationUtils = notificationUtils;
    }

    public void updateAccountManager(AccessToken accessToken) {
        mNotificationUtils.sendNotification(
                R.string.notification_syncing_problem_title,
                R.string.notification_missing_permissions_message_short,
                R.string.notification_missing_permissions_message_long);

        Log.e("CalendarUtils", "No account permissions granted");
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        AccountManager accountManager = (AccountManager) mContext.getSystemService(ACCOUNT_SERVICE);
        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        Account account;
        if (accounts.length == 0) {
            account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
            accountManager.addAccountExplicitly(account, accessToken.getToken(), null);
        } else {
            account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
            accountManager.setPassword(account, accessToken.getToken());
        }
    }

    String retrieveTokenFromAccountManager() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            mNotificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_missing_permissions_message_short,
                    R.string.notification_missing_permissions_message_long);

            Log.e("CalendarUtils", "No account permissions granted");
            return null;
        }
        AccountManager accountManager = (AccountManager) mContext.getSystemService(ACCOUNT_SERVICE);
        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        Account account;
        if (accounts.length != 0) {
            account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
            return accountManager.getPassword(account);
        }
        return null;
    }

    public void removeTokenFromAccountManager() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            mNotificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_missing_permissions_message_short,
                    R.string.notification_missing_permissions_message_long);

            Log.e("CalendarUtils", "No account permissions granted");
            return;
        }
        AccountManager accountManager = (AccountManager) mContext.getSystemService(ACCOUNT_SERVICE);
        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        Account account;
        if (accounts.length != 0) {
            account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
            accountManager.setPassword(account, null);
        }
    }

    public boolean hasEmptyOrExpiredAccessToken() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null || accessToken.isExpired()) {
            removeTokenFromAccountManager();
            return true;
        }
        return false;
    }
}
