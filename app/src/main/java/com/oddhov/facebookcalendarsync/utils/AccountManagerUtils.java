package com.oddhov.facebookcalendarsync.utils;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Patterns;

import com.facebook.AccessToken;
import com.oddhov.facebookcalendarsync.data.Constants;

import java.util.regex.Pattern;

import static android.content.Context.ACCOUNT_SERVICE;

public class AccountManagerUtils {

    private Context mContext;

    public AccountManagerUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void updateAuthManager(AccessToken accessToken) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO
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

    public String retrieveTokenFromAuthManager() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO
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

    public void removeFromAuthManager() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO
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

    public String getPrimaryAccount() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO
            return null;
        }
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(mContext).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches() && !TextUtils.isEmpty(account.type)
                    && account.type.equals("com.google")) {
                return account.name;
            }
        }
        return null;
    }
}
