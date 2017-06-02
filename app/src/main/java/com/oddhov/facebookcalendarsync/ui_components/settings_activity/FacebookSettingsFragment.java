package com.oddhov.facebookcalendarsync.ui_components.settings_activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.crashlytics.android.Crashlytics;
import com.oddhov.facebookcalendarsync.R;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.data.events.NavigateBackEvent;
import com.oddhov.facebookcalendarsync.data.exceptions.RealmException;
import com.oddhov.facebookcalendarsync.data.models.realm_models.RsvpSyncPreference;
import com.oddhov.facebookcalendarsync.ui_components.settings_activity.base.SettingsBaseFragment;
import com.oddhov.facebookcalendarsync.utils.DatabaseUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FacebookSettingsFragment extends SettingsBaseFragment implements RadioGroup.OnCheckedChangeListener,
        DialogInterface.OnMultiChoiceClickListener {
    // region Fields
    public static final String TAG = "FacebookSettingsFragment";

    @Inject
    DatabaseUtils mDatabaseUtils;

    @BindView(R.id.llSetupRsvpRange)
    LinearLayout llSetupRsvpRange;
    @BindView(R.id.rgSyncRange)
    RadioGroup rgSyncRange;
    @BindView(R.id.rbSyncRangeAll)
    RadioButton rbSynAll;
    @BindView(R.id.rbSyncRangeUpcoming)
    RadioButton rbSynUpcoming;

    private Unbinder mUnbinder;

    private boolean mSettingsChanged;
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
    @OnClick(R.id.llSetupRsvpRange)
    public void onSetupRsvpRange() {
        showRsvpOptionsDialog();
    }
    // endregion

    // region Interface RadioGroup.OnCheckedChangeListener
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int which) {
        switch (which) {
            case R.id.rbSyncRangeAll:
                try {
                    mDatabaseUtils.setSyncOnlyUpcoming(false);
                    mSettingsChanged = true;
                } catch (RealmException e) {
                    Crashlytics.logException(e);
                }
                break;
            case R.id.rbSyncRangeUpcoming:
                try {
                    mDatabaseUtils.setSyncOnlyUpcoming(true);
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

    // region DialogInterface.OnMultiChoiceClickListener
    @Override
    public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
        try {
            if (which <= mDatabaseUtils.getRsvpSyncPreferences().size()) {
                mDatabaseUtils.setRsvpSyncPreference(which, isChecked);
            }
        } catch (RealmException e) {
            e.printStackTrace();
        }
        mSettingsChanged = true;
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
            if (mDatabaseUtils.isSyncOnlyUpcoming()) {
                rbSynUpcoming.setChecked(true);
            } else {
                rbSynAll.setChecked(true);
            }
            rgSyncRange.setOnCheckedChangeListener(this);

        } catch (RealmException e) {
            e.printStackTrace();
        }
    }

    private void showRsvpOptionsDialog() {
        try {
            final CharSequence[] rsvpOptions = {
                    getString(R.string.facebook_settings_rsvp_range_attending),
                    getString(R.string.facebook_settings_rsvp_range_interested),
                    getString(R.string.facebook_settings_rsvp_range_not_replied),
                    getString(R.string.facebook_settings_rsvp_range_declined)};

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.facebook_settings_rsvp_range);
            List<RsvpSyncPreference> rsvpSyncPreferences = mDatabaseUtils.getRsvpSyncPreferences();
            boolean[] rsvpSyncPreferencesBooleans = new boolean[rsvpSyncPreferences.size()];
            int i = 0;
            for (RsvpSyncPreference rsvpSyncPreference : rsvpSyncPreferences) {
                rsvpSyncPreferencesBooleans[i++] = rsvpSyncPreference.isSet();
            }
            builder.setMultiChoiceItems(rsvpOptions, rsvpSyncPreferencesBooleans, this);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.show();
        } catch (RealmException e) {
            Crashlytics.logException(e);
        }

    }
    // endregion
}
