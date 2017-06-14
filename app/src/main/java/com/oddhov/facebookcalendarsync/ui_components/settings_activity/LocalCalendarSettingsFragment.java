package com.oddhov.facebookcalendarsync.ui_components.settings_activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
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
import com.oddhov.facebookcalendarsync.data.models.CalendarColour;
import com.oddhov.facebookcalendarsync.data.models.realm_models.EventReminder;
import com.oddhov.facebookcalendarsync.ui_components.settings_activity.base.SettingsBaseFragment;
import com.oddhov.facebookcalendarsync.utils.CalendarUtils;
import com.oddhov.facebookcalendarsync.utils.DatabaseUtils;
import com.oddhov.facebookcalendarsync.utils.SyncAdapterUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LocalCalendarSettingsFragment extends SettingsBaseFragment implements RadioGroup.OnCheckedChangeListener,
        DialogInterface.OnMultiChoiceClickListener {

    // region Fields
    public static final String TAG = "LocalCalendarSettingsFragment";

    @Inject
    DatabaseUtils mDatabaseUtils;
    @Inject
    CalendarUtils mCalendarUtils;
    @Inject
    SyncAdapterUtils mSyncAdapterUtils;

    @BindView(R.id.swReminders)
    SwitchCompat swReminders;
    @BindView(R.id.llSetupReminderTime)
    LinearLayout llSetupReminderTime;
    @BindView(R.id.rgCalendarColor)
    RadioGroup rgCalendarColor;
    @BindView(R.id.rbCalendarColorRed)
    RadioButton rbCalendarColorRed;
    @BindView(R.id.rbCalendarColorGreen)
    RadioButton rbCalendarColorGreen;
    @BindView(R.id.rbCalendarColorOrange)
    RadioButton rbCalendarColorOrange;
    @BindView(R.id.rbCalendarColorPurple)
    RadioButton rbCalendarColorPurple;
    @BindView(R.id.rbCalendarColorBlue)
    RadioButton rbCalendarColorBlue;

    private Unbinder mUnbinder;

    private boolean mSettingsChanged;
    // endregion

    // region Lifecycle methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_calendar_settings, container, false);
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
    @OnClick(R.id.swReminders)
    public void onShowRemindersClicked() {
        try {
            if (swReminders.isChecked()) {
                mDatabaseUtils.setShowReminders(true);
                llSetupReminderTime.setVisibility(View.VISIBLE);

            } else {
                mDatabaseUtils.setShowReminders(false);
                llSetupReminderTime.setVisibility(View.GONE);
            }
        } catch (RealmException e) {
            Crashlytics.logException(e);
        }
        mSettingsChanged = true;
    }

    @OnClick(R.id.llSetupReminderTime)
    public void onSetupReminderTimeClicked() {
        showReminderTimesDialog();
    }
    // endregion

    // region Interface RadioGroup.OnCheckedChangeListener
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int which) {
        CalendarColour colorValue;
        switch (which) {
            case R.id.rbCalendarColorRed:
                colorValue = CalendarColour.RED;
                break;
            case R.id.rbCalendarColorGreen:
                colorValue = CalendarColour.GREEN;
                break;
            case R.id.rbCalendarColorOrange:
                colorValue = CalendarColour.ORANGE;
                break;
            case R.id.rbCalendarColorBlue:
                colorValue = CalendarColour.BLUE;
                break;
            case R.id.rbCalendarColorPurple:
            default:
                colorValue = CalendarColour.PURPLE;
                break;
        }

        try {
            mDatabaseUtils.setCalendarColor(colorValue);
            mCalendarUtils.deleteCalendar();
            mCalendarUtils.ensureCalendarExists();
            mSyncAdapterUtils.runSyncAdapterNow();
        } catch (RealmException e) {
            Crashlytics.logException(e);
        }
        mSettingsChanged = true;
    }
    // endregion

    // region DialogInterface.OnMultiChoiceClickListener
    @Override
    public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
        try {
            if (which <= mDatabaseUtils.getAllReminderTimes().size()) {
                mDatabaseUtils.setReminderTime(which, isChecked);
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
            swReminders.setChecked(mDatabaseUtils.getShowReminders());

            if (mDatabaseUtils.getShowReminders()) {
                llSetupReminderTime.setVisibility(View.VISIBLE);
            } else {
                llSetupReminderTime.setVisibility(View.GONE);
            }

            switch (mDatabaseUtils.getCalendarColor()) {
                case RED:
                    rbCalendarColorRed.setChecked(true);
                    break;
                case GREEN:
                    rbCalendarColorGreen.setChecked(true);
                    break;
                case ORANGE:
                    rbCalendarColorOrange.setChecked(true);
                    break;
                case PURPLE:
                    rbCalendarColorPurple.setChecked(true);
                    break;
                case BLUE:
                    rbCalendarColorBlue.setChecked(true);
                    break;
                default:
                    break;
            }
            rgCalendarColor.setOnCheckedChangeListener(this);
        } catch (RealmException e) {
            e.printStackTrace();
        }
    }

    private void showReminderTimesDialog() {
        try {
            final List<CharSequence> reminderTimes = new ArrayList<>();
            List<EventReminder> eventReminders = mDatabaseUtils.getAllReminderTimes();
            for (EventReminder eventReminder : eventReminders) {
                reminderTimes.add(eventReminder.getEnum().getTimeInMinutesDisplayString());
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.sync_settings_sync_interval);
            boolean[] eventReminderBooleans = new boolean[eventReminders.size()];
            int i = 0;
            for (EventReminder eventReminder : eventReminders) {
                eventReminderBooleans[i++] = eventReminder.isIsSet();
            }
            builder.setMultiChoiceItems(reminderTimes.toArray(new CharSequence[reminderTimes.size()]),
                    eventReminderBooleans, this);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.show();
        } catch (RealmException e) {
            Crashlytics.logException(e);
        }
    }
    // endregion
}
