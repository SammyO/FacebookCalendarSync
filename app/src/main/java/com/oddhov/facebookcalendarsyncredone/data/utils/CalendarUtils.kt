package com.oddhov.facebookcalendarsyncredone.data.utils

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.provider.CalendarContract
import android.support.v4.app.ActivityCompat
import android.text.TextUtils
import android.util.Log

import com.crashlytics.android.Crashlytics
import com.oddhov.facebookcalendarsync.R
import com.oddhov.facebookcalendarsync.data.Constants
import com.oddhov.facebookcalendarsync.data.exceptions.RealmException
import com.oddhov.facebookcalendarsync.data.models.realm_models.RealmCalendarEvent
import com.oddhov.facebookcalendarsync.utils.ColorUtils
import com.oddhov.facebookcalendarsync.utils.DatabaseUtils
import com.oddhov.facebookcalendarsync.utils.NotificationUtils
import com.oddhov.facebookcalendarsync.utils.TimeUtils

import java.text.ParseException
import java.util.ArrayList
import java.util.HashSet

class CalendarUtils(private val context: Context, private val notificationUtils: NotificationUtils,
                    private val databaseUtils: DatabaseUtils, private val timeUtils: TimeUtils,
                    private val colorUtils: ColorUtils) {

    // Get the field values
    val calendarId: String
        get() {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                notificationUtils.sendNotification(
                        R.string.notification_syncing_problem_title,
                        R.string.notification_missing_permissions_message_short,
                        R.string.notification_missing_permissions_message_long)

                Log.e("CalendarUtils", "No calendar permissions granted")
                return ""
            }

            val contentResolver = context.contentResolver
            val uri = CalendarContract.Calendars.CONTENT_URI
            val selection = "((" +
                    CalendarContract.Calendars.ACCOUNT_NAME + " = ?)" +
                    " AND (" +
                    CalendarContract.Calendars.ACCOUNT_TYPE + " = ?)" +
                    ")"

            val selectionArgs = arrayOf(Constants.ACCOUNT_NAME, CalendarContract.ACCOUNT_TYPE_LOCAL)

            val cursor = contentResolver.query(uri, Constants.GET_CALENDAR_PROJECTION, selection, selectionArgs, null)

            if (cursor != null) {
                if (cursor.moveToNext()) {
                    val id = cursor.getString(Constants.GET_CALENDAR_PROJECTION_ID_INDEX)
                    cursor.close()
                    return id
                }
                cursor.close()
            }
            return ""
        }

    fun ensureCalendarExists(): String? {
        var calendarId: String? = calendarId
        if (calendarId == "") {
            calendarId = createCalendar()
        }

        return calendarId
    }

    fun insertOrUpdateCalendarEvents(calendarId: String, realmCalendarEventsList: List<RealmCalendarEvent>) {
        if (realmCalendarEventsList.size == 0) {
            return
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            notificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_missing_permissions_message_short,
                    R.string.notification_missing_permissions_message_long)
            return
        }

        val contentValuesList = ArrayList<ContentValues>()
        var bulkToInsert: Array<ContentValues?>
        for (i in realmCalendarEventsList.indices) {
            val event = realmCalendarEventsList[i]
            if (TextUtils.isEmpty(event.name) || event.startTime == null) {
                continue
            }

            if (event.endTime == null) {
                try {
                    databaseUtils.setEventEndTime(event, timeUtils.addOneHourToTimeStamp(event.startTime))
                } catch (e: ParseException) {
                    continue
                }

            }

            val eventExists = doesEventExist(event.id, calendarId)
            if (eventExists) {
                updateEvent(event)
            } else {
                val contentValues = ContentValues()
                contentValues.put(CalendarContract.Events._ID, event.id)
                contentValues.put(CalendarContract.Events.TITLE, event.name)
                contentValues.put(CalendarContract.Events.DESCRIPTION, event.description)
                contentValues.put(CalendarContract.Events.DTSTART, timeUtils.convertDateToEpochFormat(event.startTime))
                contentValues.put(CalendarContract.Events.DTEND, timeUtils.convertDateToEpochFormat(event.endTime))
                contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, "NL")
                contentValues.put(CalendarContract.Events.CALENDAR_ID, calendarId)
                contentValuesList.add(contentValues)

                if (contentValuesList.size >= 10) {
                    bulkToInsert = arrayOfNulls(contentValuesList.size)
                    contentValuesList.toTypedArray()
                    context.contentResolver.bulkInsert(CalendarContract.Events.CONTENT_URI, bulkToInsert)
                    contentValuesList.clear()
                }
            }
        }
        bulkToInsert = arrayOfNulls(contentValuesList.size)
        contentValuesList.toTypedArray()
        context.contentResolver.bulkInsert(CalendarContract.Events.CONTENT_URI, bulkToInsert)
    }

    fun deleteMissingCalendarEvents(calendarId: String, serverEvents: List<RealmCalendarEvent>) {
        if (serverEvents.size == 0) {
            return
        }

        val serverEventIds = HashSet<String>()
        for (calendarEvent in serverEvents) {
            serverEventIds.add(calendarEvent.id)
        }

        val localEvents = getCalendarEventIds(calendarId)
        for (localId in localEvents) {
            if (!serverEventIds.contains(localId)) {
                removeEventFromCalendar(calendarId, localId)
            }
        }
    }

    fun deleteCalendar(): Int {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            notificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_missing_permissions_message_short,
                    R.string.notification_missing_permissions_message_long)

            Log.e("CalendarUtils", "No calendar permissions granted")
            return 0
        }
        val calendarUri = CalendarContract.Calendars.CONTENT_URI
        val uri = ContentUris.withAppendedId(calendarUri, java.lang.Long.valueOf(calendarId)!!)
        val contentResolver = context.contentResolver
        return contentResolver.delete(uri, null, null)
    }

    private fun createCalendar(): String? {
        try {
            var calendarUri = Uri.parse(CalendarContract.Calendars.CONTENT_URI.toString())
            calendarUri = calendarUri.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, Constants.ACCOUNT_NAME)
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, Constants.ACCOUNT_TYPE).build()

            val vals = ContentValues()
            vals.put(CalendarContract.Calendars.ACCOUNT_NAME, Constants.ACCOUNT_NAME)
            vals.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
            vals.put(CalendarContract.Calendars.NAME, Constants.ACCOUNT_NAME)
            vals.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, Constants.ACCOUNT_NAME)
            vals.put(CalendarContract.Calendars.CALENDAR_COLOR,
                    Color.parseColor(colorUtils.getHexValueForColor(databaseUtils.calendarColor)))
            vals.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER)
            vals.put(CalendarContract.Calendars.SYNC_EVENTS, 1)

            val newCalendarUri = context.contentResolver.insert(calendarUri, vals)
            return ContentUris.parseId(newCalendarUri).toString()
        } catch (e: RealmException) {
            Crashlytics.logException(e)
        }

        return null
    }

    private fun removeEventFromCalendar(calendarId: String, eventId: String): Int {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            notificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_missing_permissions_message_short,
                    R.string.notification_missing_permissions_message_long)

            Log.e("CalendarUtils", "No calendar permissions granted")
            return 0
        }

        val uri = CalendarContract.Events.CONTENT_URI
        val selection = "((" +
                CalendarContract.Events._ID + " = ?)" +
                " AND (" +
                CalendarContract.Events.CALENDAR_ID + " = ?)" +
                ")"
        val selectionArgs = arrayOf(eventId, calendarId)
        val contentResolver = context.contentResolver
        return contentResolver.delete(uri, selection, selectionArgs)
    }

    private fun doesEventExist(eventId: String, calendarId: String): Boolean {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            notificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_missing_permissions_message_short,
                    R.string.notification_missing_permissions_message_long)

            Log.e("CalendarUtils", "No calendar permissions granted")
            return false
        }

        val uri = CalendarContract.Events.CONTENT_URI
        val selection = "((" +
                CalendarContract.Events._ID + " = ?)" +
                " AND (" +
                CalendarContract.Events.CALENDAR_ID + " = ?)" +
                ")"
        val selectionArgs = arrayOf(eventId, calendarId)
        val projection = arrayOf(CalendarContract.Events._ID)
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        if (cursor != null) {
            if (cursor.moveToNext()) {
                cursor.close()
                return true
            }
            cursor.close()
        }
        return false
    }

    private fun updateEvent(event: RealmCalendarEvent) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            notificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_missing_permissions_message_short,
                    R.string.notification_missing_permissions_message_long)

            Log.e("CalendarUtils", "No calendar permissions granted")
            return
        }

        val uri = CalendarContract.Events.CONTENT_URI
        val selection = "(" + CalendarContract.Events._ID + " = ?)"
        val selectionArgs = arrayOf(event.id)
        val contentValues = ContentValues()
        contentValues.put(CalendarContract.Events.TITLE, event.name)
        contentValues.put(CalendarContract.Events.DESCRIPTION, event.description)
        contentValues.put(CalendarContract.Events.DTSTART, timeUtils.convertDateToEpochFormat(event.startTime))
        contentValues.put(CalendarContract.Events.DTEND, timeUtils.convertDateToEpochFormat(event.endTime))
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, "NL") // TODO

        val contentResolver = context.contentResolver
        contentResolver.update(uri, contentValues, selection, selectionArgs)
    }

    private fun getCalendarEventIds(calendarId: String): List<String> {
        val eventIds = ArrayList<String>()

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            notificationUtils.sendNotification(
                    R.string.notification_syncing_problem_title,
                    R.string.notification_missing_permissions_message_short,
                    R.string.notification_missing_permissions_message_long)

            Log.e("CalendarUtils", "No calendar permissions granted")
            return eventIds
        }

        val uri = CalendarContract.Events.CONTENT_URI
        val selection = "(" + CalendarContract.Events.CALENDAR_ID + " = ?)"
        val selectionArgs = arrayOf(calendarId)
        val projection = arrayOf(CalendarContract.Events._ID, CalendarContract.Events.TITLE)
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                eventIds.add(cursor.getString(0))
            }
            cursor.close()
        }
        return eventIds
    }
}
