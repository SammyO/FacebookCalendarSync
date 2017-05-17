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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SyncSettingsFragment extends Fragment implements DialogInterface.OnClickListener {
    // region Fields
    public static final String TAG = "SyncSettingsFragment";

    private DatabaseUtils mDatabaseUtils;
    private SyncAdapterUtils mSyncAdapterUtils;

    private Unbinder mUnbinder;

    @BindView(R.id.swWifiOnly)
    SwitchCompat swWifiOnly;
    @BindView(R.id.swShowNotifications)
    SwitchCompat swShowNotifications;
    @BindView(R.id.llSyncInterval)
    LinearLayout llSyncInterval;
    @BindView(R.id.tvSyncInterval)
    TextView tvSyncInterval;
    // endregion

    // region Lifecycle methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDatabaseUtils = new DatabaseUtils(getActivity());
        mSyncAdapterUtils = new SyncAdapterUtils();

        View view = inflater.inflate(R.layout.fragment_sync_settings, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        setupViews(view);
        return view;
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }
    // endregion

    // region VI methods
    @OnClick(R.id.swShowNotifications)
    public void onShowNotificationsClicked() {
        try {
            mDatabaseUtils.setShowNotifications(swShowNotifications.isChecked());
        } catch (RealmException e) {
            Crashlytics.logException(e);
        }
    }

    @OnClick(R.id.swWifiOnly)
    public void onWifiOnlyCliced() {
        try {
            mDatabaseUtils.setSyncWifiOnly(swWifiOnly.isChecked());
        } catch (RealmException e) {
            Crashlytics.logException(e);
        }
    }

    @OnClick(R.id.llSyncInterval)
    public void onSyncIntervalClicked() {
        CharSequence syncIntervals[] = new CharSequence[]{
                getString(R.string.sync_settings_sync_interval_1hr),
                getString(R.string.sync_settings_sync_interval_6hrs),
                getString(R.string.sync_settings_sync_interval_12hrs),
                getString(R.string.sync_settings_sync_interval_24hrs)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.sync_settings_sync_interval);
        builder.setItems(syncIntervals, this);
        builder.show();
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
            swWifiOnly.setChecked(mDatabaseUtils.getSyncWifiOnly());

            swShowNotifications.setChecked(mDatabaseUtils.getShowNotifications());

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
        } catch (RealmException e) {
            e.printStackTrace();
        }
    }
    // endregion
}
