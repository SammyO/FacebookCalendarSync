package com.oddhov.facebookcalendarsync.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.oddhov.facebookcalendarsync.R;
import com.oddhov.facebookcalendarsync.api.FacebookService;

public class NetworkUtils {
    private Context mContext;
    private NotificationUtils mNotificationUtils;

    public NetworkUtils(Context context, NotificationUtils notificationUtils) {
        this.mContext = context;
        this.mNotificationUtils = notificationUtils;
    }

    public void fetchEvents(GraphRequest.Callback callback, boolean fetchOnlyUpcoming, String rsvpPreferenceStatusValue) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            mNotificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_missing_permissions_message_short,
                    R.string.notification_missing_permissions_message_long);
            Log.e("CalendarUtils", "No account permissions granted");
            return;
        }

        //graph.facebook.com/me?access_token=EAACEdEose0cBAHkVCqFQ1tadqT00rMHEh5T8GPozAiUkadOBjzKaa02rp0SKYAbQmZBHZBHrbkRNXKq2E6qchpSQjtT6VkxjZBDw5f4BEdmsybThaoQtOKPZBdQ3OpZATC7xhh2EauM1CpFeCM6hCWmXEtzs3hGIv02HNenfux4zrRxuk7zgkaGdPZAyhiJW4ZD&fields=id,name,events

        Log.e("NetworkUtils", "Fetching events...");
        FacebookService facebookService = new FacebookService();
        facebookService.getEvents(AccessToken.getCurrentAccessToken().getToken())
                .subscribe(
                        eventList -> {

                        }, throwable -> {

                        }
                );

//        FacebookSdk.sdkInitialize(mContext);
//
//        String accessToken = AccessToken.getCurrentAccessToken().getToken();
//        Bundle parameters = new Bundle();
//        parameters.putString("access_token", accessToken);
//        parameters.putString("limit", "25");
//        if (fetchOnlyUpcoming) {
//            parameters.putString("since", Long.toString(System.currentTimeMillis() / 1000));
//        }
//        if (rsvpPreferenceStatusValue != null) {
//            parameters.putString("type", rsvpPreferenceStatusValue);
//        }
//
//        new GraphRequest(
//                AccessToken.getCurrentAccessToken(),
//                "/" + Profile.getCurrentProfile().getId() + "/events",
//                parameters,
//                HttpMethod.GET,
//                callback)
//                .executeAsync();
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
