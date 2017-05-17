package com.oddhov.facebookcalendarsync.ui_components.settings_activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.oddhov.facebookcalendarsync.data.exceptions.RealmException;
import com.oddhov.facebookcalendarsync.data.models.CalendarColour;
import com.oddhov.facebookcalendarsync.data.models.realm_models.EventReminder;
import com.oddhov.facebookcalendarsync.utils.DatabaseUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.RealmList;

public class LocalCalendarSettingsFragment extends Fragment implements RadioGroup.OnCheckedChangeListener,
        DialogInterface.OnMultiChoiceClickListener {

    // region Fields
    public static final String TAG = "LocalCalendarSettingsFragment";

    private DatabaseUtils mDatabaseUtils;
    private Unbinder mUnbinder;

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
    // endregion

    // region Lifecycle methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDatabaseUtils = new DatabaseUtils(getActivity());

        View view = inflater.inflate(R.layout.fragment_local_calendar_settings, container, false);
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
    }

    @OnClick(R.id.llSetupReminderTime)
    public void onSetupReminderTimeClicked() {
        showReminderTimesDialog();
    }
    // endregion

    // region Interface RadioGroup.OnCheckedChangeListener
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int which) {
        int colorValue;
        switch (which) {
            case R.id.rbCalendarColorRed:
                colorValue = CalendarColour.RED.ordinal();
                break;
            case R.id.rbCalendarColorGreen:
                colorValue = CalendarColour.GREEN.ordinal();
                break;
            case R.id.rbCalendarColorOrange:
                colorValue = CalendarColour.ORANGE.ordinal();
                break;
            case R.id.rbCalendarColorBlue:
                colorValue = CalendarColour.BLUE.ordinal();
                break;
            case R.id.rbCalendarColorPurple:
            default:
                colorValue = CalendarColour.PURPLE.ordinal();
                break;
        }
        try {
            mDatabaseUtils.setCalendarColor(colorValue);
        } catch (RealmException e) {
            Crashlytics.logException(e);
        }
    }
    // endregion

    // region DialogInterface..OnMultiChoiceClickListener
    @Override
    public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
        if (isChecked) {
            // TODO add item to Realm
        } else { // TODO check if item exists in Realm
            // TODO remove item from Realm
        }
    }
    // endregion

    // region Helper Methods (UI)
    private void setupViews(View view) {
        try {
            swReminders.setChecked(mDatabaseUtils.getShowReminders());

            if (mDatabaseUtils.getShowReminders()) {
                llSetupReminderTime.setVisibility(View.VISIBLE);
            } else {
                llSetupReminderTime.setVisibility(View.GONE);
            }

            rgCalendarColor.setOnCheckedChangeListener(this);
            switch (CalendarColour.values()[mDatabaseUtils.getCalendarColor()]) {
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

        } catch (RealmException e) {
            e.printStackTrace();
        }
    }

    private void showReminderTimesDialog() {
        try {
            final CharSequence[] reminderTimes = {
                    getString(R.string.local_calendar_settings_reminder_time_05h),
                    getString(R.string.local_calendar_settings_reminder_time_1h),
                    getString(R.string.local_calendar_settings_reminder_time_2hrs),
                    getString(R.string.local_calendar_settings_reminder_time_6hrs),
                    getString(R.string.local_calendar_settings_reminder_time_12hrs),
                    getString(R.string.local_calendar_settings_reminder_time_24hrs)};

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.sync_settings_sync_interval);
            RealmList<EventReminder> eventReminders = mDatabaseUtils.getAllReminderTimes();
            boolean[] eventReminderBooleans = new boolean[eventReminders.size()];
            int i = 0;
            for (EventReminder eventReminder : eventReminders) {
                eventReminderBooleans[i++] = eventReminder.isIsSet();
            }
            builder.setMultiChoiceItems(reminderTimes, eventReminderBooleans, this);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.show();
        } catch (RealmException e) {
            Crashlytics.logException(e);
        }

    }
    // endregion
}
