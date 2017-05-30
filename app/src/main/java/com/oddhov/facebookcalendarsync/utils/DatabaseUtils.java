package com.oddhov.facebookcalendarsync.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.oddhov.facebookcalendarsync.data.exceptions.RealmException;
import com.oddhov.facebookcalendarsync.data.models.CalendarColour;
import com.oddhov.facebookcalendarsync.data.models.CustomTime;
import com.oddhov.facebookcalendarsync.data.models.Event;
import com.oddhov.facebookcalendarsync.data.models.SyncRange;
import com.oddhov.facebookcalendarsync.data.models.realm_models.EventReminder;
import com.oddhov.facebookcalendarsync.data.models.realm_models.RealmCalendarEvent;
import com.oddhov.facebookcalendarsync.data.models.realm_models.UserData;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

public class DatabaseUtils {
    private Realm mRealm;

    public DatabaseUtils(Context context) {
    }

    public void closeRealm() {
        if (!mRealm.isClosed()) {
            mRealm.close();
        }
    }

    public void ensureUserDataIsSetup() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        if (getUserData() == null) {
            final UserData userdata = new UserData();

            final RealmList<EventReminder> eventReminders = new RealmList<>();
            eventReminders.add(new EventReminder(false, 30));
            eventReminders.add(new EventReminder(false, 60));
            eventReminders.add(new EventReminder(false, 120));
            eventReminders.add(new EventReminder(false, 360));
            eventReminders.add(new EventReminder(false, 720));
            eventReminders.add(new EventReminder(false, 1440));

            userdata.setEventReminders(eventReminders);

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    mRealm.copyToRealm(userdata);
                }
            });
        }
        closeRealm();
    }

    public Long getLastSynced() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        Long lastSyncedTimeStamp = getUserData().getLastSyncedTimeStamp();
        closeRealm();
        return lastSyncedTimeStamp;
    }

    public void setLastSynced(final long timeStamp) throws RealmException {
        mRealm = Realm.getDefaultInstance();
        final UserData userData = getUserData();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userData.setLastSyncedTimeStamp(timeStamp);
            }
        });
        closeRealm();
    }

    public boolean getSyncAdapterPaused() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        boolean isPaused = getUserData().isIsSyncAdapterPaused();
        closeRealm();
        return isPaused;
    }

    public void setSyncAdapterPaused(final boolean paused) throws RealmException {
        mRealm = Realm.getDefaultInstance();
        final UserData userData = getUserData();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userData.setIsSyncAdapterPaused(paused);
            }
        });
        closeRealm();
    }

    public boolean getSyncWifiOnly() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        boolean wifiOnly = getUserData().isSyncWifiOnly();
        closeRealm();
        Log.e("DatabaseUtils", "getSyncWifiOnly: " + wifiOnly);
        return wifiOnly;
    }

    public void setSyncWifiOnly(final boolean wifiOnly) throws RealmException {
        Log.e("DatabaseUtils", "setSyncWifiOnly: " + wifiOnly);
        mRealm = Realm.getDefaultInstance();
        final UserData userData = getUserData();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userData.setSyncWifiOnly(wifiOnly);
            }
        });
        closeRealm();
    }

    public boolean getShowNotifications() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        boolean showNotifications = getUserData().isShowNotifications();
        closeRealm();
        Log.e("DatabaseUtils", "getShowNotifications: " + showNotifications);
        return showNotifications;
    }

    public void setShowNotifications(final boolean showNotifications) throws RealmException {
        Log.e("DatabaseUtils", "setShowNotifications: " + showNotifications);
        mRealm = Realm.getDefaultInstance();
        final UserData userData = getUserData();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userData.setShowNotifications(showNotifications);
            }
        });
        closeRealm();
    }

    public CustomTime getSyncInterval() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        CustomTime syncInterval = CustomTime.values()[getUserData().getSyncInterval()];
        closeRealm();
        Log.e("DatabaseUtils", "getSyncInterval: " + syncInterval);
        return syncInterval;
    }

    public void setSyncInterval(final CustomTime syncInterval) throws RealmException {
        Log.e("DatabaseUtils", "setSyncInterval: " + syncInterval);
        mRealm = Realm.getDefaultInstance();
        final UserData userData = getUserData();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userData.setSyncInterval(syncInterval.ordinal());
            }
        });
        closeRealm();
    }

    public SyncRange getSyncRange() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        SyncRange syncRange = SyncRange.values()[getUserData().getSyncRange()];
        closeRealm();
        Log.e("DatabaseUtils", "getSyncRange: " + syncRange);
        return syncRange;
    }

    public void setSyncRange(final SyncRange syncRange) throws RealmException {
        Log.e("DatabaseUtils", "setSyncRange: " + syncRange);
        mRealm = Realm.getDefaultInstance();
        final UserData userData = getUserData();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userData.setSyncRange(syncRange.ordinal());
            }
        });
        closeRealm();
    }

    public boolean getShowReminders() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        boolean showReminders = getUserData().getShowReminders();
        closeRealm();
        Log.e("DatabaseUtils", "getShowReminders: " + showReminders);
        return showReminders;
    }

    public void setShowReminders(final boolean showReminders) throws RealmException {
        Log.e("DatabaseUtils", "setShowReminders: " + showReminders);
        mRealm = Realm.getDefaultInstance();
        final UserData userData = getUserData();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userData.setShowReminders(showReminders);
            }
        });
        closeRealm();
    }

    public RealmList<EventReminder> getAllReminderTimes() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        RealmList<EventReminder> reminders = getUserData().getEventReminders();
        closeRealm();
        Log.e("DatabaseUtils", "getAllReminderTimes: " + reminders.toString());
        return reminders;
    }

    public void setReminderTime(final int position, final boolean isSet) throws RealmException {
        Log.e("DatabaseUtils", "setReminderTIme: " + position + " " + isSet);
        mRealm = Realm.getDefaultInstance();
        final UserData userData = getUserData();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userData.getEventReminders().get(position).setIsSet(isSet);
            }
        });
        closeRealm();
    }

    public CalendarColour getCalendarColor() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        CalendarColour calendarColor = CalendarColour.values()[getUserData().getCalendarColor()];
        closeRealm();
        Log.e("DatabaseUtils", "getCalendarColor: " + calendarColor);
        return calendarColor;
    }

    public void setCalendarColor(final CalendarColour calendarColor) throws RealmException {
        Log.e("DatabaseUtils", "setCalendarColor: " + calendarColor);
        mRealm = Realm.getDefaultInstance();
        final UserData userData = getUserData();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userData.setCalendarColor(calendarColor.ordinal());
            }
        });
        closeRealm();
    }

    @Nullable
    public List<RealmCalendarEvent> updateCalendarEvents(List<RealmCalendarEvent> realmCalendarEventsList) {
        if (realmCalendarEventsList.isEmpty()) {
            return null;
        }

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        List<RealmCalendarEvent> updatedEventsCopy;
        List<RealmCalendarEvent> updatedEvents = realm.copyToRealmOrUpdate(realmCalendarEventsList);
        updatedEventsCopy = realm.copyFromRealm(updatedEvents);
        realm.commitTransaction();
        realm.close(); //TODO
        if (!updatedEvents.isEmpty()) {
            return updatedEventsCopy;
        }
        return null;
    }

    public List<RealmCalendarEvent> convertToRealmCalendarEvents(List<Event> events) {
        ArrayList<RealmCalendarEvent> realmCalendarEvents = new ArrayList<>();

        if (events != null && events.size() != 0) {
            for (Event event : events) {
                RealmCalendarEvent realmCalendarEvent = new RealmCalendarEvent(
                        event.getId(),
                        event.getName(),
                        event.getDescription(),
                        event.getStartTime(),
                        event.getEndTime(),
                        event.getRsvpStatus()
                );
                realmCalendarEvents.add(realmCalendarEvent);
            }
        }
        return realmCalendarEvents;
    }

    public void setEventEndTime(final RealmCalendarEvent event, final String endTime) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                event.setEndTime(endTime);
            }
        });
        realm.close();
    }

    public long getEventsSize() {
        Realm realm = Realm.getDefaultInstance();
        long size = realm.where(RealmCalendarEvent.class).count();
        realm.close();
        return size;
    }

    public List<RealmCalendarEvent> getCalendarEvents() {
        Realm realm = Realm.getDefaultInstance();
        List<RealmCalendarEvent> events = realm.where(RealmCalendarEvent.class).findAll();
        realm.close();
        return events;
    }

    private UserData getUserData() throws RealmException {
        long instances = mRealm.where(UserData.class).count();
        if (instances <= 1) {
            return mRealm.where(UserData.class).findFirst();
        } else {
            throw new RealmException("More than one UserData instance");
        }
    }
}
