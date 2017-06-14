package com.oddhov.facebookcalendarsync.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.oddhov.facebookcalendarsync.data.exceptions.RealmException;
import com.oddhov.facebookcalendarsync.data.models.CalendarColour;
import com.oddhov.facebookcalendarsync.data.models.CustomTime;
import com.oddhov.facebookcalendarsync.data.models.Event;
import com.oddhov.facebookcalendarsync.data.models.RsvpStatus;
import com.oddhov.facebookcalendarsync.data.models.realm_models.EventReminder;
import com.oddhov.facebookcalendarsync.data.models.realm_models.RealmCalendarEvent;
import com.oddhov.facebookcalendarsync.data.models.realm_models.RealmRsvpSyncPreference;
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
            eventReminders.add(new EventReminder(false, CustomTime.HALF_HOUR));
            eventReminders.add(new EventReminder(false, CustomTime.ONE_HOUR));
            eventReminders.add(new EventReminder(false, CustomTime.TWO_HOURS));
            eventReminders.add(new EventReminder(false, CustomTime.SIX_HOURS));
            eventReminders.add(new EventReminder(false, CustomTime.TWELVE_HOURS));
            eventReminders.add(new EventReminder(false, CustomTime.TWENTY_FOUR_HOURS));
            userdata.setEventReminders(eventReminders);

            final RealmList<RealmRsvpSyncPreference> rsvpSyncPreferences = new RealmList<>();
            rsvpSyncPreferences.add(new RealmRsvpSyncPreference(true, RsvpStatus.ATTENDING));
            rsvpSyncPreferences.add(new RealmRsvpSyncPreference(true, RsvpStatus.MAYBE));
            rsvpSyncPreferences.add(new RealmRsvpSyncPreference(true, RsvpStatus.NOT_REPLIED));
            rsvpSyncPreferences.add(new RealmRsvpSyncPreference(false, RsvpStatus.DECLINED));
            userdata.setRsvpSyncPreferences(rsvpSyncPreferences);

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
        boolean isPaused = getUserData().isSyncAdapterPaused();
        closeRealm();
        return isPaused;
    }

    public void setSyncAdapterPaused(final boolean paused) throws RealmException {
        mRealm = Realm.getDefaultInstance();
        final UserData userData = getUserData();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userData.setSyncAdapterPaused(paused);
            }
        });
        closeRealm();
    }

    public boolean getSyncWifiOnly() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        boolean wifiOnly = getUserData().isSyncWifiOnly();
        closeRealm();
        return wifiOnly;
    }

    public void setSyncWifiOnly(final boolean wifiOnly) throws RealmException {
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
        return showNotifications;
    }

    public void setShowNotifications(final boolean showNotifications) throws RealmException {
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
        return syncInterval;
    }

    public void setSyncInterval(final CustomTime syncInterval) throws RealmException {
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

    public boolean isSyncOnlyUpcoming() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        boolean syncRange = getUserData().isSyncOnlyUpcoming();
        closeRealm();
        return syncRange;
    }

    public void setSyncOnlyUpcoming(final boolean syncRange) throws RealmException {
        Log.e("DatabaseUtils", "setSyncRange: " + syncRange);
        mRealm = Realm.getDefaultInstance();
        final UserData userData = getUserData();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userData.setSyncOnlyUpcoming(syncRange);
            }
        });
        closeRealm();
    }

    public boolean getShowLinks() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        boolean showLinks = getUserData().getShowLinks();
        closeRealm();
        return showLinks;
    }

    public void setShowLinks(final boolean showLinks) throws RealmException {
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

    public List<RealmRsvpSyncPreference> getRsvpSyncPreferences() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        List<RealmRsvpSyncPreference> rsvpStatusesCopy;
        RealmList<RealmRsvpSyncPreference> rsvpStatuses = getUserData().getRsvpSyncPreferences();
        rsvpStatusesCopy = mRealm.copyFromRealm(rsvpStatuses);
        closeRealm();
        return rsvpStatusesCopy;
    }

    public void setRsvpSyncPreference(final int position, final boolean isSet) throws RealmException {
        mRealm = Realm.getDefaultInstance();
        final UserData userData = getUserData();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userData.getRsvpSyncPreferences().get(position).setIsSet(isSet);
            }
        });
        closeRealm();
    }

    public boolean getShowReminders() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        boolean showReminders = getUserData().getShowReminders();
        closeRealm();
        return showReminders;
    }

    public void setShowReminders(final boolean showReminders) throws RealmException {
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

    public List<EventReminder> getAllReminderTimes() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        List<EventReminder> remindersCopy;
        RealmList<EventReminder> reminders = getUserData().getEventReminders();
        remindersCopy = mRealm.copyFromRealm(reminders);
        closeRealm();
        return remindersCopy;
    }

    public void setReminderTime(final int position, final boolean isSet) throws RealmException {
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
        return calendarColor;
    }

    public void setCalendarColor(final CalendarColour calendarColor) throws RealmException {
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
