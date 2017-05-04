package com.oddhov.facebookcalendarsync;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncStatusObserver;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.data.exceptions.RealmException;
import com.oddhov.facebookcalendarsync.utils.AccountUtils;
import com.oddhov.facebookcalendarsync.utils.DatabaseUtils;
import com.oddhov.facebookcalendarsync.utils.PermissionUtils;
import com.oddhov.facebookcalendarsync.utils.TimeUtils;

public class SyncFragment extends Fragment implements View.OnClickListener, SyncStatusObserver {

    public static final String TAG = "SyncFragment";

    private Button btnSyncNow;
    private NavigationListener mNavigationListenerCallback;
    private PermissionUtils mPermissionUtils;
    private DatabaseUtils mDatabaseUtils;
    private AccountUtils mAccountUtils;
    private TimeUtils mTimeUtils;
    private TextView tvLastSynced;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sync_fragment, container, false);

        btnSyncNow = (Button) view.findViewById(R.id.btnSynNow);
        btnSyncNow.setOnClickListener(this);

        mPermissionUtils = new PermissionUtils(getActivity());
        mDatabaseUtils = new DatabaseUtils(getActivity());
        mTimeUtils = new TimeUtils();

        tvLastSynced = (TextView) view.findViewById(R.id.tvLastSyncedValue);
        try {
            tvLastSynced.setText(mTimeUtils.convertEpochFormatToDate(mDatabaseUtils.getLastSynced()));
        } catch (RealmException e) {
            Crashlytics.logException(e);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mNavigationListenerCallback = (NavigationListener) context;
        } catch (ClassCastException e) {
            Log.e(TAG, context.toString()
                    + " must implement LoginNavigationListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AccountUtils.hasEmptyOrExpiredAccessToken() || mPermissionUtils.needsPermissions()) {
            mNavigationListenerCallback.navigate();
        }
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE | ContentResolver.SYNC_OBSERVER_TYPE_PENDING;
        ContentResolver.addStatusChangeListener(mask, this);

    }

    // region SyncStatusObserver
    @Override
    public void onStatusChanged(int which) {
        AccountManager accountManager = AccountManager.get(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            Log.e(getString(R.string.app_name), "No account permissions granted");
            return;
        }
        Account account = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE)[0];

        Log.d(getString(R.string.app_name), "Sync status changed: " + which);

        if (!ContentResolver.isSyncActive(account, "com.android.calendar") &&
                !ContentResolver.isSyncPending(account, "com.android.calendar")) {
            try {
                tvLastSynced.setText(mTimeUtils.convertEpochFormatToDate(mDatabaseUtils.getLastSynced()));
            } catch (RealmException e) {
                Crashlytics.logException(e);
            }
        }
    }
    // endregion

    // region OnClickListeners
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSynNow) {
            startSyncAdapter();
        }
    }
    // endregion

    // region Helper methods
    private void startSyncAdapter() {
        Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.setIsSyncable(account, "com.android.calendar", 1);
        ContentResolver.addPeriodicSync(account, "com.android.calendar", Bundle.EMPTY, Constants.SYNC_INTERVAL);
        ContentResolver.setSyncAutomatically(account, "com.android.calendar", true);
        ContentResolver.requestSync(account, "com.android.calendar", bundle);
    }
    // endregion
}
