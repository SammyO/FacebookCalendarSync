package com.oddhov.facebookcalendarsync.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.oddhov.facebookcalendarsync.R;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.data.events.SyncAdapterRanEvent;
import com.oddhov.facebookcalendarsync.data.exceptions.FacebookException;
import com.oddhov.facebookcalendarsync.data.exceptions.RealmException;
import com.oddhov.facebookcalendarsync.data.models.EventsResponse;
import com.oddhov.facebookcalendarsync.data.models.SyncRange;
import com.oddhov.facebookcalendarsync.data.models.realm_models.RealmCalendarEvent;
import com.oddhov.facebookcalendarsync.events.FacebookGetUserWithEventsResponse;
import com.oddhov.facebookcalendarsync.utils.AccountUtils;
import com.oddhov.facebookcalendarsync.utils.CalendarUtils;
import com.oddhov.facebookcalendarsync.utils.DatabaseUtils;
import com.oddhov.facebookcalendarsync.utils.NetworkUtils;
import com.oddhov.facebookcalendarsync.utils.NotificationUtils;
import com.oddhov.facebookcalendarsync.utils.PermissionUtils;
import com.oddhov.facebookcalendarsync.utils.TimeUtils;
import com.oddhov.facebookcalendarsync.utils.UtilsModule;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

class SyncAdapter extends AbstractThreadedSyncAdapter implements GraphRequest.Callback {

    @Inject
    NetworkUtils mNetworkUtils;
    @Inject
    CalendarUtils mCalendarUtils;
    @Inject
    NotificationUtils mNotificationUtils;
    @Inject
    DatabaseUtils mDatabaseUtils;
    @Inject
    TimeUtils mTimeUtils;
    @Inject
    PermissionUtils mPermissionUtils;

    private SyncAdapterComponent mSyncAdapterComponent;
    private Context mContext;

    private List<RealmCalendarEvent> mUpdatedEvents;

    SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        initializeInjector();

        FacebookSdk.sdkInitialize(getContext());
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        // TODO check for network
        Log.e("Facebook Calendar Sync", "Running SyncAdapter");

        if (mPermissionUtils.needsPermissions()) {
            mNotificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_missing_permissions_message_short,
                    R.string.notification_missing_permissions_message_long);
        }

        try {
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                if ((activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) ||
                        ((activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) && !mDatabaseUtils.getSyncWifiOnly())) {
                    /*
                     * User is connected to wifi, or on a mobile data connection,
                     * but the sync-wifi-only option hasn't been set, so we can sync
                     */
                    if (AccountUtils.hasEmptyOrExpiredAccessToken()) {
                        mNotificationUtils.sendNotification(
                                R.string.notification_syncing_problem_title,
                                R.string.notification_facebook_problem_message_short,
                                R.string.notification_facebook_problem_message_long);
                    } else {
                        mCalendarUtils.ensureCalendarExists();
                        mUpdatedEvents = new ArrayList<>();

                        if (mDatabaseUtils.getSyncRange() == SyncRange.SYNC_UPCOMING) {
                            mNetworkUtils.fetchUpcomingEvents(this);
                        } else {
                            mNetworkUtils.fetchAllEvents(this);
                        }

                        String dateAndTime = mTimeUtils.getCurrentDateAndTime();
                        Long dateAndTimeLong = mTimeUtils.convertDateToEpochFormat(dateAndTime);
                        mDatabaseUtils.setLastSynced(dateAndTimeLong);

                        sendSyncAdapterRanIntent();
                        EventBus.getDefault().post(new SyncAdapterRanEvent());

                        // TODO remove this, just for testing
                        mNotificationUtils.sendNotification(
                                "Syncing done",
                                "Just performed a sync",
                                "Current sync interval: " + mDatabaseUtils.getSyncInterval());
                    }
                }
            }
        } catch (RealmException e) {
            Crashlytics.logException(e);
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

            Crashlytics.logException(new FacebookException("SyncAdapter", "Facebook response error: " +
                    response.getError().getErrorMessage()));
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
                Crashlytics.logException(new FacebookException("SyncAdapter", "Facebook response contained" +
                        " no events"));
            }
        }
    }
    //endregion

    // region Helper Methods
    private void initializeInjector() {
        this.mSyncAdapterComponent = DaggerSyncAdapterComponent.builder()
                .syncAdapterModule(new SyncAdapterModule(mContext))
                .utilsModule(new UtilsModule())
                .build();
        this.mSyncAdapterComponent.inject(this);
    }
    // endregion

    //region Validation Helper Methods
    private EventsResponse parseAndValidateFacebookResponse(GraphResponse response) {
        return FacebookGetUserWithEventsResponse.parseJSON(response.getJSONObject().toString());
    }

    private void sendSyncAdapterRanIntent() {
        Intent sendIntent = new Intent("com.oddhov.facebookcalendarsync");
        sendIntent.putExtra(Constants.SYNC_ADAPTER_RAN_EXTRA, true);
        getContext().sendBroadcast(sendIntent);
    }
    //endregion
}
