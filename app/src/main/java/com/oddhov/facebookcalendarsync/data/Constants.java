package com.oddhov.facebookcalendarsync.data;

import android.provider.CalendarContract;

public final class Constants {
    // Accounts
    public static final String ACCOUNT_TYPE = "com.oddhov.facebookcalendarsync";
    public static final String ACCOUNT_NAME = "FacebookCalendarSync";

    // Permissions
    public static final int REQUEST_ACCOUNTS_PERMISSION = 952;
    public static final int REQUEST_READ_WRITE_CALENDAR_PERMISSION = 9583;
    public static final int REQUEST_APP_SETTINGS = 1563;

    // Request codes
    public static final int LOGIN_ACTIVITY_REQUEST = 846;

    //Bundle values
    public static final String SYNC_ONLY_UPCOMING_EVENTS = "syc_only_upcoming_events";

    // Authtoken type string.
    public static final String AUTHTOKEN_TYPE = "com.oddhov.facebookcalendarsynctoken";

    // Calendar related constants
    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };
    public static final int PROJECTION_ID_INDEX = 0;
    public static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    public static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    public static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
}
