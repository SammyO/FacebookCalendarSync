package com.oddhov.facebookcalendarsyncredone.data.utils

import android.content.Context
import android.util.Log
import com.oddhov.facebookcalendarsync.data.exceptions.RealmException
import com.oddhov.facebookcalendarsync.data.models.CalendarColour
import com.oddhov.facebookcalendarsync.data.models.CustomTime
import com.oddhov.facebookcalendarsync.data.models.Event
import com.oddhov.facebookcalendarsync.data.models.SyncRange
import com.oddhov.facebookcalendarsync.data.models.realm_models.EventReminder
import com.oddhov.facebookcalendarsync.data.models.realm_models.RealmCalendarEvent
import com.oddhov.facebookcalendarsync.data.models.realm_models.UserData
import io.realm.Realm
import io.realm.RealmList
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

class DatabaseUtils
@Inject
constructor(context: Context) {
    private var mRealm: Realm? = null

    val lastSynced: Long?
        get() {
            mRealm = Realm.getDefaultInstance()
            val lastSyncedTimeStamp = userData!!.lastSyncedTimeStamp
            closeRealm()
            return lastSyncedTimeStamp
        }

    var syncAdapterPaused: Boolean
        get() {
            mRealm = Realm.getDefaultInstance()
            val isPaused = userData!!.isIsSyncAdapterPaused
            closeRealm()
            return isPaused
        }
        @Throws(RealmException::class)
        set(paused) {
            mRealm = Realm.getDefaultInstance()
            val userData = userData
            mRealm!!.executeTransaction { userData!!.isIsSyncAdapterPaused = paused }
            closeRealm()
        }

    var syncWifiOnly: Boolean
        get() {
            mRealm = Realm.getDefaultInstance()
            val wifiOnly = userData!!.isSyncWifiOnly
            closeRealm()
            Log.e("DatabaseUtils", "getSyncWifiOnly: " + wifiOnly)
            return wifiOnly
        }
        @Throws(RealmException::class)
        set(wifiOnly) {
            Log.e("DatabaseUtils", "setSyncWifiOnly: " + wifiOnly)
            mRealm = Realm.getDefaultInstance()
            val userData = userData
            mRealm!!.executeTransaction { userData!!.isSyncWifiOnly = wifiOnly }
            closeRealm()
        }

    var showNotifications: Boolean
        get() {
            mRealm = Realm.getDefaultInstance()
            val showNotifications = userData!!.isShowNotifications
            closeRealm()
            Log.e("DatabaseUtils", "getShowNotifications: " + showNotifications)
            return showNotifications
        }
        @Throws(RealmException::class)
        set(showNotifications) {
            Log.e("DatabaseUtils", "setShowNotifications: " + showNotifications)
            mRealm = Realm.getDefaultInstance()
            val userData = userData
            mRealm!!.executeTransaction { userData!!.isShowNotifications = showNotifications }
            closeRealm()
        }

    var syncInterval: CustomTime
        get() {
            mRealm = Realm.getDefaultInstance()
            val syncInterval = CustomTime.values()[userData!!.syncInterval]
            closeRealm()
            Log.e("DatabaseUtils", "getSyncInterval: " + syncInterval)
            return syncInterval
        }
        @Throws(RealmException::class)
        set(syncInterval) {
            Log.e("DatabaseUtils", "setSyncInterval: " + syncInterval)
            mRealm = Realm.getDefaultInstance()
            val userData = userData
            mRealm!!.executeTransaction { userData!!.syncInterval = syncInterval.ordinal }
            closeRealm()
        }

    var syncBirthdays: Boolean
        get() {
            mRealm = Realm.getDefaultInstance()
            val syncBirthdays = userData!!.syncBirthdays
            closeRealm()
            Log.e("DatabaseUtils", "getSyncBirthdays: " + syncBirthdays)
            return syncBirthdays
        }
        @Throws(RealmException::class)
        set(syncBirthdays) {
            Log.e("DatabaseUtils", "setSyncBirthdays: " + syncBirthdays)
            mRealm = Realm.getDefaultInstance()
            val userData = userData
            mRealm!!.executeTransaction { userData!!.syncBirthdays = syncBirthdays }
            closeRealm()
        }

    var showLinks: Boolean
        get() {
            mRealm = Realm.getDefaultInstance()
            val showLinks = userData!!.showLinks
            closeRealm()
            Log.e("DatabaseUtils", "getShowLinks: " + showLinks)
            return showLinks
        }
        @Throws(RealmException::class)
        set(showLinks) {
            Log.e("DatabaseUtils", "setShowLinks: " + showLinks)
            mRealm = Realm.getDefaultInstance()
            val userData = userData
            mRealm!!.executeTransaction { userData!!.showLinks = showLinks }
            closeRealm()
        }

    var syncRange: SyncRange
        get() {
            mRealm = Realm.getDefaultInstance()
            val syncRange = SyncRange.values()[userData!!.syncRange]
            closeRealm()
            Log.e("DatabaseUtils", "getSyncRange: " + syncRange)
            return syncRange
        }
        @Throws(RealmException::class)
        set(syncRange) {
            Log.e("DatabaseUtils", "setSyncRange: " + syncRange)
            mRealm = Realm.getDefaultInstance()
            val userData = userData
            mRealm!!.executeTransaction { userData!!.syncRange = syncRange.ordinal }
            closeRealm()
        }

    var showReminders: Boolean
        get() {
            mRealm = Realm.getDefaultInstance()
            val showReminders = userData!!.showReminders
            closeRealm()
            Log.e("DatabaseUtils", "getShowReminders: " + showReminders)
            return showReminders
        }
        set(showReminders) {
            Log.e("DatabaseUtils", "setShowReminders: " + showReminders)
            mRealm = Realm.getDefaultInstance()
            val userData = userData
            mRealm!!.executeTransaction { userData!!.showReminders = showReminders }
            closeRealm()
        }

    val allReminderTimes: RealmList<EventReminder>
        get() {
            mRealm = Realm.getDefaultInstance()
            val reminders = userData!!.eventReminders
            closeRealm()
            Log.e("DatabaseUtils", "getAllReminderTimes: " + reminders.toString())
            return reminders
        }

    var calendarColor: CalendarColour
        get() {
            mRealm = Realm.getDefaultInstance()
            val calendarColor = CalendarColour.values()[userData!!.calendarColor]
            closeRealm()
            Log.e("DatabaseUtils", "getCalendarColor: " + calendarColor)
            return calendarColor
        }
        set(calendarColor) {
            Log.e("DatabaseUtils", "setCalendarColor: " + calendarColor)
            mRealm = Realm.getDefaultInstance()
            val userData = userData
            mRealm!!.executeTransaction { userData!!.calendarColor = calendarColor.ordinal }
            closeRealm()
        }

    val eventsSize: Long
        get() {
            val realm = Realm.getDefaultInstance()
            val size = realm.where(RealmCalendarEvent::class.java).count()
            realm.close()
            return size
        }

    val calendarEvents: List<RealmCalendarEvent>
        get() {
            val realm = Realm.getDefaultInstance()
            val events = realm.where(RealmCalendarEvent::class.java).findAll()
            realm.close()
            return events
        }

    private val userData: UserData?
        get() {
            val instances = mRealm!!.where(UserData::class.java).count()
            return if (instances <= 1) {
                mRealm!!.where(UserData::class.java).findFirst()
            } else {
                throw RealmException("More than one UserData instance")
            }
        }

    fun closeRealm() {
        if (!mRealm!!.isClosed) {
            mRealm!!.close()
        }
    }

    fun ensureUserDataIsSetup() {
        mRealm = Realm.getDefaultInstance()
        if (userData == null) {
            val userdata = UserData()

            val eventReminders = RealmList<EventReminder>()
            eventReminders.add(EventReminder(false, 30))
            eventReminders.add(EventReminder(false, 60))
            eventReminders.add(EventReminder(false, 120))
            eventReminders.add(EventReminder(false, 360))
            eventReminders.add(EventReminder(false, 720))
            eventReminders.add(EventReminder(false, 1440))

            userdata.eventReminders = eventReminders

            mRealm!!.executeTransaction { mRealm!!.copyToRealm(userdata) }
        }
        closeRealm()
    }

    fun setLastSynced(timeStamp: Long) {
        mRealm = Realm.getDefaultInstance()
        val userData = userData
        mRealm!!.executeTransaction { userData!!.lastSyncedTimeStamp = timeStamp }
        closeRealm()
    }

    fun setReminderTime(position: Int, isSet: Boolean) {
        mRealm = Realm.getDefaultInstance()
        val userData = userData
        mRealm!!.executeTransaction { userData!!.eventReminders[position].isIsSet = isSet }
        closeRealm()
    }

    fun updateCalendarEvents(realmCalendarEventsList: List<RealmCalendarEvent>): List<RealmCalendarEvent>? {
        if (realmCalendarEventsList.isEmpty()) {
            return null
        }

        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val updatedEventsCopy: List<RealmCalendarEvent>
        val updatedEvents = realm.copyToRealmOrUpdate(realmCalendarEventsList)
        updatedEventsCopy = realm.copyFromRealm(updatedEvents)
        realm.commitTransaction()
        realm.close() //TODO
        return if (!updatedEvents.isEmpty()) {
            updatedEventsCopy
        } else null
    }

    fun convertToRealmCalendarEvents(events: List<Event>?): List<RealmCalendarEvent> {
        val realmCalendarEvents = ArrayList<RealmCalendarEvent>()

        if (events != null && events.isNotEmpty()) {
            for (event in events) {
                val realmCalendarEvent = RealmCalendarEvent(
                        event.id,
                        event.name,
                        event.description,
                        event.startTime,
                        event.endTime,
                        event.rsvpStatus
                )
                realmCalendarEvents.add(realmCalendarEvent)
            }
        }
        return realmCalendarEvents
    }

    fun setEventEndTime(event: RealmCalendarEvent, endTime: String) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { event.endTime = endTime }
        realm.close()
    }
}
