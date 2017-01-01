package com.oddhov.facebookcalendarsync.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;

public class NetworkUtils {
    private Context mContext;
    private AccountManagerUtils mAccountManagerUtils;

    public NetworkUtils(Context context, AccountManagerUtils accountManagerUtils) {
        mContext = context;
        mAccountManagerUtils = accountManagerUtils;
    }

    public void fetchUpcomingEvents(GraphRequest.Callback callback) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("CalendarUtils", "No account permissions granted");
            return;
        }

        FacebookSdk.sdkInitialize(mContext);

        String accessToken = mAccountManagerUtils.retrieveTokenFromAuthManager();
        Bundle parameters = new Bundle();
        parameters.putString("access_token", accessToken);
        parameters.putString("since", Long.toString(System.currentTimeMillis() / 1000)); //1476703915

        new GraphRequest(
                null,
                "me/events",
                parameters,
                HttpMethod.GET,
                callback)
                .executeAsync();
    }

    public void fetchAllEvents(GraphRequest.Callback callback) {
        // TODO move this to Authenticator

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("CalendarUtils", "No account permissions granted");
            return;
        }

        FacebookSdk.sdkInitialize(mContext);

        String accessToken = mAccountManagerUtils.retrieveTokenFromAuthManager();
        Bundle parameters = new Bundle();
        parameters.putString("access_token", accessToken);
        new GraphRequest(
                null,
                "me/events",
                parameters,
                HttpMethod.GET,
                callback)
                .executeAsync();

    }
}
