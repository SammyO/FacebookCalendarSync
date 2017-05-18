package com.oddhov.facebookcalendarsync.data;

import android.provider.CalendarContract;

public final class Constants {
    // Sync interval
    public static final long SECONDS_IN_HOUR = 3600L;

    // Accounts
    public static final String ACCOUNT_TYPE = "com.oddhov.facebookcalendarsync";
    public static final String ACCOUNT_NAME = "FacebookCalendarSync";

    // Permissions
    public static final int REQUEST_READ_WRITE_CALENDAR_GET_ACCOUNT_PERMISSIONS = 9583;
    public static final int REQUEST_APP_SETTINGS = 1563;

    // Calendar related constants
    public static final String[] GET_CALENDAR_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,
    };
    public static final int GET_CALENDAR_PROJECTION_ID_INDEX = 0;
    public static final String[] GET_EVENT_PROJECTION = new String[]{
            CalendarContract.Events._ID,
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.TITLE

    };
    public static final int GET_EVENT_PROJECTION_ID_INDEX = 0;

    // Notification ID
    public static final int NOTIFICATION_ID = 176;

    // Intent constants
    public static final String SYNC_ADAPTER_RAN_EXTRA = "SYNC_ADAPTER_RAN_EXTRA";
    public static final String SETTINGS_SCREEN = "SETTINGS_SCREEN";

    // Navigation drawer items
    public static final int STOP_START_SYNC = 1;
    public static final int LOG_IN_OUT = 2;
    public static final int REPORT_BUG = 3;
    public static final int FACEBOOK_SETTINGS = 4;
    public static final int LOCAL_CALENDAR_SETTINGS = 5;
    public static final int SYNC_SETTINGS = 6;
}
