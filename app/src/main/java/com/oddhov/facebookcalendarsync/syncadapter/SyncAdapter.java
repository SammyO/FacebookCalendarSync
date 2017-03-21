package com.oddhov.facebookcalendarsync.syncadapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.events.FacebookGetUserWithEventsResponse;
import com.oddhov.facebookcalendarsync.models.EventsResponse;
import com.oddhov.facebookcalendarsync.utils.AccountManagerUtils;
import com.oddhov.facebookcalendarsync.utils.CalendarUtils;
import com.oddhov.facebookcalendarsync.utils.NetworkUtils;
import com.oddhov.facebookcalendarsync.utils.SharedPreferencesUtils;


class SyncAdapter extends AbstractThreadedSyncAdapter implements GraphRequest.Callback {

    private Context mContext;
    private NetworkUtils mNetworkUtils;
    private CalendarUtils mCalendarUtils;
    private AccountManagerUtils mAccountManagerUtils;
    private SharedPreferencesUtils mSharedPreferencesUtils;

    SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        mCalendarUtils = new CalendarUtils(mContext);
        mAccountManagerUtils = new AccountManagerUtils(mContext);
        mNetworkUtils = new NetworkUtils(mContext, mAccountManagerUtils);
        mSharedPreferencesUtils = new SharedPreferencesUtils(mContext);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        // TODO check for network
        // TODO check for login status

        Log.e("SyncAdapter", "onPerformSync");

        String ownerAccount = mAccountManagerUtils.getPrimaryAccount();

        if (ownerAccount != null) {
            Integer calId = mCalendarUtils.checkDoesCalendarExistAndGetCalId(ownerAccount);
            if (calId == null) {
                Uri uri = mCalendarUtils.createCalendar(ownerAccount);
                // TODO do a check on the Uri
                if (mCalendarUtils.createCalendar(ownerAccount) == null) {
                    // TODO
                    Log.e("SyncAdapter", "Error creating calendar");
                }
            }

            if (mSharedPreferencesUtils.getSyncOnlyUpcoming()) {
                mNetworkUtils.fetchUpcomingEvents(this);
            } else {
                mNetworkUtils.fetchAllEvents(this);
            }
        } else {
            //TODO
            Log.e("Syncadapter", "No primary account specified on phone");
        }

        // TODO set last sync time in shared preferences (or do it on Facebook response),
        // or use http://stackoverflow.com/questions/6622316/how-to-know-when-sync-is-finished
    }


    @Override
    public void onCompleted(GraphResponse response) {
        if (response.getError() != null) {
            Log.e("SyncAdapter", "Facebook response error: " + response.getError().getErrorMessage());
            // TODO error handling
        } else {
            Log.e("SyncAdapter", response.getJSONObject().toString());
            EventsResponse eventsResponse = parseAndValidateFacebookResponse(response);
            if (eventsResponse.getEvents().size() != 0) {
                String ownerAccount = mAccountManagerUtils.getPrimaryAccount();
                if (ownerAccount != null) {
                    Integer calId = mCalendarUtils.checkDoesCalendarExistAndGetCalId(ownerAccount);
                    if (calId != null) {
                        mCalendarUtils.removeEventsFromCalendar(calId); // TODO optimise this
                        mCalendarUtils.addEventsToCalendar(eventsResponse, calId);
                    } else {
                        // TODO this should never happen. Show notification
                        Log.e("Syncadapter", "No calendar exists for this account");
                        return;
                    }
                } else {
                    //TODO
                    Log.e("Syncadapter", "No primary account specified on phone");
                    return;
                }
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
