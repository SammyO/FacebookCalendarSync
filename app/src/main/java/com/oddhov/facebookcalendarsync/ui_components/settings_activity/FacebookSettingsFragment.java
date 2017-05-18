package com.oddhov.facebookcalendarsync.ui_components.settings_activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.crashlytics.android.Crashlytics;
import com.oddhov.facebookcalendarsync.R;
import com.oddhov.facebookcalendarsync.data.exceptions.RealmException;
import com.oddhov.facebookcalendarsync.data.models.SyncRange;
import com.oddhov.facebookcalendarsync.utils.DatabaseUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FacebookSettingsFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {
    // region Fields
    public static final String TAG = "FacebookSettingsFragment";

    @Inject
    DatabaseUtils mDatabaseUtils;

    @BindView(R.id.swSyncBirthdays)
    SwitchCompat swSyncBirthdays;
    @BindView(R.id.swShowLinks)
    SwitchCompat swShowLinks;
    @BindView(R.id.rgSyncRange)
    RadioGroup rgSyncRange;
    @BindView(R.id.rbSyncRangeAll)
    RadioButton rbSynAll;
    @BindView(R.id.rbSyncRangeUpcoming)
    RadioButton rbSynUpcoming;

    private Unbinder mUnbinder;
    // endregion

    // region Lifecycle methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facebook_settings, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeInjector();
        setupViews();
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }
    // endregion

    // region VI methods
    @OnClick(R.id.swSyncBirthdays)
    public void onSyncBirthdaysClicked() {
        try {
            mDatabaseUtils.setSyncBirthdays(swSyncBirthdays.isChecked());
        } catch (RealmException e) {
            Crashlytics.logException(e);
        }
    }

    @OnClick(R.id.swShowLinks)
    public void onShowLinksClicked() {
        try {
            mDatabaseUtils.setShowLinks(swShowLinks.isChecked());
        } catch (RealmException e) {
            Crashlytics.logException(e);
        }
    }
    // endregion

    // region Interface RadioGroup.OnCheckedChangeListener
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int which) {
        switch (which) {
            case R.id.rbSyncRangeAll:
                try {
                    mDatabaseUtils.setSyncRange(SyncRange.SYNC_ALL);
                } catch (RealmException e) {
                    Crashlytics.logException(e);
                }
                break;
            case R.id.rbSyncRangeUpcoming:
                try {
                    mDatabaseUtils.setSyncRange(SyncRange.SYNC_UPCOMING);
                } catch (RealmException e) {
                    Crashlytics.logException(e);
                }
                break;
            default:
                break;
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
            swSyncBirthdays.setChecked(mDatabaseUtils.getSyncBirthdays());
            swShowLinks.setChecked(mDatabaseUtils.getShowLinks());

            rgSyncRange.setOnCheckedChangeListener(this);
            switch (mDatabaseUtils.getSyncRange()) {
                case SYNC_ALL:
                    rbSynAll.setChecked(true);
                    break;
                case SYNC_UPCOMING:
                    rbSynUpcoming.setChecked(true);
                    break;
                default:
                    break;
            }

        } catch (RealmException e) {
            e.printStackTrace();
        }
    }
    // endregion
}
