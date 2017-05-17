package com.oddhov.facebookcalendarsync.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.oddhov.facebookcalendarsync.data.exceptions.RealmException;
import com.oddhov.facebookcalendarsync.data.models.Event;
import com.oddhov.facebookcalendarsync.data.models.realm_models.EventReminder;
import com.oddhov.facebookcalendarsync.data.models.realm_models.RealmCalendarEvent;
import com.oddhov.facebookcalendarsync.data.models.realm_models.UserData;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

public class DatabaseUtils {
    private Context mContext;
    private Realm mRealm;

    public DatabaseUtils(Context context) {
        this.mContext = context;
        this.mRealm = Realm.getDefaultInstance();
    }

    public void openRealm() {
        if (!mRealm.isClosed()) {
            return;
        }
        mRealm = null;
        mRealm = Realm.getDefaultInstance();
    }

    public void closeRealm() {
        if (!mRealm.isClosed()) {
            mRealm.close();
        }
    }

    public void setupUserData() throws RealmException {
        openRealm();
        if (getUserData() == null) {
            final UserData userdata = new UserData();

            final RealmList<EventReminder> eventReminders = new RealmList<>();
            eventReminders.add(new EventReminder(false, 30));
            eventReminders.add(new EventReminder(false, 60));
            eventReminders.add(new EventReminder(true, 120));
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

    public int getSyncInterval() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        int syncInterval = getUserData().getSyncInterval();
        closeRealm();
        Log.e("DatabaseUtils", "getSyncInterval: " + syncInterval);
        return syncInterval;
    }

    public void setSyncInterval(final int syncInterval) throws RealmException {
        Log.e("DatabaseUtils", "setSyncInterval: " + syncInterval);
        mRealm = Realm.getDefaultInstance();
        final UserData userData = getUserData();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userData.setSyncInterval(syncInterval);
            }
        });
        closeRealm();
    }

    public boolean getSyncBirthdays() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        boolean syncBirthdays = getUserData().getSyncBirthdays();
        closeRealm();
        Log.e("DatabaseUtils", "getSyncBirthdays: " + syncBirthdays);
        return syncBirthdays;
    }

    public void setSyncBirthdays(final boolean syncBirthdays) throws RealmException {
        Log.e("DatabaseUtils", "setSyncBirthdays: " + syncBirthdays);
        mRealm = Realm.getDefaultInstance();
        final UserData userData = getUserData();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userData.setSyncBirthdays(syncBirthdays);
            }
        });
        closeRealm();
    }

    public boolean getShowLinks() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        boolean showLinks = getUserData().getShowLinks();
        closeRealm();
        Log.e("DatabaseUtils", "getShowLinks: " + showLinks);
        return showLinks;
    }

    public void setShowLinks(final boolean showLinks) throws RealmException {
        Log.e("DatabaseUtils", "setShowLinks: " + showLinks);
        mRealm = Realm.getDefaultInstance();
        final UserData userData = getUserData();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userData.setShowLinks(showLinks);
            }
        });
        closeRealm();
    }

    public int getSyncRange() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        int syncRange = getUserData().getSyncRange();
        closeRealm();
        Log.e("DatabaseUtils", "getSyncRange: " + syncRange);
        return syncRange;
    }

    public void setSyncRange(final int syncRange) throws RealmException {
        Log.e("DatabaseUtils", "setSyncRange: " + syncRange);
        mRealm = Realm.getDefaultInstance();
        final UserData userData = getUserData();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userData.setSyncRange(syncRange);
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


    public int getCalendarColor() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        int calendarColor = getUserData().getCalendarColor();
        closeRealm();
        Log.e("DatabaseUtils", "getCalendarColor: " + calendarColor);
        return calendarColor;
    }

    public void setCalendarColor(final int calendarColor) throws RealmException {
        Log.e("DatabaseUtils", "setCalendarColor: " + calendarColor);
        mRealm = Realm.getDefaultInstance();
        final UserData userData = getUserData();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userData.setCalendarColor(calendarColor);
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
        List<RealmCalendarEvent> updatedEvents = realm.copyToRealmOrUpdate(realmCalendarEventsList);
        realm.commitTransaction();
        realm.close(); //TODO
        if (!updatedEvents.isEmpty()) {
            return updatedEvents;
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
