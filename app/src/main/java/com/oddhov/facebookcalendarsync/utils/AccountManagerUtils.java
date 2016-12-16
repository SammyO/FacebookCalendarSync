package com.oddhov.facebookcalendarsync.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.facebook.AccessToken;
import com.oddhov.facebookcalendarsync.data.Constants;

import static android.content.Context.ACCOUNT_SERVICE;

public class AccountManagerUtils {
    public static void updateAuthManager(Context context, AccessToken accessToken) {
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
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

    public static String retrieveTokenFromAuthManager(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        Account account;
        if (accounts.length != 0) {
            account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
            return accountManager.getPassword(account);
        }
        return null;
    }

    public static void removeFromAuthManager(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        Account account;
        if (accounts.length != 0) {
            account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
            accountManager.setPassword(account, null);
        }
    }
}
