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
import com.oddhov.facebookcalendarsync.utils.SyncAdapterUtils;

import io.realm.RealmList;

public class LocalCalendarSettingsFragment extends Fragment implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, DialogInterface.OnMultiChoiceClickListener {

    // region Fields
    public static final String TAG = "LocalCalendarSettingsFragment";

    private DatabaseUtils mDatabaseUtils;
    private SyncAdapterUtils mSyncAdapterUtils;

    private SwitchCompat swReminders;
    private LinearLayout llSetupReminderTime;
    private RadioGroup rgCalendarColor;
    private RadioButton rbCalendarColorRed;
    private RadioButton rbCalendarColorGreen;
    private RadioButton rbCalendarColorOrange;
    private RadioButton rbCalendarColorPurple;
    private RadioButton rbCalendarColorBlue;
    // endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDatabaseUtils = new DatabaseUtils(getActivity());
        mSyncAdapterUtils = new SyncAdapterUtils();

        View view = inflater.inflate(R.layout.fragment_local_calendar_settings, container, false);
        setupViews(view);
        return view;
    }

    // region Interface View.OnClickListener
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.swReminders:
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
                break;
            case R.id.llSetupReminderTime:
                showReminderTimesDialog();
            default:
                break;
        }
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
            swReminders = (SwitchCompat) view.findViewById(R.id.swReminders);
            swReminders.setChecked(mDatabaseUtils.getShowReminders());
            swReminders.setOnClickListener(this);

            llSetupReminderTime = (LinearLayout) view.findViewById(R.id.llSetupReminderTime);
            if (mDatabaseUtils.getShowReminders()) {
                llSetupReminderTime.setVisibility(View.VISIBLE);
            } else {
                llSetupReminderTime.setVisibility(View.GONE);
            }
            llSetupReminderTime.setOnClickListener(this);

            rbCalendarColorRed = (RadioButton) view.findViewById(R.id.rbCalendarColorRed);
            rbCalendarColorGreen = (RadioButton) view.findViewById(R.id.rbCalendarColorGreen);
            rbCalendarColorOrange = (RadioButton) view.findViewById(R.id.rbCalendarColorOrange);
            rbCalendarColorPurple = (RadioButton) view.findViewById(R.id.rbCalendarColorPurple);
            rbCalendarColorBlue = (RadioButton) view.findViewById(R.id.rbCalendarColorBlue);

            rgCalendarColor = (RadioGroup) view.findViewById(R.id.rgCalendarColor);
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
