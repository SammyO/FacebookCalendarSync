package com.oddhov.facebookcalendarsync.syncadapter;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.oddhov.facebookcalendarsync.data.Constants;

public class SyncAdapter extends AbstractThreadedSyncAdapter implements GraphRequest.Callback {
    private Context mContext;
    private AccountManager mAccountManager;

    private static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };

    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        mAccountManager = AccountManager.get(context);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        // TODO check for network
        // TODO check for login status
        fetchAllEvents();
    }


    @Override
    public void onCompleted(GraphResponse response) {
        if (response.getError() != null) {
            Log.e("SyncAdapter", "Facebook response error: " + response.getError().getErrorMessage());
            // TODO error handling
        } else {
            Log.e("SyncAdapter", response.getJSONObject().toString());
        }
    }
    //endregion

    //region Helper methods Facebook
    private void fetchAllEvents() {
        // TODO move this to Authenticator
        FacebookSdk.sdkInitialize(mContext);

        if (Profile.getCurrentProfile() != null) {

            Bundle parameters = new Bundle();
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + Profile.getCurrentProfile().getId() + "/events",
                    parameters,
                    HttpMethod.GET,
                    this)
                    .executeAsync();

        } else {
            Log.e("SyncAdapter", "Facebook profile not loaded.");
            // TODO notify user that new login is required
        }
    }
    //endregion
}
