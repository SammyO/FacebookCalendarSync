package com.oddhov.facebookcalendarsync.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.oddhov.facebookcalendarsync.R;
import com.oddhov.facebookcalendarsync.data.models.EventsResponse;
import com.oddhov.facebookcalendarsync.data.realm_models.RealmCalendarEvent;
import com.oddhov.facebookcalendarsync.events.FacebookGetUserWithEventsResponse;
import com.oddhov.facebookcalendarsync.utils.AccountUtils;
import com.oddhov.facebookcalendarsync.utils.CalendarUtils;
import com.oddhov.facebookcalendarsync.utils.DatabaseUtils;
import com.oddhov.facebookcalendarsync.utils.NetworkUtils;
import com.oddhov.facebookcalendarsync.utils.NotificationUtils;
import com.oddhov.facebookcalendarsync.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;


class SyncAdapter extends AbstractThreadedSyncAdapter implements GraphRequest.Callback {

    private Context mContext;
    private NetworkUtils mNetworkUtils;
    private CalendarUtils mCalendarUtils;
    private SharedPreferencesUtils mSharedPreferencesUtils;
    private NotificationUtils mNotificationUtils;
    private DatabaseUtils mDatabaseUtils;
    private List<RealmCalendarEvent> mUpdatedEvents;

    SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        mDatabaseUtils = new DatabaseUtils(mContext);
        mNotificationUtils = new NotificationUtils(mContext);
        mSharedPreferencesUtils = new SharedPreferencesUtils(mContext);
        mCalendarUtils = new CalendarUtils(mContext, mNotificationUtils, mDatabaseUtils);
        mNetworkUtils = new NetworkUtils(mContext, mNotificationUtils, mDatabaseUtils);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        // TODO check for network
        Log.e("SyncAdapter", "onPerformSync");

        if (AccountUtils.hasEmptyOrExpiredAccessToken()) {
            mNotificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_facebook_problem_message_short,
                    R.string.notification_facebook_problem_message_long);
        } else {
            mCalendarUtils.ensureCalendarExists();
            mUpdatedEvents = new ArrayList<>();

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
            LoginManager.getInstance().logOut();
            mNotificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_facebook_problem_message_short,
                    R.string.notification_facebook_problem_message_long);

            Log.e("SyncAdapter", "Facebook response error: " + response.getError().getErrorMessage());
        } else {
            EventsResponse eventsResponse = parseAndValidateFacebookResponse(response);
            if (eventsResponse.getEvents().size() != 0) {
                 List<RealmCalendarEvent> updatedEvents = mDatabaseUtils.updateCalendarEvents(
                        mDatabaseUtils.convertToRealmCalendarEvents(eventsResponse.getEvents()));

                if (updatedEvents != null) {
                    mUpdatedEvents.addAll(updatedEvents);
                }

                if (!mNetworkUtils.requestNextPage(response, this)) {
                    /*
                     * This was the last page of events, so we can continue with updating the calendar
                     */
                    mCalendarUtils.insertOrUpdateCalendarEvents(mCalendarUtils.getCalendarId(), mUpdatedEvents);
                    mCalendarUtils.deleteMissingCalendarEvents(mCalendarUtils.getCalendarId(), mUpdatedEvents);
                    Log.i(getContext().getString(R.string.app_name), "Updating events finished");
                }
            } else {
                Log.e("Syncadapter", "Facebook response contained no events");
            }
        }
    }
    //endregion

    //region Validation Helper Methods
    private EventsResponse parseAndValidateFacebookResponse(GraphResponse response) {
        return FacebookGetUserWithEventsResponse.parseJSON(response.getJSONObject().toString());
    }
    //endregion
}
