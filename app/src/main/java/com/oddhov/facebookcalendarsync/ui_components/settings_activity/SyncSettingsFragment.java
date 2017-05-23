package com.oddhov.facebookcalendarsync.ui_components.settings_activity;

import android.content.DialogInterface;
import android.os.Bundle;
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
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.data.events.NavigateBackEvent;
import com.oddhov.facebookcalendarsync.data.exceptions.RealmException;
import com.oddhov.facebookcalendarsync.data.exceptions.UnexpectedException;
import com.oddhov.facebookcalendarsync.data.models.CustomTime;
import com.oddhov.facebookcalendarsync.ui_components.settings_activity.base.SettingsBaseFragment;
import com.oddhov.facebookcalendarsync.utils.DatabaseUtils;
import com.oddhov.facebookcalendarsync.utils.SyncAdapterUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SyncSettingsFragment extends SettingsBaseFragment implements DialogInterface.OnClickListener {
    // region Fields
    public static final String TAG = "SyncSettingsFragment";

    @Inject
    DatabaseUtils mDatabaseUtils;
    @Inject
    SyncAdapterUtils mSyncAdapterUtils;

    @BindView(R.id.swWifiOnly)
    SwitchCompat swWifiOnly;
    @BindView(R.id.swShowNotifications)
    SwitchCompat swShowNotifications;
    @BindView(R.id.llSyncInterval)
    LinearLayout llSyncInterval;
    @BindView(R.id.tvSyncInterval)
    TextView tvSyncInterval;

    private Unbinder mUnbinder;

    private boolean mSettingsChanged;
    // endregion

    // region Lifecycle methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sync_settings, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mSettingsChanged = savedInstanceState.getBoolean(Constants.SETTINGS_CHANGED);
        }
        initializeInjector();
        setupViews();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.SETTINGS_CHANGED, mSettingsChanged);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
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
        mSettingsChanged = true;
    }

    @OnClick(R.id.swWifiOnly)
    public void onWifiOnlyClicked() {
        try {
            mDatabaseUtils.setSyncWifiOnly(swWifiOnly.isChecked());
        } catch (RealmException e) {
            Crashlytics.logException(e);
        }
        mSettingsChanged = true;
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
                    mDatabaseUtils.setSyncInterval(CustomTime.ONE_HOUR);
                    mSyncAdapterUtils.setSyncAdapterRunInterval(CustomTime.ONE_HOUR);
                    tvSyncInterval.setText(R.string.sync_settings_sync_interval_1hr);
                    mSettingsChanged = true;
                } catch (RealmException e) {
                    Crashlytics.logException(e);
                }
                break;
            case 1:
                try {
                    mDatabaseUtils.setSyncInterval(CustomTime.SIX_HOURS);
                    mSyncAdapterUtils.setSyncAdapterRunInterval(CustomTime.SIX_HOURS);
                    tvSyncInterval.setText(R.string.sync_settings_sync_interval_6hrs);
                    mSettingsChanged = true;
                } catch (RealmException e) {
                    Crashlytics.logException(e);
                }
                break;
            case 2:
                try {
                    mDatabaseUtils.setSyncInterval(CustomTime.TWELVE_HOURS);
                    mSyncAdapterUtils.setSyncAdapterRunInterval(CustomTime.TWELVE_HOURS);
                    tvSyncInterval.setText(R.string.sync_settings_sync_interval_12hrs);
                    mSettingsChanged = true;
                } catch (RealmException e) {
                    Crashlytics.logException(e);
                }
                break;
            case 3:
                try {
                    mDatabaseUtils.setSyncInterval(CustomTime.TWENTY_FOUR_HOURS);
                    mSyncAdapterUtils.setSyncAdapterRunInterval(CustomTime.TWENTY_FOUR_HOURS);
                    tvSyncInterval.setText(R.string.sync_settings_sync_interval_24hrs);
                    mSettingsChanged = true;
                } catch (RealmException e) {
                    Crashlytics.logException(e);
                }
                break;
            default:
                break;
        }
    }
    // endregion

    // region EventBus methods
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNavigateBackEvent(NavigateBackEvent event) {
        if (mSettingsChanged) {
            showSettingsChangedDialog();
        } else {
            navigateBack();
        }
    }
    // endregion

    // region Helper Methods Dagger
    private void initializeInjector() {
        ((SettingsActivity) getActivity()).getComponent().inject(this);
    }
    // endregion

    // region Helper Methods (UI)
    private void setupViews() {
        try {
            swWifiOnly.setChecked(mDatabaseUtils.getSyncWifiOnly());

            swShowNotifications.setChecked(mDatabaseUtils.getShowNotifications());

            CustomTime syncInterval = mDatabaseUtils.getSyncInterval();
            switch (syncInterval) {
                case ONE_HOUR:
                    tvSyncInterval.setText(R.string.sync_settings_sync_interval_1hr);
                    break;
                case SIX_HOURS:
                    tvSyncInterval.setText(R.string.sync_settings_sync_interval_6hrs);
                    break;
                case TWELVE_HOURS:
                    tvSyncInterval.setText(R.string.sync_settings_sync_interval_12hrs);
                    break;
                case TWENTY_FOUR_HOURS:
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
