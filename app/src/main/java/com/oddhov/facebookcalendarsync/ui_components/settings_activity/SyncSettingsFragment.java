package com.oddhov.facebookcalendarsync.ui_components.settings_activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.oddhov.facebookcalendarsync.R;
import com.oddhov.facebookcalendarsync.data.exceptions.RealmException;
import com.oddhov.facebookcalendarsync.data.exceptions.UnexpectedException;
import com.oddhov.facebookcalendarsync.utils.DatabaseUtils;
import com.oddhov.facebookcalendarsync.utils.SyncAdapterUtils;

public class SyncSettingsFragment extends Fragment implements View.OnClickListener, DialogInterface.OnClickListener {
    // region Fields
    public static final String TAG = "SyncSettingsFragment";

    private DatabaseUtils mDatabaseUtils;
    private SyncAdapterUtils mSyncAdapterUtils;

    private SwitchCompat swWifiOnly;
    private SwitchCompat swNotifications;
    private LinearLayout llSyncInterval;
    private TextView tvSyncInterval;
    // endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDatabaseUtils = new DatabaseUtils(getActivity());

        View view = inflater.inflate(R.layout.fragment_sync_settings, container, false);
        setupViews(view);
        return view;
    }

    // region Interface View.OnClickListener
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.swWifiOnly:
                try {
                    mDatabaseUtils.setSyncWifiOnly(swWifiOnly.isChecked());
                } catch (RealmException e) {
                    Crashlytics.logException(e);
                }
                Log.e("SyncSettings", "click wifi only");
                break;
            case R.id.swShowNotifications:
                try {
                    mDatabaseUtils.setShowNotifications(swNotifications.isChecked());
                } catch (RealmException e) {
                    Crashlytics.logException(e);
                }
                Log.e("SyncSettings", "click notifications");
                break;
            case R.id.llSyncInterval:
                CharSequence syncIntervals[] = new CharSequence[]{
                        getString(R.string.sync_settings_sync_interval_1hr),
                        getString(R.string.sync_settings_sync_interval_6hrs),
                        getString(R.string.sync_settings_sync_interval_12hrs),
                        getString(R.string.sync_settings_sync_interval_24hrs)};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.sync_settings_sync_interval);
                builder.setItems(syncIntervals, this);
                builder.show();
                break;
            default:
                break;
        }
    }
    // endregion

    // region interface DialogInterface.OnClickListener
    @Override
    public void onClick(DialogInterface dialogInterface, int option) {
        switch (option) {
            case 0:
                try {
                    mDatabaseUtils.setSyncInterval(1);
                    mSyncAdapterUtils.setSyncAdapterRunInterval(1);
                    tvSyncInterval.setText(R.string.sync_settings_sync_interval_1hr);
                } catch (RealmException e) {
                    Crashlytics.logException(e);
                }
                break;
            case 1:
                try {
                    mDatabaseUtils.setSyncInterval(6);
                    mSyncAdapterUtils.setSyncAdapterRunInterval(6);
                    tvSyncInterval.setText(R.string.sync_settings_sync_interval_6hrs);
                } catch (RealmException e) {
                    Crashlytics.logException(e);
                }
                break;
            case 2:
                try {
                    mDatabaseUtils.setSyncInterval(12);
                    mSyncAdapterUtils.setSyncAdapterRunInterval(12);
                    tvSyncInterval.setText(R.string.sync_settings_sync_interval_12hrs);
                } catch (RealmException e) {
                    Crashlytics.logException(e);
                }
                break;
            case 3:
                try {
                    mDatabaseUtils.setSyncInterval(24);
                    mSyncAdapterUtils.setSyncAdapterRunInterval(24);
                    tvSyncInterval.setText(R.string.sync_settings_sync_interval_24hrs);
                } catch (RealmException e) {
                    Crashlytics.logException(e);
                }
                break;
            default:
                break;
        }
    }
    // endregion

    // region Helper Methods (UI)
    private void setupViews(View view) {
        try {
            swWifiOnly = (SwitchCompat) view.findViewById(R.id.swWifiOnly);
            swWifiOnly.setChecked(mDatabaseUtils.getIsSyncWifiOnly());
            swWifiOnly.setOnClickListener(this);

            swNotifications = (SwitchCompat) view.findViewById(R.id.swShowNotifications);
            swNotifications.setChecked(mDatabaseUtils.getShowNotifications());
            swNotifications.setOnClickListener(this);

            tvSyncInterval = (TextView) view.findViewById(R.id.tvSyncInterval);

            llSyncInterval = (LinearLayout) view.findViewById(R.id.llSyncInterval);
            int syncInterval = mDatabaseUtils.getSyncInterval();
            switch (syncInterval) {
                case 1:
                    tvSyncInterval.setText(R.string.sync_settings_sync_interval_1hr);
                    break;
                case 6:
                    tvSyncInterval.setText(R.string.sync_settings_sync_interval_6hrs);
                    break;
                case 12:
                    tvSyncInterval.setText(R.string.sync_settings_sync_interval_12hrs);
                    break;
                case 24:
                    tvSyncInterval.setText(R.string.sync_settings_sync_interval_24hrs);
                    break;
                default:
                    Log.e("SyncSettingsFragment", "Unexpected sync interval in Realm");
                    Crashlytics.logException(new UnexpectedException("SyncSettingsFragment",
                            "Unexpected sync interval in Realm"));
                    break;
            }
            llSyncInterval.setOnClickListener(this);
        } catch (RealmException e) {
            e.printStackTrace();
        }
    }
    // endregion
}
