package com.oddhov.facebookcalendarsync.utils;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.oddhov.facebookcalendarsync.R;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.data.models.Event;
import com.oddhov.facebookcalendarsync.data.models.EventsResponse;

import java.util.ArrayList;

public class CalendarUtils {
    private Context mContext;
    private NotificationUtils mNotificationUtils;

    public CalendarUtils(Context context, NotificationUtils notificationUtils) {
        this.mContext = context;
        this.mNotificationUtils = notificationUtils;
    }

    public Integer checkDoesCalendarExistAndGetCalId() {
        Cursor cur;
        ContentResolver cr = mContext.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";
        String[] selectionArgs = new String[]{Constants.ACCOUNT_NAME, CalendarContract.ACCOUNT_TYPE_LOCAL};

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            mNotificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_missing_permissions_message_short,
                    R.string.notification_missing_permissions_message_long);

            Log.e("CalendarUtils", "No calendar permissions granted");
            return null;
        }
        cur = cr.query(uri, Constants.EVENT_PROJECTION, selection, selectionArgs, null);

        if (cur != null) {
            if (cur.moveToNext()) {
                int calID = 0;
                String displayName = null;
                String accountName = null;
                String ownerName = null;

                // Get the field values
                calID = cur.getInt(Constants.PROJECTION_ID_INDEX);
                displayName = cur.getString(Constants.PROJECTION_DISPLAY_NAME_INDEX);
                accountName = cur.getString(Constants.PROJECTION_ACCOUNT_NAME_INDEX);
                cur.close();
                return calID;
            }
            cur.close();
        }
        return null;
    }

    public void addEventsToCalendar(EventsResponse eventsResponse, int calId) {
        String eventTitle;
        Long eventDtStart; // The time the event starts in UTC millis since epoch. Column name. Type: INTEGER (long; millis since epoch)
        Long eventDtEnd; // The time the event ends in UTC millis since epoch. Column name. Type: INTEGER (long; millis since epoch)
        String eventTimeZone; // The timezone for the event. Column name. Type: TEXT
        String eventCalID; // The _ID of the calendar the event belongs to. Column name. Type: INTEGER

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        for (Event event : eventsResponse.getEvents()) {
            if (TextUtils.isEmpty(event.getName()) || event.getStartTime() == null
                    || event.getEndTime() == null) {
                // TODO improve this
                continue;
            }
            eventTitle = event.getName();
            eventDtStart = EventUtils.convertDateToEpochFormat(event.getStartTime());
            eventDtEnd = EventUtils.convertDateToEpochFormat(event.getEndTime());

            ops.add(
                    ContentProviderOperation.newInsert(CalendarContract.Events.CONTENT_URI)
                            .withValue(CalendarContract.Events.TITLE, eventTitle)
                            .withValue(CalendarContract.Events.DTSTART, eventDtStart)
                            .withValue(CalendarContract.Events.DTEND, eventDtEnd)
                            .withValue(CalendarContract.Events.EVENT_TIMEZONE, "NL") // TODO
                            .withValue(CalendarContract.Events.CALENDAR_ID, calId)
                            .withYieldAllowed(true)
                            .build());
        }
        try {
            mContext.getContentResolver().applyBatch(CalendarContract.AUTHORITY, ops);
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    public Uri createCalendar() {
        Uri calendarUri = Uri.parse(CalendarContract.Calendars.CONTENT_URI.toString());
        calendarUri = calendarUri.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, Constants.ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, Constants.ACCOUNT_TYPE).build();


        ContentValues vals = new ContentValues();
        vals.put(CalendarContract.Calendars.ACCOUNT_NAME, Constants.ACCOUNT_NAME);
        vals.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL); // TODO look into this
        vals.put(CalendarContract.Calendars.NAME, Constants.ACCOUNT_NAME);
        vals.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, Constants.ACCOUNT_NAME);
        vals.put(CalendarContract.Calendars.CALENDAR_COLOR, 0xffff0000);
        vals.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        vals.put(CalendarContract.Calendars.SYNC_EVENTS, 1);

        return mContext.getContentResolver().insert(calendarUri, vals);
    }

    public void removeEventsFromCalendar(int calId) {
        Uri eventsContentUri = Uri.parse("content://com.android.calendar/events");
        String where = "calendar_id=?";
        String[] selectionArgs = new String[]{Integer.toString(calId)};
        mContext.getContentResolver().delete(eventsContentUri, where, selectionArgs);
    }
}
