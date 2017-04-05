package com.oddhov.facebookcalendarsync.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.oddhov.facebookcalendarsync.R;
import com.oddhov.facebookcalendarsync.events.FacebookGetUserWithEventsResponse;
import com.oddhov.facebookcalendarsync.data.models.EventsResponse;
import com.oddhov.facebookcalendarsync.utils.AccountUtils;
import com.oddhov.facebookcalendarsync.utils.CalendarUtils;
import com.oddhov.facebookcalendarsync.utils.DatabaseUtils;
import com.oddhov.facebookcalendarsync.utils.NetworkUtils;
import com.oddhov.facebookcalendarsync.utils.NotificationUtils;
import com.oddhov.facebookcalendarsync.utils.SharedPreferencesUtils;


class SyncAdapter extends AbstractThreadedSyncAdapter implements GraphRequest.Callback {

    private Context mContext;
    private NetworkUtils mNetworkUtils;
    private CalendarUtils mCalendarUtils;
    private AccountUtils mAccountUtils;
    private SharedPreferencesUtils mSharedPreferencesUtils;
    private NotificationUtils mNotificationUtils;
    private DatabaseUtils mDatabaseUtils;

    SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        mCalendarUtils = new CalendarUtils(mContext, mNotificationUtils);
        mSharedPreferencesUtils = new SharedPreferencesUtils(mContext);
        mNotificationUtils = new NotificationUtils(mContext);
        mAccountUtils = new AccountUtils(mContext, mNotificationUtils);
        mDatabaseUtils = new DatabaseUtils(mContext);
        mNetworkUtils = new NetworkUtils(mContext, mAccountUtils, mNotificationUtils, mDatabaseUtils);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        // TODO check for network

        if (mAccountUtils.hasEmptyOrExpiredAccessToken()) {
            mNotificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_facebook_problem_message_short,
                    R.string.notification_facebook_problem_message_long);
        } else {
            Log.e("SyncAdapter", "onPerformSync");

            Integer calId = mCalendarUtils.checkDoesCalendarExistAndGetCalId();
            if (calId == null) {
                Uri uri = mCalendarUtils.createCalendar();
                // TODO do a check on the Uri
                if (uri == null) {
                    // TODO Crashlytics
                    Log.e("SyncAdapter", "Error creating calendar");
                }
            }

            if (mSharedPreferencesUtils.getSyncOnlyUpcoming()) {
                mNetworkUtils.fetchUpcomingEvents(this);
            } else {
                mNetworkUtils.fetchAllEvents(this);
            }

            // TODO set last sync time in shared preferences (or do it on Facebook response),
            // or use http://stackoverflow.com/questions/6622316/how-to-know-when-sync-is-finished
        }
    }


    @Override
    public void onCompleted(GraphResponse response) {
        if (response.getError() != null) {
            mAccountUtils.removeTokenFromAccountManager();
            LoginManager.getInstance().logOut();
            mNotificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_facebook_problem_message_short,
                    R.string.notification_facebook_problem_message_long);

            Log.e("SyncAdapter", "Facebook response error: " + response.getError().getErrorMessage());
        } else {
            Log.e("SyncAdapter", response.getJSONObject().toString());
            EventsResponse eventsResponse = parseAndValidateFacebookResponse(response);
            if (eventsResponse.getEvents().size() != 0) {
                mDatabaseUtils.insertAndUpdateCalendarEvents(mDatabaseUtils.convertCalendarEvents(eventsResponse.getEvents()));

//                Integer calId = mCalendarUtils.checkDoesCalendarExistAndGetCalId();
//                if (calId != null) {
//                    mCalendarUtils.removeEventsFromCalendar(calId); // TODO optimise this
//                    mCalendarUtils.addEventsToCalendar(eventsResponse, calId);
//                } else {
//                    // TODO Crashlytics
//                    Log.e("Syncadapter", "No calendar exists for this account");
//                    return;
//                }
                mNetworkUtils.requestNextPage(response, this);
            } else {
                Log.e("Syncadapter", "Facebook response contained no events");
            }
        }
    }
    //endregion

    //region Helper methods

    //endregion

    //region Validation Helper Methods
    private EventsResponse parseAndValidateFacebookResponse(GraphResponse response) {
        return FacebookGetUserWithEventsResponse.parseJSON(response.getJSONObject().toString());
    }
    //endregion
}
