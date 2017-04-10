package com.oddhov.facebookcalendarsync.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.oddhov.facebookcalendarsync.R;

public class NetworkUtils {
    private Context mContext;
    private NotificationUtils mNotificationUtils;
    private DatabaseUtils mDatabaseUtils;

    public NetworkUtils(Context context, NotificationUtils notificationUtils, DatabaseUtils databaseUtils) {
        this.mContext = context;
        this.mNotificationUtils = notificationUtils;
        this.mDatabaseUtils = databaseUtils;
    }

    public void fetchUpcomingEvents(GraphRequest.Callback callback) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            mNotificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_missing_permissions_message_short,
                    R.string.notification_missing_permissions_message_long);
            Log.e("CalendarUtils", "No account permissions granted");
            return;
        }

        FacebookSdk.sdkInitialize(mContext);

        String accessToken = AccessToken.getCurrentAccessToken().getToken();
        Bundle parameters = new Bundle();
        parameters.putString("access_token", accessToken);
        parameters.putString("limit", "25");
        parameters.putString("since", Long.toString(System.currentTimeMillis() / 1000));

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + Profile.getCurrentProfile().getId() + "/events",
                parameters,
                HttpMethod.GET,
                callback)
                .executeAsync();
    }

    public void fetchAllEvents(GraphRequest.Callback callback) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            mNotificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_missing_permissions_message_short,
                    R.string.notification_missing_permissions_message_long);
            Log.e("CalendarUtils", "No account permissions granted");
            return;
        }

        FacebookSdk.sdkInitialize(mContext);

        String accessToken = AccessToken.getCurrentAccessToken().getToken();
        Bundle parameters = new Bundle();
        parameters.putString("access_token", accessToken);
        parameters.putString("limit", "25");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + Profile.getCurrentProfile().getId() + "/events",
                parameters,
                HttpMethod.GET,
                callback)
                .executeAsync();
    }

    public boolean requestNextPage(GraphResponse response, GraphRequest.Callback callback) {
        GraphRequest nextRequest = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
        if (nextRequest != null) {
            Bundle parameters = new Bundle();
            nextRequest.setParameters(parameters);
            nextRequest.setCallback(callback);
            nextRequest.executeAsync();
            return true;
        }
        return false;
    }
}
