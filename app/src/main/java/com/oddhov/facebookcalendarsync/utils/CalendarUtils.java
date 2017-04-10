package com.oddhov.facebookcalendarsync.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.oddhov.facebookcalendarsync.R;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.data.realm_models.RealmCalendarEvent;

import java.util.ArrayList;
import java.util.List;

public class CalendarUtils {
    private Context mContext;
    private NotificationUtils mNotificationUtils;

    public CalendarUtils(Context context, NotificationUtils notificationUtils) {
        this.mContext = context;
        this.mNotificationUtils = notificationUtils;
    }

    public long ensureCalendarExists() {
        long calendarId = getCalendarId();
        if (calendarId == 0) {
            calendarId = createCalendar();
        }

        return calendarId;
    }

    public long getCalendarId() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            mNotificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_missing_permissions_message_short,
                    R.string.notification_missing_permissions_message_long);

            Log.e("CalendarUtils", "No calendar permissions granted");
            return 0;
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
                int calID = cursor.getInt(Constants.GET_CALENDAR_PROJECTION_ID_INDEX);
                cursor.close();
                return calID;
            }
            cursor.close();
        }
        return 0;
    }

    public void updateCalendarEvents(long calendarId, List<RealmCalendarEvent> realmCalendarEventsList) {
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
            if (TextUtils.isEmpty(event.getName()) || event.getStartTime() == null
                    || event.getEndTime() == null) {
                // TODO improve this
                continue;
            }

            boolean eventExists = doesEventExist(event.getId(), String.valueOf(calendarId));
            if (eventExists) {
                Log.i("CalendarUtils", "Updating event: " + event.getName());
                updateEvent(event);
            } else {
                Log.i("CalendarUtils", "Adding event: " + event.getName());

                ContentValues contentValues = new ContentValues();
                contentValues.put(CalendarContract.Events._ID, event.getId());
                contentValues.put(CalendarContract.Events.TITLE, event.getName());
                contentValues.put(CalendarContract.Events.DESCRIPTION, event.getDescription());
                contentValues.put(CalendarContract.Events.DTSTART, EventUtils.convertDateToEpochFormat(event.getStartTime()));
                contentValues.put(CalendarContract.Events.DTEND, EventUtils.convertDateToEpochFormat(event.getEndTime()));
                contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, "NL");
                contentValues.put(CalendarContract.Events.CALENDAR_ID, calendarId);
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

    private long createCalendar() {
        Uri calendarUri = Uri.parse(CalendarContract.Calendars.CONTENT_URI.toString());
        calendarUri = calendarUri.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, Constants.ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, Constants.ACCOUNT_TYPE).build();

        ContentValues vals = new ContentValues();
        vals.put(CalendarContract.Calendars.ACCOUNT_NAME, Constants.ACCOUNT_NAME);
        vals.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        vals.put(CalendarContract.Calendars.NAME, Constants.ACCOUNT_NAME);
        vals.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, Constants.ACCOUNT_NAME);
        vals.put(CalendarContract.Calendars.CALENDAR_COLOR, 0xffff0000);
        vals.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        vals.put(CalendarContract.Calendars.SYNC_EVENTS, 1);

        Uri newCalendarUri = mContext.getContentResolver().insert(calendarUri, vals);
        return ContentUris.parseId(newCalendarUri);
    }

    public void removeEventsFromCalendar(int calId) {
        Uri eventsContentUri = Uri.parse("content://com.android.calendar/events");
        String where = "calendar_id=?";
        String[] selectionArgs = new String[]{Integer.toString(calId)};
        mContext.getContentResolver().delete(eventsContentUri, where, selectionArgs);
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

    private void updateEvent(RealmCalendarEvent event) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            mNotificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_missing_permissions_message_short,
                    R.string.notification_missing_permissions_message_long);

            Log.e("CalendarUtils", "No calendar permissions granted");
            return;
        }

        Uri uri = CalendarContract.Events.CONTENT_URI;
        String selection = "(" + CalendarContract.Events._ID + " = ?)";
        String[] selectionArgs = new String[]{event.getId()};
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Events.TITLE, event.getName());
        contentValues.put(CalendarContract.Events.DESCRIPTION, event.getDescription());
        contentValues.put(CalendarContract.Events.DTSTART, EventUtils.convertDateToEpochFormat(event.getStartTime()));
        contentValues.put(CalendarContract.Events.DTEND, EventUtils.convertDateToEpochFormat(event.getEndTime()));
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, "NL"); // TODO

        ContentResolver contentResolver = mContext.getContentResolver();
        contentResolver.update(uri, contentValues, selection, selectionArgs);
    }
}
