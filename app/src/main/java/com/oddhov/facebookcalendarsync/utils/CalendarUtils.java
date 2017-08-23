package com.oddhov.facebookcalendarsync.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.oddhov.facebookcalendarsync.R;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.data.models.realm_models.EventReminder;
import com.oddhov.facebookcalendarsync.data.models.realm_models.RealmCalendarEvent;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CalendarUtils {
    private Context mContext;
    private TimeUtils mTimeUtils;
    private NotificationUtils mNotificationUtils;
    private DatabaseUtils mDatabaseUtils;
    private ColorUtils mColorUtils;

    public CalendarUtils(Context context, NotificationUtils notificationUtils, DatabaseUtils databaseUtils,
                         TimeUtils timeUtils, ColorUtils colorUtils) {
        this.mContext = context;
        this.mNotificationUtils = notificationUtils;
        this.mDatabaseUtils = databaseUtils;
        this.mTimeUtils = timeUtils;
        this.mColorUtils = colorUtils;
    }

    public String ensureCalendarExists() {
        String calendarId = getCalendarId();
        if (calendarId.equals("")) {
            calendarId = createCalendar();
        }

        return calendarId;
    }

    public String getCalendarId() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            mNotificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_missing_permissions_message_short,
                    R.string.notification_missing_permissions_message_long);

            Log.e("CalendarUtils", "No calendar permissions granted");
            return "";
        }

        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" +
                CalendarContract.Calendars.ACCOUNT_NAME + " = ?)" +
                " AND (" +
                CalendarContract.Calendars.ACCOUNT_TYPE + " = ?)" +
                ")";

        String[] selectionArgs = new String[]{Constants.ACCOUNT_NAME, CalendarContract.ACCOUNT_TYPE_LOCAL};

        Cursor cursor = contentResolver.query(uri, Constants.GET_CALENDAR_PROJECTION, selection, selectionArgs, null);

        if (cursor != null) {
            if (cursor.moveToNext()) {
                // Get the field values
                String id = cursor.getString(Constants.GET_CALENDAR_PROJECTION_ID_INDEX);
                cursor.close();
                return id;
            }
            cursor.close();
        }
        return "";
    }

    public void insertCalendarEvents(String calendarId, List<RealmCalendarEvent> realmCalendarEventsList) {
        if (realmCalendarEventsList.size() == 0) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            mNotificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_missing_permissions_message_short,
                    R.string.notification_missing_permissions_message_long);
            return;
        }

        ArrayList<ContentValues> contentValuesList = new ArrayList<>();
        ContentValues[] bulkToInsert;
        for (int i = 0; i < realmCalendarEventsList.size(); i++) {
            RealmCalendarEvent event = realmCalendarEventsList.get(i);
            if (TextUtils.isEmpty(event.getName()) || event.getStartTime() == null) {
                continue;
            }

            if (event.getEndTime() == null) {
                try {
                    mDatabaseUtils.setEventEndTime(event, TimeUtils.addOneHourToTimeStamp(event.getStartTime()));
                } catch (ParseException e) {
                    // TODO add logging to local file
                    continue;
                }
            }

            boolean eventExists = doesEventExist(event.getId(), calendarId);
            if (eventExists) {
                Log.e("CalendarUtils", "Event with ID " + event.getId() + " exists already. Skipping.");
            } else {
                int eventId = Math.abs(String.valueOf(event.getId()).hashCode());

                ContentValues contentValues = new ContentValues();
                contentValues.put(CalendarContract.Events._ID, eventId);
                contentValues.put(CalendarContract.Events.TITLE, event.getName());
                contentValues.put(CalendarContract.Events.DESCRIPTION, getEventDescription(event));
                contentValues.put(CalendarContract.Events.DTSTART, mTimeUtils.convertDateToEpochFormat(
                        event.getStartTime()));
                contentValues.put(CalendarContract.Events.DTEND, mTimeUtils.convertDateToEpochFormat(
                        event.getEndTime()));
                contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, "NL");
                contentValues.put(CalendarContract.Events.CALENDAR_ID, calendarId);

                if (mDatabaseUtils.getShowReminders()) {
                    contentValues.put(CalendarContract.Events.HAS_ALARM, true);

                    for (EventReminder eventReminder : mDatabaseUtils.getAllReminderTimes()) {
                        if (eventReminder.isIsSet()) {
                            ContentValues contentValuesReminders = new ContentValues();
                            contentValuesReminders.put(CalendarContract.Reminders.EVENT_ID, eventId);
                            contentValuesReminders.put(CalendarContract.Reminders.METHOD,
                                    CalendarContract.Reminders.METHOD_ALERT);
                            contentValuesReminders.put(CalendarContract.Reminders.MINUTES,
                                    eventReminder.getEnum().getTimeInMinutes());
                            mContext.getContentResolver().insert(CalendarContract.Reminders.CONTENT_URI,
                                    contentValuesReminders);
                        }
                    }
                }

                contentValuesList.add(contentValues);

                if (contentValuesList.size() >= 10) {
                    bulkToInsert = new ContentValues[contentValuesList.size()];
                    contentValuesList.toArray(bulkToInsert);
                    mContext.getContentResolver().bulkInsert(CalendarContract.Events.CONTENT_URI, bulkToInsert);
                    contentValuesList.clear();
                }
            }
        }
        bulkToInsert = new ContentValues[contentValuesList.size()];
        contentValuesList.toArray(bulkToInsert);
        mContext.getContentResolver().bulkInsert(CalendarContract.Events.CONTENT_URI, bulkToInsert);
    }

    public int deleteCalendar() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            mNotificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_missing_permissions_message_short,
                    R.string.notification_missing_permissions_message_long);

            Log.e("CalendarUtils", "No calendar permissions granted");
            return 0;
        }
        Uri calendarUri = CalendarContract.Calendars.CONTENT_URI;
        Uri uri = ContentUris.withAppendedId(calendarUri, Long.valueOf(getCalendarId()));
        ContentResolver contentResolver = mContext.getContentResolver();
        return contentResolver.delete(uri, null, null);
    }

    private String createCalendar() {
        Uri calendarUri = Uri.parse(CalendarContract.Calendars.CONTENT_URI.toString());
        calendarUri = calendarUri.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, Constants.ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, Constants.ACCOUNT_TYPE).build();

        ContentValues vals = new ContentValues();
        vals.put(CalendarContract.Calendars.ACCOUNT_NAME, Constants.ACCOUNT_NAME);
        vals.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        vals.put(CalendarContract.Calendars.NAME, Constants.ACCOUNT_NAME);
        vals.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, Constants.ACCOUNT_NAME);
        vals.put(CalendarContract.Calendars.CALENDAR_COLOR,
                Color.parseColor(mColorUtils.getHexValueForColor(mDatabaseUtils.getCalendarColor())));
        vals.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        vals.put(CalendarContract.Calendars.SYNC_EVENTS, 1);

        Uri newCalendarUri = mContext.getContentResolver().insert(calendarUri, vals);
        return String.valueOf(ContentUris.parseId(newCalendarUri));
    }

    public void removeEventsFromCalendar(String calendarId) {
        if (!TextUtils.isEmpty(calendarId)) {
            Uri eventsContentUri = Uri.parse("content://com.android.calendar/events");
            String where = "calendar_id=?";
            String[] selectionArgs = new String[]{calendarId};
            mContext.getContentResolver().delete(eventsContentUri, where, selectionArgs);
        }
    }

    private boolean doesEventExist(String eventId, String calendarId) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            mNotificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_missing_permissions_message_short,
                    R.string.notification_missing_permissions_message_long);

            Log.e("CalendarUtils", "No calendar permissions granted");
            return false;
        }

        Uri uri = CalendarContract.Events.CONTENT_URI;
        String selection = "((" +
                CalendarContract.Events._ID + " = ?)" +
                " AND (" +
                CalendarContract.Events.CALENDAR_ID + " = ?)" +
                ")";
        String[] selectionArgs = new String[]{eventId, calendarId};
        String[] projection = new String[]{CalendarContract.Events._ID};
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    private String getEventDescription(RealmCalendarEvent event) {
        if (mDatabaseUtils.getShowLinks()) {
            return String.format("%s\n\nRSVP status: %s\n\nFacebook event link: www.facebook.com/%s",
                    TextUtils.isEmpty(event.getDescription()) ? "" : event.getDescription(),
                    event.getRsvpStatus().getDisplayString(),
                    event.getId());
        } else {
            return String.format("%s\n\nRSVP status: %s",
                    TextUtils.isEmpty(event.getDescription()) ? "" : event.getDescription(),
                    event.getRsvpStatus().getDisplayString());
        }
    }
}
